package nameserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import nameserver.meta.File;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.HeartbeatTask;
import nameserver.task.TaskFactory;
import common.network.ServerConnector;
import common.call.Call;
import common.call.CallListener;
import common.call.all.AbortCall;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.Task;
import common.task.TaskMonitor;
import common.util.Configuration;
import common.util.Logger;

/**
 * Name server implementation.
 * <p>
 * It is responsible for:
 * <p>
 * 1. Manage meta data, the directory structure.
 * <p>
 * 2. Manage status data, the storage server status.
 * <p>
 * <strong>Warning:</strong> Name server will never send call to anyone
 * initially, it only reply other's call.
 * 
 * @author lishunyang
 * 
 */
public class NameServer
    implements TaskEventListener, CallListener
{
    /**
     * Logger.
     */
    private final static Logger logger = Logger.getLogger(NameServer.class);

    /**
     * Maximum number of task which can be running simultaneously.
     */
    private final static int MAX_THREADS = 20;

    /**
     * Time period of making snapshot.(second)
     */
    private final static long SNAPSHOT_PERIOD = 10;

    /**
     * Server connector, used to send/receive call to/from client and storage
     * server.
     */
    private ServerConnector connector = null;

    /**
     * Task list.
     * <p>
     * {taskId, task}
     */
    private Map<Long, Task> tasks = new HashMap<Long, Task>();

    /**
     * Task monitor, used to check task status.
     */
    private TaskMonitor taskMonitor = null;

    /**
     * Task thread executor.
     */
    private ExecutorService taskExecutor = null;

    /**
     * Snapshot maker thread executor.
     */
    private ScheduledExecutorService snapshotExecutor = null;

    /**
     * When name server is pausing, it won't response for any new call.
     */
    private Lock pauseLock = null;

    /**
     * Determine whether name server has initiated.
     */
    private boolean initialized = false;

    /**
     * Construction method.
     */
    public NameServer()
    {
    }

    /**
     * Initializing method. Do some initializing job and check name server
     * status.
     * <p>
     * <strong>Warning:</strong> It MUST be called before using
     * <tt>NameServer</tt>.
     * 
     * @throws Exception
     */
    public void initilize() throws Exception
    {
        if (initialized)
        {
            logger
                .warn("NameServer has been initialized before, you can't do it twice.");
            return;
        }
        else
        {
            // Check configuration.
            if (null == Configuration.getInstance())
            {
                throw new Exception(
                    "Initiation failed, couldn't load configuration file.");
            }

            // Check backup util.
            if (null == BackupUtil.getInstance())
            {
                throw new Exception(
                    "Initiation failed, couldn't create backup directory.");
            }

            // Check connector.
            if (null == ServerConnector.getInstance())
            {
                throw new Exception(
                    "Initiation failed, couldn't create server connector.");
            }

            pauseLock = new ReentrantLock();

            taskExecutor = Executors.newFixedThreadPool(MAX_THREADS);

            snapshotExecutor = Executors.newSingleThreadScheduledExecutor();
            snapshotExecutor.scheduleAtFixedRate(new SnapshotMaker(),
                SNAPSHOT_PERIOD, SNAPSHOT_PERIOD, TimeUnit.SECONDS);

            taskMonitor = new TaskMonitor();
            taskMonitor.addListener(this);

            connector = ServerConnector.getInstance();
            connector.addListener(this);

            logger.info("NameServer initialization finished.");

            initialized = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleCall(Call call)
    {
        logger.info("NameServer received a call: " + call.getType());

        Task task = null;

        if (isNewCall(call))
        {
            boolean permitted = pauseLock.tryLock();

            try
            {
                if (!permitted)
                {
                    sendAbortCall(call,
                        "Nameserver is maintaining, please try later.");
                }
                else
                {
                    task = TaskFactory.createTask(call);
                    tasks.put(task.getTaskId(), task);
                    taskExecutor.execute(task);
                    taskMonitor.addTask(task);
                }
            }
            finally
            {
                if (permitted)
                    pauseLock.unlock();
            }
        }
        else
        {
            task = getRelatedTask(call.getToTaskId());

            if (null != task)
            {
                tasks.get(call.getToTaskId()).handleCall(call);
            }
            else
            {
                // Shall we send an abort call? Maybe not.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void handle(TaskEvent event)
    {
        final Task task = event.getTaskThread();

        tasks.remove(task);

        if (event.getType() == TaskEvent.Type.TASK_DUE)
        {
            task.release();
            logger.info("Task: " + task.getTaskId() + " " + event.getType());
        }
        else if (event.getType() == TaskEvent.Type.TASK_FINISHED)
        {
            logger.info("Task: " + task.getTaskId() + " " + event.getType());
        }
        else if (event.getType() == TaskEvent.Type.HEARTBEAT_FATAL)
        {
            handleHeartbeatFatal(event);
        }
    }

    /**
     * Handle heartbeat event, which means some storage server has dead.
     * 
     * @param event
     */
    private synchronized void handleHeartbeatFatal(TaskEvent event)
    {
        Storage storage =
            ((HeartbeatTask) (event.getTaskThread())).getStorage();
        Status.getInstance().removeStorage(storage);

        // Remove files' location.
        List<File> files = storage.getFiles();
        for (File file : files)
        {
            file.removeLocations(storage);
        }

        List<Storage> storages = Status.getInstance().getStorages();
        if (0 == storages.size())
        {
            logger
                .fatal("Failed to migrate data, no active storage server was found.");
            return;
        }

        // Allocate migration work.
        Iterator<Storage> iter = storages.iterator();
        for (File f : files)
        {
            // Refresh the iterator.
            if (!iter.hasNext())
                iter = storages.iterator();

            Storage active = iter.next();
            active.addMigrateFile(f.getLocations().get(0), f);
        }
    }

    /**
     * Test whether the coming call is new one.
     * 
     * @param call
     * @return
     */
    private synchronized boolean isNewCall(Call call)
    {
        return call.getToTaskId() < 0;
    }

    /**
     * Get task with specified task id.
     * 
     * @param tid
     * @return
     */
    private synchronized Task getRelatedTask(long tid)
    {
        return tasks.get(tid);
    }

    private void sendAbortCall(Call call, String reason)
    {
        final long localTaskId = call.getToTaskId();
        final long remoteTaskId = call.getFromTaskId();

        Call back = new AbortCall(reason);

        back.setFromTaskId(localTaskId);
        back.setToTaskId(remoteTaskId);
        back.setInitiator(call.getInitiator());
        connector.sendCall(back);
    }

    private class SnapshotMaker
        implements Runnable
    {
        @Override
        public void run()
        {
            makeSnapshot();
        }

        /**
         * Make an image snapshot.
         */
        private void makeSnapshot()
        {
            pauseLock.lock();

            try
            {
                final BackupUtil backup = BackupUtil.getInstance();

                boolean hasRunningTask = !tasks.isEmpty();

                while (hasRunningTask)
                {
                    try
                    {
                        TimeUnit.SECONDS.sleep(1);
                        hasRunningTask = !tasks.isEmpty();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

                backup.writeBackupImage();
                backup.readBackupLog();
            }
            finally
            {
                pauseLock.unlock();
            }
        }
    }
}

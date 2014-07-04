package nameserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import nameserver.meta.File;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.HeartbeatTask;
import nameserver.task.TaskFactory;
import common.network.ServerConnector;
import common.call.Call;
import common.call.CallListener;
import common.call.n2c.AbortCallN2C;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.TaskExecutor;
import common.task.Task;
import common.task.TaskMonitor;
import common.util.Logger;

// TODO: Snapshot hasn't been added.
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
    private static final Logger logger = Logger.getLogger(NameServer.class);

    /**
     * Task monitor, used to check task status.
     */
    private TaskMonitor monitor = new TaskMonitor();

    /**
     * Server connector, used to send/receive call to/from client and storage
     * server.
     */
    private ServerConnector connector = ServerConnector.getInstance();

    /**
     * Task list.
     * <p>
     * {taskId, task}
     */
    private Map<Long, Task> tasks = new HashMap<Long, Task>();

    /**
     * Task executor.
     */
    private TaskExecutor executor = new TaskExecutor();

    /**
     * When name server is pausing, it won't response for any new call.
     */
    private boolean pause = false;

    /**
     * Determine whether name server has initiated.
     */
    private boolean initialized = false;

    /**
     * Construction method.
     */
    public NameServer()
    {
        monitor.addListener(this);
        connector.addListener(this);
    }

    /**
     * Initializing method. Do some initializing job and check name server
     * status.
     * <p>
     * <strong>Warning:</strong> It MUST be called before using
     * <tt>NameServer</tt>.
     */
    public void initilize()
    {
        if (initialized)
        {
            logger
                .warn("NameServer has been initialized before, you can't do it twice.");
        }
        else
        {
            // TODO
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void handleCall(Call call)
    {
        logger.info("NameServer received a call: " + call.getType());

        Task task = null;
        long localTaskId = call.getToTaskId();
        long remoteTaskId = call.getFromTaskId();

        // TODO: It seems wired, I should refactor it.
        if (!isNewCall(call))
        {
            if (isTaskExisted(call.getToTaskId()))
            {
                tasks.get(call.getToTaskId()).handleCall(call);
            }
            else
            {
                // Should we send an abort call? Maybe not.
            }
        }
        else
        {
            if (pause)
            {
                Call back =
                    new AbortCallN2C(
                        "Nameserver is maintaining, please try later.");
                back.setFromTaskId(localTaskId);
                back.setToTaskId(remoteTaskId);
                connector.sendCall(back);
                return;
            }

            task = TaskFactory.createTask(call);
            tasks.put(task.getTaskId(), task);
            executor.executeTask(task);
            monitor.addTask(task);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(TaskEvent event)
    {
        Task task = event.getTaskThread();
        tasks.remove(task);

        if (event.getType() == TaskEvent.Type.TASK_ABORTED)
        {
            task.release();
            logger.fatal("Task: " + task.getTaskId() + " " + event.getType());
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
    private void handleHeartbeatFatal(TaskEvent event)
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
    private boolean isNewCall(Call call)
    {
        return call.getToTaskId() < 0;
    }

    /**
     * Test whether the specified task exists.
     * 
     * @param tid
     * @return
     */
    private boolean isTaskExisted(long tid)
    {
        return tasks.containsKey(tid);
    }

    /**
     * Make an image snapshot.
     */
    private synchronized void makeSnapshot()
    {
        pause = true;
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

        pause = false;
    }
}

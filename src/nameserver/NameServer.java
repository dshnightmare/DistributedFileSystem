package nameserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import nameserver.meta.File;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.AddFileTask;
import nameserver.task.AppendFileTask;
import nameserver.task.HeartbeatTask;
import nameserver.task.MoveFileTask;
import nameserver.task.RemoveFileTask;
import nameserver.task.SyncTask;
import nameserver.task.TaskFactory;
import common.network.ServerConnector;
import common.call.Call;
import common.call.CallListener;
import common.call.n2c.AbortCallN2C;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.TaskExecutor;
import common.task.TaskLease;
import common.task.Task;
import common.task.TaskMonitor;
import common.util.Configuration;
import common.util.IdGenerator;
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
    private static NameServer instance = null;

    private static final Logger logger = Logger.getLogger(NameServer.class);

    private TaskMonitor monitor = new TaskMonitor();

    private ServerConnector connector = ServerConnector.getInstance();

    private Map<Long, Task> tasks = new HashMap<Long, Task>();

    private TaskExecutor executor = new TaskExecutor();

    private boolean pause = false;

    private boolean initialized = false;

    private NameServer()
    {
        monitor.addListener(this);
        connector.addListener(this);
    }

    public void initilize()
    {
        if (initialized)
        {
            logger
                .error("NameServer has been initialized before, you can't do it twice.");
        }
        else
        {
            // TODO
        }
    }

    public synchronized static NameServer getInstance()
    {
        if (null == instance)
            instance = new NameServer();

        return instance;
    }

    @Override
    public synchronized void handleCall(Call call)
    {
        logger.info("NameServer received a call: " + call.getType());

        Task task = null;
        long localTaskId = call.getToTaskId();
        long remoteTaskId = call.getFromTaskId();

        if (!isNewCall(call))
        {
            if (taskExisted(call.getToTaskId()))
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
            monitor.monitor(task);
        }
    }

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

    private boolean isNewCall(Call call)
    {
        return call.getToTaskId() < 0;
    }

    private boolean taskExisted(long tid)
    {
        return tasks.containsKey(tid);
    }

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

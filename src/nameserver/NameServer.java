package nameserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nameserver.meta.File;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.AddFileTask;
import nameserver.task.AppendFileTask;
import nameserver.task.HeartbeatTask;
import nameserver.task.MoveFileTask;
import nameserver.task.RemoveFileTask;
import nameserver.task.SyncTask;
import common.network.ServerConnector;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskLease;
import common.thread.TaskThread;
import common.thread.TaskThreadMonitor;
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
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(NameServer.class);

    /**
     * The task thread monitor, it will check lease validation of task threads
     * regularly, notify the listeners if someone is dead.
     */
    private TaskThreadMonitor taskMonitor;

    private ServerConnector connector = new ServerConnector();

    private Map<Long, TaskThread> tasks = new HashMap<Long, TaskThread>();

    public void init()
    {
        taskMonitor = TaskThreadMonitor.getInstance();
        taskMonitor.addListener(this);
        connector.start();
    }

    @Override
    public void handleCall(Call call)
    {
        TaskThread task = null;
        long tid = call.getTaskId();
        Configuration conf = Configuration.getInstance();

        if (tid >= 0)
        {
            // Should we send a abort call? Maybe not.
            if (tasks.containsKey(tid))
                tasks.get(tid).handleCall(call);
        }
        else
        {

            tid = IdGenerator.getInstance().getLongId();

            if (Call.Type.ADD_FILE_C2N == call.getType())
            {
                task = new AddFileTask(tid, call, connector);
                task.setLease(new TaskLease(conf
                    .getLong(Configuration.LEASE_PERIOD_KEY)));
            }
            else if (Call.Type.APPEND_FILE_C2N == call.getType())
            {
                task = new AppendFileTask(tid, call, connector);
                task.setLease(new TaskLease(conf
                    .getLong(Configuration.LEASE_PERIOD_KEY)));
            }
            else if (Call.Type.MOVE_FILE_C2N == call.getType())
            {
                task = new MoveFileTask(tid, call, connector);
                task.setLease(new TaskLease(conf
                    .getLong(Configuration.LEASE_PERIOD_KEY)));
            }
            else if (Call.Type.REGISTRATION_S2N == call.getType())
            {
                // Heartbeat task doesn't need lease.
                task =
                    new HeartbeatTask(tid, call, connector,
                        conf.getLong(Configuration.HEARTBEAT_INTERVAL_KEY));
            }
            else if (Call.Type.REMOVE_FILE_C2N == call.getType())
            {
                task = new RemoveFileTask(tid, call, connector);
                task.setLease(new TaskLease(conf
                    .getLong(Configuration.LEASE_PERIOD_KEY)));
            }
            else if (Call.Type.SYNC_S2N == call.getType())
            {
                task = new SyncTask(tid, call, connector);
                task.setLease(new TaskLease(conf
                    .getLong(Configuration.LEASE_PERIOD_KEY)));
            }

            synchronized (tasks)
            {
                tasks.put(tid, task);
            }
        }
    }

    @Override
    public void handle(TaskEvent event)
    {
        TaskThread task = event.getTaskThread();

        if (event.getType() == TaskEvent.Type.TASK_ABORTED)
        {
            task.release();
            logger.fatal("Task: " + task.getTaskId() + " " + event.getType());
        }
        else if (event.getType() == TaskEvent.Type.TASK_FINISHED)
        {
            task.release();
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

}

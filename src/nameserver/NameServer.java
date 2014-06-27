package nameserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nameserver.heartbeat.HeartbeatEvent;
import nameserver.heartbeat.HeartbeatListener;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.AddFileTask;
import nameserver.task.AppendFileTask;
import nameserver.task.MoveFileTask;
import nameserver.task.RegisterStorageTask;
import nameserver.task.RemoveFileTask;
import nameserver.task.SyncTask;
import common.network.ServerConnector;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.MigrateFileCallN2S;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThread;
import common.thread.TaskThreadMonitor;
import common.util.Configuration;
import common.util.Constant;
import common.util.IdGenerator;
import common.util.Logger;

public class NameServer
    implements TaskEventListener, HeartbeatListener, CallListener
{

    private static final Logger logger = Logger.getLogger(NameServer.class);

    private TaskThreadMonitor taskMonitor;

    private Meta meta = new Meta();

    private Status status = new Status();

    private ServerConnector connector = new ServerConnector();

    private Map<Long, TaskThread> tasks = new HashMap<Long, TaskThread>();

    public void init()
    {
        Configuration conf = Configuration.getInstance();
        taskMonitor =
            new TaskThreadMonitor(
                conf.getLong(Constant.TASK_CHECK_INTERVAL_KEY) * 1000);
        taskMonitor.addListener(this);
        connector.start();
    }

    @Override
    public void handleCall(Call call)
    {
        TaskThread task = null;
        long tid = call.getTaskId();

        if (tid >= 0)
        {
            tasks.get(tid).handleCall(call);
            return;
        }

        tid = IdGenerator.getInstance().getLongId();

        if (Call.Type.ADD_FILE_C2N == call.getType())
        {
            task = new AddFileTask(tid, call, meta, status, connector);
        }
        else if (Call.Type.APPEND_FILE_C2N == call.getType())
        {
            task = new AppendFileTask(tid, call, meta, connector);
        }
        else if (Call.Type.MOVE_FILE_C2N == call.getType())
        {
            task = new MoveFileTask(tid, call, meta, connector);
        }
        else if (Call.Type.REGISTRATION_S2N == call.getType())
        {
            task = new RegisterStorageTask(tid, call, status, connector);
        }
        else if (Call.Type.REMOVE_FILE_C2N == call.getType())
        {
            task = new RemoveFileTask(tid, call, meta, connector);
        }
        else if (Call.Type.SYNC_S2N == call.getType())
        {
            task = new SyncTask(tid, call, meta, status, connector);
        }
        else if (Call.Type.HEARTBEAT_S2N == call.getType())
        {
            
        }

        task.addListener(this);
        synchronized (tasks)
        {
            tasks.put(tid, task);
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
    }

    @Override
    public void handleHeatbeatEvent(HeartbeatEvent event)
    {
        Storage storage = event.getStorage();
        status.removeStorage(storage);

        List<File> files = storage.getFiles();
        for (File file : files)
        {
            file.removeLocations(storage);
        }

        List<Storage> storages = status.getStorages();
        if (0 == storages.size())
        {
            logger
                .fatal("Failed to migrate data, no active storage server was found.");
            return;
        }

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

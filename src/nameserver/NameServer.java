package nameserver;

import java.io.IOException;

import nameserver.heartbeat.CardiacArrest;
import nameserver.heartbeat.CardiacArrestListener;
import nameserver.heartbeat.CardiacArrestMonitor;
import nameserver.meta.DirectoryTree;
import nameserver.task.TaskFactory;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThread;
import common.thread.TaskThreadMonitor;
import common.util.Configuration;
import common.util.Constant;
import common.util.Logger;

public class NameServer
    implements TaskEventListener, CardiacArrestListener, CallListener
{

    private static final Logger logger = Logger.getLogger(NameServer.class);

    private CardiacArrestMonitor cardiacArrestMonitor;

    private TaskThreadMonitor taskMonitor;

    private TaskFactory taskFactory;

    private DirectoryTree directory = new DirectoryTree();

    public void init()
    {
        try
        {
            Configuration conf = Configuration.getInstance();
            cardiacArrestMonitor =
                new CardiacArrestMonitor(
                    conf.getLong(Constant.HEARTBEAT_INTERVAL_KEY));
            cardiacArrestMonitor.setEventListener(this);
            taskMonitor =
                new TaskThreadMonitor(
                    conf.getLong(Constant.TASK_CHECK_INTERVAL_KEY) * 1000);
            taskMonitor.addListener(this);
            taskFactory = new TaskFactory(directory);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(TaskEvent event)
    {
        TaskThread task = event.getTaskThread();

        if (event.getType() == TaskEvent.Type.TASK_ABORTED)
        {
            task.release();
            logger.fatal("Task: " + task.getSid() + " " + event.getType());
        }
        else if (event.getType() == TaskEvent.Type.TASK_FINISHED)
        {
            task.release();
            logger.info("Task: " + task.getSid() + " " + event.getType());
        }
    }

    @Override
    public void handleCall(Call call)
    {
        if (call.getTaskId() < 0)
        {
            TaskThread task = taskFactory.createThread(call);
            taskMonitor.addThread(task);
            new Thread(task).start();
        }
    }

    @Override
    public void handle(CardiacArrest OMG)
    {
        logger.info("StorageNode " + OMG.getStorageNode() + " is dead.");
        // TODO: Data migration
    }

}

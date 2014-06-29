package test.nameserver.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.HeartbeatTask;
import nameserver.task.RegisterStorageTask;
import common.network.ServerConnector;
import common.network.XConnector;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.FinishCall;
import common.observe.call.HeartbeatCallS2N;
import common.observe.call.MigrateFileCallN2S;
import common.observe.call.RegistrationCallS2N;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThread;

public class TestHeartbeatTask
    extends TestCase
{
    private static ServerConnector NConnector;

    private static XConnector SConnector;

    @Override
    protected void setUp()
    {
        NConnector = ServerConnector.getInstance();
        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        SConnector = XConnector.getInstance();
        NConnector.addListener(new NCallListener());
        SConnector.addListener(new SCallListener());
    }

    public void testTask()
    {
        Storage storage = new Storage(1, "localhost");
        Status.getInstance().addStorage(storage);
        long timestamp = storage.getHearbeatTime();

        HeartbeatCallS2N call =
            new HeartbeatCallS2N("localhost", new HashMap<String, List<Long>>());
        SConnector.sendCall(call);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        storage = Status.getInstance().getStorage("localhost");
        assertTrue(storage.getHearbeatTime() > timestamp);
    }

    @Override
    protected void tearDown()
    {
    }

    private class NCallListener
        implements CallListener
    {
        @Override
        public void handleCall(Call call)
        {
            System.out.println("Server received a call: " + call.getType());
            TaskThread task =
                new HeartbeatTask(1, call.getInitiator(),
                    Status.getInstance().getStorage(((HeartbeatCallS2N) call).getAddress()),
                    NConnector, 2000);
            task.addListener(new TaskListener());
            new Thread(task).start();
        }
    }

    private class SCallListener
        implements CallListener
    {
        @Override
        public void handleCall(Call call)
        {
            System.out.println("Server sent a call: " + call.getType());
            if (Call.Type.MIGRATE_FILE_N2S == call.getType())
            {
                MigrateFileCallN2S c = (MigrateFileCallN2S) call;
                for (Entry<String, List<Long>> s : c.getFiles().entrySet())
                {
                    System.out.println("Get from storage: " + s.getKey());
                    for (Long l : s.getValue())
                        System.out.println("\t" + l);
                }
                Call back = new FinishCall(call.getTaskId());
                SConnector.send(back);
            }
        }
    }

    private class TaskListener
        implements TaskEventListener
    {
        @Override
        public void handle(TaskEvent event)
        {
            System.out.println("Task " + event.getTaskThread().getTaskId()
                + " " + event.getType());
        }
    }
}
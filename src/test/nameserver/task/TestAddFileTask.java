package test.nameserver.task;

import nameserver.meta.Directory;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.AddFileTask;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.observe.call.AddFileCallC2N;
import common.observe.call.AddFileCallN2C;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.FinishCall;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThread;
import junit.framework.TestCase;

public class TestAddFileTask
    extends TestCase
{
    private static ServerConnector NConnector;

    private static ClientConnector CConnector;

    @Override
    protected void setUp()
    {
        Status.getInstance().addStorage(new Storage(1, "localhost"));
        NConnector = ServerConnector.getInstance();
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        CConnector = ClientConnector.getInstance();
        CConnector.addListener(new CCallListener());
        NConnector.addListener(new NCallListener());
    }

    public void testTask()
    {
        Directory dir = Meta.getInstance().getDirectory("/a/");
        assertNull(dir);

        AddFileCallC2N call = new AddFileCallC2N("/a/", "b");
        CConnector.sendCall(call);

        try
        {
            Thread.sleep(100000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        dir = Meta.getInstance().getDirectory("/a/");
        assertNotNull(dir);
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
            TaskThread task = new AddFileTask(1, call, NConnector);
            task.addListener(new TaskListener());
            new Thread(task).start();
        }
    }

    private class CCallListener
        implements CallListener
    {

        @Override
        public void handleCall(Call call)
        {
            System.out.println("Server sent a call: " + call.getType());
            if (Call.Type.ADD_FILE_N2C == call.getType())
            {
                AddFileCallN2C c = (AddFileCallN2C) call;
                System.out.println(c.getTaskId());
                System.out.println(c.getType());
                System.out.println(c.getInitiator());
                for (String l : c.getLocations())
                    System.out.println(l);

                FinishCall ack = new FinishCall(call.getTaskId());
                CConnector.sendCall(ack);
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

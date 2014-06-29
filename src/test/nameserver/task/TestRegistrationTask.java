package test.nameserver.task;

import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.RegisterStorageTask;
import nameserver.task.RemoveFileTask;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.network.XConnector;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.FinishCall;
import common.observe.call.RegistrationCallS2N;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThread;
import junit.framework.TestCase;

public class TestRegistrationTask
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
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        SConnector = XConnector.getInstance();
        SConnector.addListener(new SCallListener());
        NConnector.addListener(new NCallListener());
    }

    public void testTask()
    {
        assertNull(Status.getInstance().getStorage("localhost"));

        RegistrationCallS2N call = new RegistrationCallS2N("localhost");
        SConnector.sendCall(call);

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        assertNotNull(Status.getInstance().getStorage("localhost"));
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
                new RegisterStorageTask(1, call, NConnector);
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
            if (Call.Type.REGISTRATION_S2N == call.getType())
            {
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

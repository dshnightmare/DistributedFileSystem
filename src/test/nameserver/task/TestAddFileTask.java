package test.nameserver.task;

import java.util.concurrent.TimeUnit;

import nameserver.meta.Directory;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.AddFileTask;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.call.AddFileCallC2N;
import common.call.AddFileCallN2C;
import common.call.Call;
import common.call.CallListener;
import common.call.FinishCall;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.Task;
import common.util.Configuration;
import junit.framework.TestCase;

public class TestAddFileTask
    extends TestCase
{
    private static ServerConnector NConnector;

    private static ClientConnector CConnector;

    private static Task task;

    @Override
    protected void setUp()
    {
        Status.getInstance().addStorage(new Storage(1, "localhost"));
        NConnector = ServerConnector.getInstance();
        try
        {
            TimeUnit.SECONDS.sleep(1);
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
        Meta meta = Meta.getInstance();

        Directory dir = meta.getDirectory("/a/");
        assertNull(dir);

        AddFileCallC2N call = new AddFileCallC2N("/a/", "b");
        CConnector.sendCall(call);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        synchronized (meta)
        {
            assertNotNull(meta.getDirectory("/a/"));
            assertNotNull(meta.getFile("/a/", "b"));
        }
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
            System.out.println("<---: " + call.getType());
            if (Call.Type.ADD_FILE_C2N == call.getType())
            {
                task =
                    new AddFileTask(1, call, NConnector, Configuration
                        .getInstance().getInteger(Configuration.DUPLICATE_KEY));
                task.addListener(new TaskListener());
                new Thread(task).start();
            }
            else if (Call.Type.FINISH == call.getType())
            {
                task.handleCall(call);
            }
        }
    }

    private class CCallListener
        implements CallListener
    {
        @Override
        public void handleCall(Call call)
        {
            System.out.println("--->: " + call.getType());
            if (Call.Type.ADD_FILE_N2C == call.getType())
            {
                AddFileCallN2C c = (AddFileCallN2C) call;
                System.out.println("task type: " + c.getType());
                System.out.print("location: ");
                for (String l : c.getLocations())
                    System.out.print(l + " ");
                System.out.println();

                FinishCall ack = new FinishCall();
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

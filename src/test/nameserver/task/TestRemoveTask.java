package test.nameserver.task;

import java.util.concurrent.TimeUnit;

import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.task.RemoveFileTask;
import junit.framework.TestCase;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.call.Call;
import common.call.CallListener;
import common.call.c2n.RemoveFileCallC2N;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.Task;

public class TestRemoveTask
    extends TestCase
{
    private static ServerConnector NConnector;

    private static ClientConnector CConnector;

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
        CConnector = ClientConnector.getInstance();
        CConnector.addListener(new CCallListener());
        NConnector.addListener(new NCallListener());
    }

    public void testTaskRemove()
    {

        Directory dir = new Directory("/a/");
        Meta.getInstance().addDirectory(dir);
        File file = new File("b", 1);
        dir.addFile(file);

        assertNotNull(Meta.getInstance().getDirectory("/a/").getFile("b"));

        RemoveFileCallC2N call = new RemoveFileCallC2N("/a/", "b");
        CConnector.sendCall(call);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        dir = Meta.getInstance().getDirectory("/a/");
        assertNotNull(dir);
        file = dir.getFile("b");
        assertNull(file);
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
            if (Call.Type.REMOVE_FILE_C2N == call.getType())
            {
                Task task = new RemoveFileTask(1, call, NConnector);
                task.addListener(new TaskListener());
                new Thread(task).start();
            }
        }
    }

    private class CCallListener
        implements CallListener
    {

        @Override
        public void handleCall(Call call)
        {
            System.out.println("Server sent a call: " + call.getType());
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

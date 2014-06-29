package test.nameserver.task;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.task.MoveFileTask;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.MoveFileCallC2N;
import common.thread.TaskThread;

public class TestMoveFileTask
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

        MoveFileCallC2N call = new MoveFileCallC2N("/a/", "b", "/c/", "d");
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
        dir = Meta.getInstance().getDirectory("/c/");
        assertNotNull(dir);
        file = dir.getFile("d");
        assertNotNull(file);
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
            TaskThread task = new MoveFileTask(1, call, NConnector);
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
        }
    }
}

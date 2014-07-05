package test.nameserver.task;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.meta.Directory;
import nameserver.meta.Meta;
import nameserver.task.MoveDirectoryTask;
import common.call.Call;
import common.call.CallListener;
import common.call.c2n.MoveDirectoryCallC2N;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.task.Task;

public class TestMoveDirectoryTask
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
        final Meta meta = Meta.getInstance();

        meta.addDirectory(new Directory("/a/"));
        meta.addDirectory(new Directory("/b/c/d/e/"));
        meta.addDirectory(new Directory("/b/c/f/"));

        assertTrue(meta.containDirectory("/a/"));
        assertTrue(meta.containDirectory("/b/c/d/e/"));
        assertTrue(meta.containDirectory("/b/c/f/"));
        assertTrue(meta.containDirectory("/b/"));
        assertTrue(meta.containDirectory("/b/c/"));
        assertTrue(meta.containDirectory("/b/c/d/"));

        MoveDirectoryCallC2N call = new MoveDirectoryCallC2N("/a/", "/b/");
        CConnector.sendCall(call);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        call = new MoveDirectoryCallC2N("/a/", "/x/");
        CConnector.sendCall(call);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        assertFalse(meta.containDirectory("/a/"));
        assertTrue(meta.containDirectory("/x/"));

        call = new MoveDirectoryCallC2N("/b/", "/y/");
        CConnector.sendCall(call);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        assertFalse(meta.containDirectory("/b/c/d/e/"));
        assertFalse(meta.containDirectory("/b/c/f/"));
        assertFalse(meta.containDirectory("/b/"));
        assertFalse(meta.containDirectory("/b/c/"));
        assertFalse(meta.containDirectory("/b/c/d/"));

        assertTrue(meta.containDirectory("/y/c/d/e/"));
        assertTrue(meta.containDirectory("/y/c/f/"));
        assertTrue(meta.containDirectory("/y/"));
        assertTrue(meta.containDirectory("/y/c/"));
        assertTrue(meta.containDirectory("/y/c/d/"));

        // try
        // {
        // TimeUnit.SECONDS.sleep(100);
        // }
        // catch (InterruptedException e)
        // {
        // e.printStackTrace();
        // }
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
            if (Call.Type.MOVE_DIRECTORY_C2N == call.getType())
            {
                Task task = new MoveDirectoryTask(1, call, NConnector);
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
}

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
        Meta meta = Meta.getInstance();
        File file = new File("b", 1);
        meta.addFile("/a/", file);

        assertNotNull(meta.getFile("/a/", "b"));
        assertNull(meta.getFile("/c/", "d"));

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

        assertNotNull(meta.getDirectory("/a/"));
        assertNull(meta.getFile("/a/", "b"));
        assertNotNull(meta.getFile("/c/", "d"));
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
            if (Call.Type.MOVE_FILE_C2N == call.getType())
            {
                TaskThread task = new MoveFileTask(1, call, NConnector);
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

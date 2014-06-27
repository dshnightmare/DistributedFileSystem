package test.nameserver;

import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.task.RemoveFileTask;
import junit.framework.TestCase;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.RemoveFileCallC2N;
import common.thread.TaskThread;

public class TestTaskRemove
    extends TestCase
    implements CallListener
{
    private static Meta meta;
    
    private static ServerConnector NConnector;
    
    private static ClientConnector CConnector;
    
    @Override
    protected void setUp()
    {
        meta = new Meta();
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
        NConnector.addListener(this);
        System.out.println("HELLO");
    }

    public void testTaskRemove()
    {
        
        Directory directory = new Directory("/a/");
        meta.addDirectory(directory);
        File file = new File("b", 1);
        directory.addFile(file);

        assertNotNull(meta.getDirectory("/a/").getFile("b"));

        RemoveFileCallC2N call = new RemoveFileCallC2N("/a/", "b");
        CConnector.sendCall(call);
        
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        assertNull(meta.getDirectory("/a/").getFile("b"));
    }

    @Override
    protected void tearDown()
    {
    }

    @Override
    public void handleCall(Call call)
    {
        TaskThread task = new RemoveFileTask(1, call, meta, NConnector);
        new Thread(task).start();
    }
}

package test.nameserver;

import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.task.RemoveFileTask;
import junit.framework.TestCase;
import common.network.ServerConnector;
import common.observe.call.RemoveFileCallC2N;
import common.thread.TaskThread;

public class TestTaskRemove
    extends TestCase
{
    @Override
    protected void setUp()
    {
    }

    public void testTaskRemove()
    {
        Meta meta = new Meta();
        Directory directory = new Directory("/a/");
        meta.addDirectory(directory);
        File file = new File("b", 1);
        directory.addFile(file);

        assertNotNull(meta.getDirectory("/a/").getFile("b"));

        ServerConnector connector = new ServerConnector();
        RemoveFileCallC2N call = new RemoveFileCallC2N("/a/", "b");
        connector.start();
        TaskThread task = new RemoveFileTask(1, call, meta, connector);
        new Thread(task).start();

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
}

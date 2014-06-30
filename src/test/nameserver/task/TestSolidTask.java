package test.nameserver.task;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.task.SolidTask;
import common.thread.TaskThread;

public class TestSolidTask extends TestCase
{
    public void testTaskRemove()
    {
        Meta meta =  Meta.getInstance();
        
        TaskThread task = new SolidTask(1, "output", "backup", 3);
        new Thread(task).start();
        
        try
        {
            TimeUnit.SECONDS.sleep(4);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        meta.addFile("/a/b/c", new File("f1", 1));
        meta.addFile("/a/b/c", new File("f2", 2));
       
        try
        {
            TimeUnit.SECONDS.sleep(4);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        meta.addFile("/c", new File("f3", 3));
        meta.addFile("/c/d/", new File("f1", 4));
        meta.addFile("/", new File("f1", 5));
        
        try
        {
            TimeUnit.SECONDS.sleep(4);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        meta.removeFile("/a/b/c", "f1");
        meta.removeFile("/c", "f3");
        meta.removeDirectory("/c/d/");
        
        try
        {
            TimeUnit.SECONDS.sleep(4);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

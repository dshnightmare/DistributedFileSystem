package test.nameserver.backup;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.BackupUtil;
import nameserver.meta.File;
import nameserver.meta.Meta;

public class TestBackupUtil extends TestCase
{
    @Override
    protected void setUp()
    {
    }
    
    public void testBackupAndRestore()
    {
        try
        {
            BackupUtil util = BackupUtil.getInstance();
            Meta meta =  Meta.getInstance();
            
            TimeUnit.SECONDS.sleep(1);
            
            util.backup();
            System.out.println("Backup 1");
            
            meta.addFile("/a/b/c/", new File("f1", 1));
            meta.addFile("/a/b/c/", new File("f2", 2));
            TimeUnit.SECONDS.sleep(1);
           
            util.backup();
            System.out.println("Backup 2");
            
            meta.addFile("/c/", new File("f3", 3));
            meta.addFile("/c/d/", new File("f1", 4));
            meta.addFile("/", new File("f1", 5));
            TimeUnit.SECONDS.sleep(1);
            
            util.backup();
            System.out.println("Backup 3");
            
            meta.removeFile("/a/b/c/", "f1");
            meta.removeFile("/c/", "f3");
            meta.removeDirectory("/c/d/");
            TimeUnit.SECONDS.sleep(1);
            
            util.backup();
            System.out.println("Backup 4");
            
            meta.clear();
            
            util.restore();
            
            assertNotNull(meta.getFile("/a/b/c/", "f2"));
            assertNotNull(meta.getFile("/", "f1"));
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

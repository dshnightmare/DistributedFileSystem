package test.nameserver.backup;

import java.util.concurrent.TimeUnit;

import common.observe.call.Call;

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
            meta.clear();
            
            TimeUnit.SECONDS.sleep(1);
            
            util.writeBackupImage();
            System.out.println("Backup 1");
            
            meta.addFile("/a/b/c/", new File("f1", 1));
            meta.setDirectoryValid("/a/b/c/", true);
            meta.setFileValid("/a/b/c/", "f2", true);
            
            meta.addFile("/a/b/c/", new File("f2", 2));
            meta.setFileValid("/a/b/c/", "f2", true);
            
            TimeUnit.SECONDS.sleep(1);
           
            util.writeBackupImage();
            System.out.println("Backup 2");
            
            meta.addFile("/c/", new File("f3", 3));
            meta.setDirectoryValid("/c/", true);
            meta.setFileValid("/c/", "f3", true);
            
            meta.addFile("/c/d/", new File("f1", 4));
            meta.setDirectoryValid("/c/d/", true);
            meta.setFileValid("/c/d/", "f1", true);
            
            meta.addFile("/", new File("f1", 5));
            meta.setDirectoryValid("/", true);
            meta.setFileValid("/", "f1", true);
            
            TimeUnit.SECONDS.sleep(1);
            
            util.writeBackupImage();
            System.out.println("Backup 3");
            
            meta.removeFile("/a/b/c/", "f1");
            meta.removeFile("/c/", "f3");
            meta.removeDirectory("/c/d/");
            TimeUnit.SECONDS.sleep(1);
            
            util.writeBackupImage();
            System.out.println("Backup 4");
            
            meta.clear();
            
            util.readBackupImage();
            
            assertNotNull(meta.getFile("/a/b/c/", "f2"));
            assertNotNull(meta.getFile("/", "f1"));
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    public void testLog()
    {
        BackupUtil util = BackupUtil.getInstance();
        Meta meta = Meta.getInstance();
        
        meta.clear();
        
        util.deleteBackLog();
        
        util.writeLogIssue(1, Call.Type.ADD_FILE_C2N, "/1/a/b/c/ d 1");
        util.writeLogIssue(2, Call.Type.ADD_FILE_C2N, "/1/c/d/ a 2");
        util.writeLogIssue(3, Call.Type.ADD_FILE_C2N, "/1/e/ x 3");
        util.writeLogCommit(1);
        util.writeLogCommit(2);
        util.writeLogIssue(4, Call.Type.REMOVE_FILE_C2N, "/1/c/d/ a");
        util.writeLogCommit(4);
        util.writeLogIssue(5, Call.Type.MOVE_FILE_C2N, "/1/a/b/c/ d /1/a/ y");
        util.writeLogCommit(3);
        util.writeLogIssue(6, Call.Type.ADD_FILE_C2N, "/1/p/ z 4");
        util.writeLogCommit(5);
        
        util.readBackupLog();
        
        assertFalse(meta.containFile("/1/a/b/c/", "d"));
        assertTrue(meta.containFile("/1/a/", "y"));
        assertFalse(meta.containFile("/1/c/d/", "a"));
        assertFalse(meta.containFile("/1/p/", "z"));
    }
}

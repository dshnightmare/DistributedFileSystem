package test.nameserver.backup;


import common.observe.call.Call;

import junit.framework.TestCase;
import nameserver.LogUtil;
import nameserver.meta.Meta;

public class TestLogUtil extends TestCase
{
    @Override
    protected void setUp()
    {
    }
    
    public void testLog()
    {
        Meta meta = Meta.getInstance();
        LogUtil log = LogUtil.getInstance();
        
        log.checkpoint();
        
        log.writeIssue(1, Call.Type.ADD_FILE_C2N, "/a/b/c/ d 1");
        log.writeIssue(2, Call.Type.ADD_FILE_C2N, "/c/d/ a 2");
        log.writeIssue(3, Call.Type.ADD_FILE_C2N, "/e/ x 3");
        log.writeCommit(1);
        log.writeCommit(2);
        log.writeIssue(4, Call.Type.REMOVE_FILE_C2N, "/c/d/ a");
        log.writeCommit(4);
        log.writeIssue(5, Call.Type.MOVE_FILE_C2N, "/a/b/c/ d /a/ y");
        log.writeCommit(3);
        log.writeIssue(6, Call.Type.ADD_FILE_C2N, "/p/ z 4");
        log.writeCommit(5);
        
        log.recover();
        
        assertFalse(meta.containFile("/a/b/c/", "d"));
        assertTrue(meta.containFile("/a/", "y"));
        assertFalse(meta.containFile("/c/d/", "a"));
        assertFalse(meta.containFile("/p/", "z"));
    }
}

package test.nameserver.meta;

import java.util.List;

import nameserver.status.Status;
import nameserver.status.Storage;
import junit.framework.TestCase;

public class TestStatus
    extends TestCase
{
    public void testStatus()
    {
        Status s = Status.getInstance();
        Storage st1 = new Storage("localhost1");
        Storage st2 = new Storage("localhost2");
        Storage st3 = new Storage("localhost3");

        s.addStorage(st1);
        s.addStorage(st2);
        s.addStorage(st3);

        List<Storage> l = s.allocateStorage(3);
        assertEquals(l.size(), 3);

        s.removeStorage(st3);

        l = s.allocateStorage(3);
        System.out.println(l.size());
        assertEquals(l.size(), 2);
        
        assertTrue(st1.getHearbeatTime() > 0);
    }
}

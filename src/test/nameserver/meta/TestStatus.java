package test.nameserver.meta;

import java.util.List;

import nameserver.meta.Status;
import nameserver.meta.Storage;
import junit.framework.TestCase;

public class TestStatus
    extends TestCase
{
    public void testStatus()
    {
        Status s = new Status();
        Storage st1 = new Storage(1, "localhost");
        Storage st2 = new Storage(2, "localhost");
        Storage st3 = new Storage(3, "localhost");

        s.addStorage(st1);
        s.addStorage(st2);
        s.addStorage(st3);

        List<Storage> l = s.allocateStorage(3);
        assertEquals(l.size(), 3);

        s.removeStorage(st3);

        l = s.allocateStorage(3);
        System.out.println(l.size());
        assertEquals(l.size(), 2);
    }
}

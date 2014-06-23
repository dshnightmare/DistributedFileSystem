package test;

import java.util.HashSet;
import java.util.Set;

import nameserver.meta.FileNode;
import nameserver.meta.StorageStatus;
import junit.framework.TestCase;

public class TestStorageStatus
    extends TestCase
{
    public void testDiff()
    {
        StorageStatus status = new StorageStatus(1, "localhost");
        status.addFile(new FileNode("A", 1));
        status.addFile(new FileNode("B", 2));
        status.addFile(new FileNode("C", 3));

        Set<Long> list = new HashSet<Long>();
        list.add((long) 1);
        list.add((long) 2);
        list.add((long) 4);
        
        for (Long l : list)
        {
            System.out.print(l);
        }
        System.out.println();

        status.diff(list);
        
        for (Long l : list)
        {
            System.out.print(l);
        }

        assertFalse(list.contains((long) 2));
        assertFalse(list.contains((long) 1));
        assertTrue(list.contains((long) 4));
    }

}

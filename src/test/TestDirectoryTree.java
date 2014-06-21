package test;

import nameserver.meta.DirectoryTree;
import junit.framework.TestCase;

public class TestDirectoryTree
    extends TestCase
{
    @Override
    protected void setUp()
    {
    }

    public void testDirectoryTree()
    {
        DirectoryTree tree = new DirectoryTree();

        assertNotNull(tree.getNode("/"));
        assertNull(tree.getNode("/abc/"));
        assertNull(tree.getNode("/abc/def/ghi"));

        tree.createPath("/abc/def/ghi/");
        tree.createPath("/abc/ghi/");

        assertNotNull(tree.getNode("/abc/"));
        assertNotNull(tree.getNode("/abc/def/ghi"));
        assertNotNull(tree.getNode("abc/ghi/"));
    }

    public void testLockAndUnlock()
    {
        final DirectoryTree tree = new DirectoryTree();
        tree.createPath("/abc/def/ghi");
        tree.createPath("/abc/def/jkl");
        tree.createPath("/abc/ghi");

        Thread threadA = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                assertTrue(tree.lock("/abc/def/ghi"));
                try
                {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                tree.unlock("/abc/def/ghi");
            }
        });
        Thread threadB = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                assertFalse(tree.lock("/abc/def/ghi"));
                assertTrue(tree.lock("/abc/ghi"));
                tree.unlock("/abc/def/ghi");
                try
                {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                assertTrue(tree.lock("/abc/def/ghi"));
                tree.unlock("/abc/def/ghi/");
            }
        });

        threadA.start();
        threadB.start();
        
        try
        {
            Thread.sleep(12000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void tearDown()
    {
    }
}

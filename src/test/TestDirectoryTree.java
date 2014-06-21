package test;

import nameserver.meta.DirectoryTree;
import nameserver.meta.FileNode;
import nameserver.meta.Node;
import junit.framework.TestCase;

public class TestDirectoryTree
    extends TestCase
{
    private static Object sync = new Object();

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
        Node dir = tree.createPath("/dir");
        Node file = new FileNode("file");
        dir.addChild(file);

        Thread a = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                    FileNode file = (FileNode) tree.getNode("/dir/file");
                    assertTrue(file.getLock());
                    synchronized (sync)
                    {
                        sync.notifyAll();
                    }
                    synchronized (sync)
                    {
                        sync.wait();
                    }
                    file.releaseLock();
                    synchronized (sync)
                    {
                        sync.notifyAll();
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        Thread b = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    synchronized (sync)
                    {
                        sync.wait();
                    }
                    FileNode file = (FileNode) tree.getNode("/dir/file");
                    assertFalse(file.getLock());
                    synchronized (sync)
                    {
                        sync.notifyAll();
                    }
                    synchronized (sync)
                    {
                        sync.wait();
                    }
                    assertTrue(file.getLock());
                    file.releaseLock();
                    synchronized (this) {
                        notifyAll();
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        a.start();
        b.start();

        synchronized (b)
        {
            try
            {
                b.wait();
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void tearDown()
    {
    }
}

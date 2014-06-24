package test.nameserver;

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

        try
        {
            assertNotNull(tree.getNode("/"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        try
        {
            tree.getNode("/abc/");
        }
        catch (Exception e)
        {
            assertTrue(true);
        }

        try
        {
            tree.getNode("/abc/def/ghi");
        }
        catch (Exception e)
        {
            assertTrue(true);
        }

        try
        {
            tree.createPath("/abc/def/ghi/", true);
            tree.createPath("/abc/ghi/", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            assertNotNull(tree.getNode("/abc/"));
            assertNotNull(tree.getNode("/abc/def/ghi"));
            assertNotNull(tree.getNode("abc/ghi/"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void testLockAndUnlock()
    {
        final DirectoryTree tree = new DirectoryTree();
        try
        {
            Node dir = tree.createPath("/dir", true);
            Node file = new FileNode("file", 1);
            dir.addChild(file);
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Thread a = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                    try
                    {
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
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
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
                    try
                    {
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
                        synchronized (this)
                        {
                            notifyAll();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
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

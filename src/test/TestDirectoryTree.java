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

    public void testXXX()
    {
        DirectoryTree tree = new DirectoryTree();

        assertTrue(tree.containNode("/"));
        assertFalse(tree.containNode("/abc/"));
        assertFalse(tree.containNode("/abc/def/ghi"));
        
        tree.createPath("/abc/def/ghi/");
        
        assertTrue(tree.containNode("/abc/"));
        assertTrue(tree.containNode("/abc/def/ghi"));
    }

    @Override
    protected void tearDown()
    {
    }
}

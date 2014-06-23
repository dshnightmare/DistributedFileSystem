package test;

import nameserver.meta.DirectoryNode;
import nameserver.meta.FileNode;
import nameserver.meta.Node;
import junit.framework.TestCase;

public class TestDirectoryNode extends TestCase
{
    @Override
    protected void setUp()
    {
    }

    public void testNode()
    {
        Node root = new DirectoryNode("root");
        root.addChild(new DirectoryNode("b"));
        root.addChild(new FileNode("a", 1));
        root.addChild(new DirectoryNode("c"));

        for (Node n : root.getChilds())
        {
            System.out.println(n.getName());
        }
        
        assertNotNull(root.getChild("a"));
        assertNotNull(root.getChild("b"));
        assertNotNull(root.getChild("c"));
    }

    @Override
    protected void tearDown()
    {
    }
}

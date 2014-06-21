package test;

import nameserver.meta.DirectoryNode;
import nameserver.meta.DirectoryNode.DirectoryNodeType;
import junit.framework.TestCase;

public class TestDirectoryNode extends TestCase
{
    @Override
    protected void setUp()
    {
    }

    public void testNode()
    {
        DirectoryNode root = new DirectoryNode("root", DirectoryNodeType.DIR);
        root.addChild(new DirectoryNode("b", DirectoryNodeType.DIR));
        root.addChild(new DirectoryNode("a", DirectoryNodeType.FILE));
        root.addChild(new DirectoryNode("c", DirectoryNodeType.DIR));

        for (DirectoryNode n : root.getChilds())
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

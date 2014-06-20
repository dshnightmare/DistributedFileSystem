package nameserver.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeNode
    implements Comparable<TreeNode>
{
    private String name;

    private TreeNodeType type;

    private List<TreeNode> childs = new ArrayList<TreeNode>();

    public TreeNode(String name, TreeNodeType type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String newName)
    {
        name = newName;
    }

    public void addChild(TreeNode child)
    {
        childs.add(child);
        Collections.sort(childs);
    }

    public boolean isFile()
    {
        return type == TreeNodeType.FILE;
    }

    public boolean isDir()
    {
        return type == TreeNodeType.DIR;
    }

    public List<TreeNode> getChilds()
    {
        return childs;
    }

    public TreeNode getChild(String name)
    {
        int index = Collections.binarySearch(childs, new TreeNode(name, TreeNodeType.FILE));
        if (0 > index)
            return null;
        else
            return childs.get(index);
    }
    
    @Override
    public int compareTo(TreeNode o)
    {
        return name.compareTo(o.getName());
    }

    public static enum TreeNodeType
    {
        DIR, FILE
    }

    public static void main(String[] args)
    {
        TreeNode root = new TreeNode("root", TreeNodeType.DIR);
        root.addChild(new TreeNode("b", TreeNodeType.DIR));
        root.addChild(new TreeNode("a", TreeNodeType.FILE));
        root.addChild(new TreeNode("c", TreeNodeType.DIR));

        for (TreeNode n : root.getChilds())
        {
            System.out.println(n.getName());
        }
    }
}
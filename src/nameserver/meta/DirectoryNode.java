package nameserver.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryNode
    implements Comparable<DirectoryNode>
{
    private String name;

    private DirectoryNodeType type;

    private List<DirectoryNode> childs = new ArrayList<DirectoryNode>();

    public DirectoryNode(String name, DirectoryNodeType type)
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

    public void addChild(DirectoryNode child)
    {
        childs.add(child);
        Collections.sort(childs);
    }

    public boolean isFile()
    {
        return type == DirectoryNodeType.FILE;
    }

    public boolean isDir()
    {
        return type == DirectoryNodeType.DIR;
    }

    public List<DirectoryNode> getChilds()
    {
        return childs;
    }

    public DirectoryNode getChild(String name)
    {
        int index =
            Collections.binarySearch(childs, new DirectoryNode(name,
                DirectoryNodeType.FILE));
        if (0 > index)
            return null;
        else
            return childs.get(index);
    }

    @Override
    public int compareTo(DirectoryNode o)
    {
        return name.compareTo(o.getName());
    }

    public static enum DirectoryNodeType
    {
        DIR, FILE
    }

    public static void main(String[] args)
    {
        DirectoryNode root = new DirectoryNode("root", DirectoryNodeType.DIR);
        root.addChild(new DirectoryNode("b", DirectoryNodeType.DIR));
        root.addChild(new DirectoryNode("a", DirectoryNodeType.FILE));
        root.addChild(new DirectoryNode("c", DirectoryNodeType.DIR));

        for (DirectoryNode n : root.getChilds())
        {
            System.out.println(n.getName());
        }
    }
}
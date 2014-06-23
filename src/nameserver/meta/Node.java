package nameserver.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Node
    implements Comparable<Node>
{
    private String name;

    private List<Node> childs = new ArrayList<Node>();

    public Node(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String newName)
    {
        name = newName;
    }

    public void addChild(Node child)
    {
        childs.add(child);
        Collections.sort(childs);
    }

    public Node removeChild(Node child)
    {
        return childs.remove(childs.indexOf(child));
    }

    public abstract boolean isDirectory();

    public abstract boolean isFile();

    public List<Node> getChilds()
    {
        return childs;
    }

    public Node getChild(String name)
    {
        int index = Collections.binarySearch(childs, new Node(name)
        {
            @Override
            public boolean isDirectory()
            {
                return false;
            }

            @Override
            public boolean isFile()
            {
                return false;
            }
        });
        if (0 > index)
            return null;
        else
            return childs.get(index);
    }

    @Override
    public int compareTo(Node o)
    {
        return name.compareTo(o.getName());
    }

    public static enum Type
    {
        DIR, FILE
    }
}
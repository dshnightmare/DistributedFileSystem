package nameserver.meta;

import nameserver.meta.Node.Type;

public class DirectoryTree
{
    public static final String SEPERATOR = "/";

    private Node root = new DirectoryNode("");

    public synchronized Node getNode(String path)
    {
        return find(root, normalizePath(path));
    }

    public synchronized Node createPath(String path)
    {
        return create(root, normalizePath(path));
    }
    
    private Node create(Node node, String path)
    {
        if (null == node || null == path)
            return null;
        if (path.isEmpty())
            return node;

        String name = null;
        String leftPath = null;
        if (path.indexOf(SEPERATOR) >= 0)
        {
            name = path.split(SEPERATOR, 2)[0];
            leftPath = path.split(SEPERATOR, 2)[1];
        }
        else
        {
            name = path;
            leftPath = "";
        }

        Node childNode = node.getChild(name);
        if (null == childNode)
        {
            childNode = new DirectoryNode(name);
            node.addChild(childNode);
        }
        return create(childNode, leftPath);
    }

    private Node find(Node node, String path)
    {
        if (null == node || null == path)
            return null;
        if (path.isEmpty())
            return node;

        String name = null;
        String leftPath = null;
        if (path.indexOf(SEPERATOR) >= 0)
        {
            name = path.split(SEPERATOR, 2)[0];
            leftPath = path.split(SEPERATOR, 2)[1];
        }
        else
        {
            name = path;
            leftPath = "";
        }
        Node childNode = node.getChild(name);
        return find(childNode, leftPath);
    }

    private String normalizePath(String path)
    {
        String normalPath = path;
        // Remove the suffix '/'
        if (normalPath.length() - 1 == normalPath.lastIndexOf(SEPERATOR))
            normalPath = normalPath.substring(0, path.length() - 1);
        // Remove the prefix '/'
        if (0 == normalPath.indexOf(SEPERATOR))
            normalPath = normalPath.substring(1);
        return normalPath;
    }
}

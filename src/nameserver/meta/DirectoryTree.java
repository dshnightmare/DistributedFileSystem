package nameserver.meta;

import nameserver.meta.DirectoryNode.DirectoryNodeType;

public class DirectoryTree
{
    public static final String SEPERATOR = "/";

    private DirectoryNode root = new DirectoryNode("", DirectoryNodeType.DIR);

    public synchronized DirectoryNode getNode(String path)
    {
        return find(root, normalizePath(path));
    }

    public synchronized DirectoryNode createPath(String path)
    {
        return create(root, normalizePath(path));
    }

    public synchronized boolean lock(String path)
    {
        DirectoryNode node = getNode(path);
        if (null == node)
            return false;
        return node.getLock();
    }
    
    public synchronized void unlock(String path)
    {
        DirectoryNode node = getNode(path);
        if (null != node)
            node.releaseLock();
        
    }

    private DirectoryNode create(DirectoryNode node, String path)
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

        DirectoryNode childNode = node.getChild(name);
        if (null == childNode)
        {
            childNode = new DirectoryNode(name, DirectoryNodeType.DIR);
            node.addChild(childNode);
        }
        return create(childNode, leftPath);
    }

    private DirectoryNode find(DirectoryNode node, String path)
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
        DirectoryNode childNode = node.getChild(name);
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

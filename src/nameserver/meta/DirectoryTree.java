package nameserver.meta;

public class DirectoryTree
{
    public static final String SEPERATOR = "/";

    private Node root = new DirectoryNode("");

    public synchronized Node getNode(String path) throws Exception
    {
        Node node = find(root, normalizePath(path));
        if (null == node)
        {
            throw new Exception("Failed to get " + path
                + ", file or dir was not existed.");
        }
        return node;
    }

    public synchronized Node createPath(String path, boolean isRecursive)
        throws Exception
    {
        if (contains(path))
        {
            throw new Exception("Failed to create " + path
                + ", file or dir had already been existed.");
        }

        Node node = create(root, normalizePath(path), isRecursive);
        if (null == node)
        {
            throw new Exception("Failed to create " + path
                + ", parent directory was not existed.");
        }
        return node;
    }

    public synchronized Node removePath(String path) throws Exception
    {
        String parentPath = getPrefixPath(path);
        String fileName = getSuffixPath(path);
        Node parentNode = null;
        try
        {
            parentNode = getNode(parentPath);
        }
        catch (Exception e)
        {
            throw new Exception("Failed to remove " + path
                + ", file or dir was not existed.");
        }
        Node node = parentNode.getChild(fileName);
        if (null == node)
        {
            throw new Exception("Failed to remove " + path
                + ", file or dir was not existed.");
        }

        return parentNode.removeChild(node);
    }

    public boolean contains(String path)
    {
        try
        {
            getNode(path);
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    private Node create(Node node, String path, boolean isRecursive)
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
            if (!isRecursive)
                return null;
            else
            {
                childNode = new DirectoryNode(name);
                node.addChild(childNode);
            }
        }
        return create(childNode, leftPath, isRecursive);
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

    public static String normalizePath(String path)
    {
        if (path.isEmpty())
            return path;

        String normalPath = path;
        // Remove the suffix '/'
        if (normalPath.length() - 1 == normalPath.lastIndexOf(SEPERATOR))
            normalPath = normalPath.substring(0, path.length() - 1);
        // Remove the prefix '/'
        if (0 == normalPath.indexOf(SEPERATOR))
            normalPath = normalPath.substring(1);
        return normalPath;
    }

    public static String getPrefixPath(String path)
    {
        String normalPath = normalizePath(path);
        final int pos = normalPath.lastIndexOf(SEPERATOR);
        if (pos < 0)
            return "";
        else
            return normalPath.substring(0, pos);
    }

    public static String getSuffixPath(String path)
    {
        String normalPath = normalizePath(path);
        final int pos = normalPath.indexOf(SEPERATOR);
        if (pos < 0)
            return normalPath;
        else
            return normalPath.substring(pos + 1);
    }
}

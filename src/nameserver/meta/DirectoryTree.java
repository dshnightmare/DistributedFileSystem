package nameserver.meta;

import nameserver.meta.TreeNode.TreeNodeType;

public class DirectoryTree
{
    public static final String sep = "/";

    private TreeNode root = new TreeNode("", TreeNodeType.DIR);

    public TreeNode getNode(String path)
    {
        return find(root, normalizePath(path));
    }

    public TreeNode createPath(String path)
    {
        return create(root, normalizePath(path));
    }

    public boolean containNode(String path)
    {
        return null != getNode(path);
    }

    private TreeNode create(TreeNode node, String path)
    {
        if (null == node || null == path)
            return null;
        if (path.isEmpty())
            return node;

        String name = null;
        String leftPath = null;
        if (path.indexOf(sep) >= 0)
        {
            name = path.split("/", 2)[0];
            leftPath = path.split("/", 2)[1];
        }
        else
        {
            name = path;
            leftPath = "";
        }

        TreeNode childNode = node.getChild(name);
        if (null == childNode)
        {
            childNode = new TreeNode(name, TreeNodeType.DIR);
            node.addChild(childNode);
        }
        return create(childNode, leftPath);
    }

    private TreeNode find(TreeNode node, String path)
    {
        if (null == node || null == path)
            return null;
        if (path.isEmpty())
            return node;

        String name = null;
        String leftPath = null;
        if (path.indexOf(sep) >= 0)
        {
            name = path.split(sep, 2)[0];
            leftPath = path.split(sep, 2)[1];
        }
        else
        {
            name = path;
            leftPath = "";
        }
        TreeNode childNode = node.getChild(name);
        return find(childNode, leftPath);
    }

    private String normalizePath(String path)
    {
        String normalPath = path;
        // Remove the suffix '/'
        if ('/' == path.charAt(path.length() - 1))
            normalPath = normalPath.substring(0, path.length() - 1);
        // Remove the prefix '/'
        if ('/' == path.charAt(0))
            normalPath = normalPath.substring(1);
        return normalPath;
    }

    public static void main(String[] args)
    {
        DirectoryTree tree = new DirectoryTree();

        tree.createPath("/abc/def/ghi/");
        System.out.println(tree.containNode("/abc/def/ghi"));
    }
}

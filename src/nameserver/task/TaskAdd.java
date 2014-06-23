package nameserver.task;

import java.util.Random;

import nameserver.meta.DirectoryTree;
import nameserver.meta.FileNode;
import nameserver.meta.Node;
import common.thread.TaskThread;

public class TaskAdd
    extends TaskThread
{
    private String path;

    private DirectoryTree directory;

    private boolean isRecursive;

    public TaskAdd(long sid, String path, DirectoryTree directory,
        boolean isRecursive)
    {
        super(sid);
        this.path = path;
        this.directory = directory;
        this.isRecursive = isRecursive;
    }

    @Override
    public void run()
    {
        String parentPath = DirectoryTree.getPrefixPath(path);
        String fileName = DirectoryTree.getSuffixPath(path);
        Node parentNode = null;
        try
        {
            parentNode = directory.getNode(parentPath);
        }
        catch (Exception e)
        {
            try
            {
                parentNode = directory.createPath(parentPath, isRecursive);
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
                // TODO: Send back error call to client.
            }
        }

        Random rand = new Random(System.currentTimeMillis());
        Node node = new FileNode(fileName, rand.nextLong());
        
        // TODO Send continue to client
        // TODO Wait for Storage server send finish packet

        parentNode.addChild(node);
    }

    @Override
    public void release()
    {
        // TODO Auto-generated method stub

    }
}

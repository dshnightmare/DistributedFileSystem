package nameserver.task;

import nameserver.meta.DirectoryTree;
import nameserver.meta.Node;
import common.thread.TaskThread;

public class TaskMove
    extends TaskThread
{
    private String oldPath;

    private String newPath;

    private DirectoryTree directory;

    public TaskMove(long sid, String oldPath, String newPath,
        DirectoryTree directory)
    {
        super(sid);
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.directory = directory;
    }

    @Override
    public void run()
    {
        if (0 == oldPath.compareTo(newPath))
        {
            setFinish();
            return;
        }

        String newName = DirectoryTree.getSuffixPath(newPath);
        String newParentPath = DirectoryTree.getPrefixPath(newPath);

        try
        {
            Node node = directory.removePath(oldPath);
            node.setName(newName);
            Node parentNode = directory.getNode(newParentPath);
            parentNode.addChild(node);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // TODO: Return false Call
        }
        setFinish();
    }

    @Override
    public void release()
    {
        // TODO Auto-generated method stub

    }

}

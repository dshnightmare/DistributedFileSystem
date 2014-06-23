package nameserver.task;

import nameserver.meta.DirectoryTree;
import common.thread.TaskThread;

public class TaskRemove
    extends TaskThread
{
    private String path;

    private DirectoryTree directory;

    public TaskRemove(long sid, String path, DirectoryTree directory)
    {
        super(sid);
        this.path = path;
        this.directory = directory;
    }

    @Override
    public void run()
    {
        try
        {
            directory.removePath(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // TODO: Send error call back to client.
        }
        setFinish();
    }

    @Override
    public void release()
    {
        // TODO Auto-generated method stub

    }

}

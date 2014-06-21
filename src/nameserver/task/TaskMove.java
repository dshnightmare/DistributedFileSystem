package nameserver.task;

import common.thread.TaskThread;

public class TaskMove
    extends TaskThread
{
    private String oldpath;

    private String newPath;

    public TaskMove(long sid, String oldPath, String newPath)
    {
        super(sid);
        this.oldpath = oldPath;
        this.newPath = newPath;
    }

    @Override
    public void run()
    {
        
    }

}

package nameserver.task;

import common.thread.TaskThread;

public class TaskAdd
    extends TaskThread
{
    private String filePath;

    public TaskAdd(long sid, String filePath)
    {
        super(sid);
        this.filePath = filePath;
    }

    @Override
    public void run()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void release()
    {
        // TODO Auto-generated method stub
        
    }
}

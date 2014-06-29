package nameserver.task;

import nameserver.meta.Meta;
import common.observe.call.Call;
import common.thread.TaskThread;

public class SolidTask extends TaskThread
{
    public SolidTask(long tid)
    {
        super(tid);
    }

    @Override
    public void handleCall(Call call)
    {
        synchronized (Meta.getInstance())
        {
            // TODO
        }
    }

    @Override
    public void run()
    {
        synchronized (Meta.getInstance())
        {
            
        }
    }

    @Override
    public void release()
    {
    }

}

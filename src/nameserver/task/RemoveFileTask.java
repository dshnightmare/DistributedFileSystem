package nameserver.task;

import nameserver.meta.Meta;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.Call;
import common.observe.call.FinishCall;
import common.observe.call.RemoveFileCallC2N;
import common.thread.TaskThread;

public class RemoveFileTask
    extends TaskThread
{
    private String dirName;

    private String fileName;

    private Connector connector;

    private String initiator;
    
    private boolean hasLock = false;

    public RemoveFileTask(long sid, Call call, Connector connector)
    {
        super(sid);
        RemoveFileCallC2N c = (RemoveFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.initiator = c.getInitiator();
        this.connector = connector;
    }

    @Override
    public void run()
    {
        lock();
        
        if (!fileExists())
        {
            sendAbortCall("Task aborted, file does not exist.");
        }
        else
        {
            Meta.getInstance().removeFile(dirName, fileName);
            sendFinishCall();
        }
        
        unlock();
    }

    @Override
    public void release()
    {
    }

    @Override
    public void handleCall(Call call)
    {
        if (call.getTaskId() != getTaskId())
            return;

        if (call.getType() == Call.Type.LEASE)
        {
            renewLease();
        }
    }
    
    private void lock()
    {
        if (hasLock)
            return;
        Meta.getInstance().lock(dirName);
        hasLock = true;
    }

    private void unlock()
    {
        if (!hasLock)
            return;
        Meta.getInstance().unlock();
        hasLock = false;
    }
    
    private boolean fileExists()
    {
        return Meta.getInstance().containFile(dirName, fileName);
    }
    
    private void sendAbortCall(String reason)
    {
        Call back = new AbortCall(getTaskId(), reason);
        back.setInitiator(initiator);
        connector.sendCall(back);
        release();
        setFinish();
    }
    
    private void sendFinishCall()
    {
        Call back = new FinishCall(getTaskId());
        back.setInitiator(initiator);
        connector.sendCall(back);
        release();
        setFinish();
    }
}

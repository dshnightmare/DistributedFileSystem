package nameserver.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Storage;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.AppendFileCallC2N;
import common.observe.call.AppendFileCallN2C;
import common.observe.call.Call;
import common.observe.call.FinishCall;
import common.thread.TaskThread;

// TODO: If append file task failed, what can we do? The files could be
// inconsistent.
public class AppendFileTask
    extends TaskThread
{
    private String dirName;

    private String fileName;

    private Object syncRoot = new Object();

    private Connector connector;

    private String initiator;

    private boolean hasLock = false;

    private File file = null;

    public AppendFileTask(long sid, Call call, Connector connector)
    {
        super(sid);
        AppendFileCallC2N c = (AppendFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.connector = connector;
        this.initiator = c.getInitiator();
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
            file = Meta.getInstance().getFile(dirName, fileName);
            if (file.tryLockWrite(1, TimeUnit.SECONDS))
            {
                sendResponseCall();
                unlock();
                
                waitUntilTaskFinish();
                
                lock();
                file.unlockWrite();
                sendFinishCall();
            }
            else
            {
                sendAbortCall("Task aborted, someone is using the file.");
            }
        }

        unlock();
    }

    @Override
    public void release()
    {
        lock();
        file.unlockWrite();
        unlock();
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

        if (call.getType() == Call.Type.FINISH)
        {
            synchronized (syncRoot)
            {
                syncRoot.notify();
            }
            return;
        }
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

    private void sendResponseCall()
    {
        List<String> locations = new ArrayList<String>();
        for (Storage s : file.getLocations())
            locations.add(s.getAddress());
        Call back = new AppendFileCallN2C(locations);
        back.setInitiator(initiator);
        back.setTaskId(getTaskId());
        connector.sendCall(back);
    }

    private void sendFinishCall()
    {
        Call back = new FinishCall(getTaskId());
        back.setInitiator(initiator);
        connector.sendCall(back);
        release();
        setFinish();
    }

    private void waitUntilTaskFinish()
    {
        try
        {
            synchronized (syncRoot)
            {
                syncRoot.wait();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
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
}

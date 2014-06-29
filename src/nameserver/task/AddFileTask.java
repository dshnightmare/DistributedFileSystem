package nameserver.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.AddFileCallC2N;
import common.observe.call.AddFileCallN2C;
import common.observe.call.Call;
import common.observe.call.FinishCall;
import common.thread.TaskThread;
import common.util.IdGenerator;

public class AddFileTask
    extends TaskThread
{
    private int duplicate;

    private String dirName;

    private String fileName;

    private Object syncRoot = new Object();

    private Connector connector;

    private String initiator;

    /**
     * Indicates whether the directory is already existed before adding this
     * file. If the directory is already existed, we should not delete it when
     * task aborts, but if it isn't, we can consider this.
     */
    private boolean hasDir = false;

    private boolean hasLock = false;

    private File file = null;

    public AddFileTask(long sid, Call call, Connector connector, int duplicate)
    {
        super(sid);
        AddFileCallC2N c = (AddFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.connector = connector;
        this.initiator = c.getInitiator();
        this.duplicate = duplicate;
    }

    @Override
    public void run()
    {
        lock();

        if (fileExists())
        {
            sendAbortCall("Task aborted, there has been a directory/file with the same name.");
        }
        else
        {
            file = new File(fileName, IdGenerator.getInstance().getLongId());
            file.tryLockWrite(1, TimeUnit.SECONDS);

            addFileToMeta();
            sendResponseCall();
            unlock();
            
            waitUntilTaskFinish();
            
            lock();
            commit();
            file.unlockWrite();
            sendFinishCall();
        }

        unlock();
    }

    @Override
    public void release()
    {
        lock();

        removeFileFromMeta();

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
        if (Meta.getInstance().containDirectory(dirName))
            hasDir = true;
        else
        {
            hasDir = false;
            return false;
        }

        if (Meta.getInstance().containFile(dirName, fileName))
            return true;
        else
            return false;
    }

    private void addFileToMeta()
    {
        Meta.getInstance().addFile(dirName, file);

        List<Storage> storages =
            Status.getInstance().allocateStorage(duplicate);
        file.setLocations(storages);
        for (Storage s : storages)
            s.addFile(file);
    }

    private void removeFileFromMeta()
    {
        Meta.getInstance().removeFile(dirName, fileName);
        // The directory is invalid and doesn't exist before.
        if (!hasDir && !Meta.getInstance().isDirectoryValid(dirName))
            Meta.getInstance().removeDirectory(dirName);

        for (Storage s : file.getLocations())
            s.removeFile(file);
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
        List<Storage> storages = file.getLocations();
        List<String> locations = new ArrayList<String>();
        for (Storage s : storages)
            locations.add(s.getAddress());

        Call back = new AddFileCallN2C(locations);
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

    private void commit()
    {
        Directory dir = Meta.getInstance().getDirectory(dirName);
        if (null == dir)
            return;
        dir.setValid(true);
        file.setValid(true);
    }
}

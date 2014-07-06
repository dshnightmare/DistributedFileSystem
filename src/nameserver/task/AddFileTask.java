package nameserver.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nameserver.BackupUtil;
import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.status.Status;
import nameserver.status.Storage;
import common.network.Connector;
import common.call.Call;
import common.call.c2n.AddFileCallC2N;
import common.call.n2c.AddFileCallN2C;
import common.util.IdGenerator;
import common.util.Logger;

/**
 * Task of adding file.
 * 
 * @author lishunyang
 * @see NameServerTask
 */
public class AddFileTask
    extends NameServerTask
{
    /**
     * Logger.
     */
    private final static Logger logger = Logger.getLogger(AddFileTask.class);

    /**
     * Duplicate number of file.
     */
    private int duplicate;

    /**
     * File directory name.
     */
    private String dirName;

    /**
     * File name.
     */
    private String fileName;

    /**
     * Sync object which is used for synchronizing.
     */
    private Object syncRoot = new Object();

    /**
     * Indicates whether the directory is already existed before adding this
     * file. If the directory is already existed, we should not delete it when
     * task aborts, but if it isn't, we can consider this.
     */
    private boolean hasDir = false;

    /**
     * The file that we focus on.
     */
    private File file = null;

    /**
     * Construction method.
     * 
     * @param tid
     * @param call
     * @param connector
     * @param duplicate
     */
    public AddFileTask(long tid, Call call, Connector connector, int duplicate)
    {
        super(tid, call, connector);
        AddFileCallC2N c = (AddFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.duplicate = duplicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        final Meta meta = Meta.getInstance();
        final BackupUtil backup = BackupUtil.getInstance();

        synchronized (meta)
        {
            if (fileExists())
            {
                sendAbortCall("Task aborted, there has been a directory/file with the same name.");
                setFinish();
                return;
            }
            else
            {
                file =
                    new File(fileName, IdGenerator.getInstance().getLongId());
                // This must success, because we create this file.
                file.tryLockWrite(1, TimeUnit.SECONDS);

                logger.info("AddFileTask " + getTaskId() + " started.");
                backup.writeLogIssue(getTaskId(), Call.Type.ADD_FILE_C2N,
                    dirName + " " + fileName + " " + file.getId());

                // This should be a problem: if here comes an exception, then it
                // will never release the lock.
                addFileToMeta();
                sendResponseCall();
            }
        }

        waitUntilTaskFinish();

        if (isDead())
            return;

        synchronized (meta)
        {
            logger.info("AddFileTask " + getTaskId() + " commit.");
            backup.writeLogCommit(getTaskId());
            commit();
            file.unlockWrite();

            sendFinishCall();
            
            setFinish();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release()
    {
        setDead();

        synchronized (syncRoot)
        {
            syncRoot.notify();
        }

        synchronized (Meta.getInstance())
        {
            removeFileFromMeta();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleCall(Call call)
    {
        if (call.getToTaskId() != getTaskId())
            return;

        if (call.getType() == Call.Type.LEASE_C2N)
        {
            renewLease();
            return;
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

    /**
     * Test whether the file that client wants to add has existed.
     * 
     * @return
     */
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

    /**
     * Add file into meta structure.
     */
    private void addFileToMeta()
    {
        Meta.getInstance().addFile(dirName, file);

        List<Storage> storages =
            Status.getInstance().allocateStorage(duplicate);
        file.setLocations(storages);
        for (Storage s : storages)
            s.addFile(file);
    }

    /**
     * Remove file from meta structure.
     */
    private void removeFileFromMeta()
    {
        Meta.getInstance().removeFile(dirName, fileName);
        // The directory is invalid and doesn't exist before.
        if (!hasDir && !Meta.getInstance().isDirectoryValid(dirName))
            Meta.getInstance().removeDirectory(dirName);

        if (null == file)
            return;
        for (Storage s : file.getLocations())
            s.removeFile(file);
    }

    /**
     * Wait until task has finished.
     */
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

    /**
     * Send response call back to client.
     */
    private void sendResponseCall()
    {
    	logger.info("XXXXXXX" + Status.getInstance().getStorageNum());
        List<Storage> storages = file.getLocations();
        List<String> locations = new ArrayList<String>();
        for (Storage s : storages)
            locations.add(s.getId());

        String fileId = file.getId();
        Call back = new AddFileCallN2C(fileId, locations);
        sendCall(back);
    }

    /**
     * Commit task.
     * <p>
     * Once a task is committed, all works it has done won't be lost.
     */
    private void commit()
    {
        Directory dir = Meta.getInstance().getDirectory(dirName);
        if (null == dir)
            return;
        Meta.getInstance().setFileValid(dirName, fileName, true);
    }
}

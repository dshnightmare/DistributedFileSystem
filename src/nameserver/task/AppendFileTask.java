package nameserver.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nameserver.BackupUtil;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Storage;
import common.network.Connector;
import common.call.Call;
import common.call.c2n.AppendFileCallC2N;
import common.call.n2c.AppendFileCallN2C;
import common.util.Logger;

/**
 * Task of appending file.
 * 
 * @author lishunyang
 * @see NameServerTask
 */
public class AppendFileTask
    extends NameServerTask
{
    /**
     * Logger.
     */
    private final static Logger logger = Logger.getLogger(AppendFileTask.class);

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
     * The file that we focus on.
     */
    private File file = null;

    /**
     * Construction method.
     * 
     * @param tid
     * @param call
     * @param connector
     */
    public AppendFileTask(long tid, Call call, Connector connector)
    {
        super(tid, call, connector);
        AppendFileCallC2N c = (AppendFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
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

            if (!fileExists())
            {
                sendAbortCall("Task aborted, file does not exist.");
                setFinish();
                return;
            }
            else
            {
                logger.info("AppendFileTask " + getTaskId() + " started.");
                backup.writeLogIssue(getTaskId(), Call.Type.APPEND_FILE_C2N,
                    dirName + " " + fileName);

                file = Meta.getInstance().getFile(dirName, fileName);
                if (file.tryLockWrite(1, TimeUnit.SECONDS))
                {
                    sendResponseCall();
                }
                else
                {
                    sendAbortCall("Task aborted, someone is using the file.");
                    return;
                }
            }
        }

        waitUntilTaskFinish();

        if (isDead())
            return;

        synchronized (meta)
        {
            logger.info("AppendFileTask " + getTaskId() + " commit.");
            backup.writeLogCommit(getTaskId());

            file.updateVersion();
            file.unlockWrite();
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
        file.unlockWrite();
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
     * Test whether the file that client wants to append exists.
     * 
     * @return
     */
    private boolean fileExists()
    {
        return Meta.getInstance().containFile(dirName, fileName);
    }

    /**
     * Send response call back to client.
     */
    private void sendResponseCall()
    {
        List<String> locations = new ArrayList<String>();
        for (Storage s : file.getLocations())
            locations.add(s.getId());

        long newFileVersion = file.getVersion() + 1;
        String fileId = file.getId() + "-" + newFileVersion;

        Call back = new AppendFileCallN2C(fileId, locations);
        sendCall(back);
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
}

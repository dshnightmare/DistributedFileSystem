package nameserver.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nameserver.BackupUtil;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Storage;
import common.network.Connector;
import common.call.AbortCall;
import common.call.AppendFileCallC2N;
import common.call.AppendFileCallN2C;
import common.call.Call;
import common.call.FinishCall;
import common.thread.TaskThread;
import common.util.Logger;

// TODO: If append file task failed, what can we do? The files could be
// inconsistent.
public class AppendFileTask
    extends TaskThread
{
    private final static Logger logger = Logger.getLogger(AppendFileTask.class);

    private String dirName;

    private String fileName;

    private Object syncRoot = new Object();

    private Connector connector;

    private String initiator;

    private File file = null;

    private long remoteTaskId;

    public AppendFileTask(long tid, Call call, Connector connector)
    {
        super(tid);
        AppendFileCallC2N c = (AppendFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.connector = connector;
        this.initiator = c.getInitiator();
        this.remoteTaskId = call.getFromTaskId();
    }

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

        synchronized (meta)
        {
            logger.info("AppendFileTask " + getTaskId() + " commit.");
            backup.writeLogCommit(getTaskId());

            file.updateVersion();
            file.unlockWrite();
            setFinish();
            // sendFinishCall();
        }

    }

    @Override
    public void release()
    {
        file.unlockWrite();
    }

    @Override
    public void handleCall(Call call)
    {
        if (call.getToTaskId() != getTaskId())
            return;

        if (call.getType() == Call.Type.LEASE)
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

    private boolean fileExists()
    {
        return Meta.getInstance().containFile(dirName, fileName);
    }

    private void sendAbortCall(String reason)
    {
        Call back = new AbortCall(reason);
        back.setFromTaskId(getTaskId());
        back.setToTaskId(remoteTaskId);
        connector.sendCall(back);
        release();
        setFinish();
    }

    private void sendResponseCall()
    {
        List<String> locations = new ArrayList<String>();
        for (Storage s : file.getLocations())
            locations.add(s.getAddress());

        long newFileVersion = file.getVersion() + 1;
        String fileId = file.getId() + "-" + newFileVersion;

        Call back = new AppendFileCallN2C(fileId, locations);
        back.setFromTaskId(getTaskId());
        back.setToTaskId(remoteTaskId);
        back.setInitiator(initiator);
        connector.sendCall(back);
    }

    private void sendFinishCall()
    {
        Call back = new FinishCall();
        back.setFromTaskId(getTaskId());
        back.setToTaskId(remoteTaskId);
        back.setInitiator(initiator);
        connector.sendCall(back);
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
}

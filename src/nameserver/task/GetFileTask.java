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
import common.call.Call;
import common.call.GetFileCallC2N;
import common.call.GetFileCallN2C;
import common.thread.TaskThread;
import common.util.Logger;

public class GetFileTask
    extends TaskThread
{
    private final static Logger logger = Logger.getLogger(GetFileTask.class);

    private String dirName;

    private String fileName;

    private Connector connector;

    private Object syncRoot = new Object();

    private String initiator;

    private long clientTaskId;

    private File file = null;

    public GetFileTask(long tid, Call call, Connector connector)
    {
        super(tid);
        GetFileCallC2N c = (GetFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.connector = connector;
        this.initiator = c.getInitiator();
        this.clientTaskId = call.getClientTaskId();
    }

    @Override
    public void handleCall(Call call)
    {
        if (call.getTaskId() != getTaskId())
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
                logger.info("GetFileTask " + getTaskId() + " started.");
                backup.writeLogIssue(getTaskId(), Call.Type.GET_FILE_C2N,
                    dirName + " " + fileName);

                file = Meta.getInstance().getFile(dirName, fileName);
                if (file.tryLockRead(1, TimeUnit.SECONDS))
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
            file.unlockRead();
            setFinish();
            // sendFinishCall();
        }
    }

    @Override
    public void release()
    {
        file.unlockRead();
    }

    private boolean fileExists()
    {
        return Meta.getInstance().containFile(dirName, fileName);
    }

    private void sendAbortCall(String reason)
    {
        Call back = new AbortCall(getTaskId(), reason);
        back.setClientTaskId(clientTaskId);
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

        long newFileVersion = file.getVersion() + 1;
        String fileId = file.getId() + "-" + newFileVersion;

        Call back = new GetFileCallN2C(fileId, locations);
        back.setClientTaskId(clientTaskId);
        back.setInitiator(initiator);
        back.setTaskId(getTaskId());
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

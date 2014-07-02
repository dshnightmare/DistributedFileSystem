package nameserver.task;

import nameserver.BackupUtil;
import nameserver.meta.Meta;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.Call;
import common.observe.call.FinishCall;
import common.observe.call.RemoveFileCallC2N;
import common.thread.TaskThread;
import common.util.Logger;

public class RemoveFileTask
    extends TaskThread
{
    private final static Logger logger = Logger.getLogger(RemoveFileTask.class);

    private String dirName;

    private String fileName;

    private Connector connector;

    private String initiator;

    private long clientTaskId;

    public RemoveFileTask(long tid, Call call, Connector connector)
    {
        super(tid);
        RemoveFileCallC2N c = (RemoveFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.initiator = c.getInitiator();
        this.connector = connector;
        this.clientTaskId = call.getClientTaskId();
    }

    @Override
    public void run()
    {
        final BackupUtil backup = BackupUtil.getInstance();

        synchronized (Meta.getInstance())
        {
            if (!fileExists())
            {
                sendAbortCall("Task aborted, file does not exist.");
            }
            else
            {
                logger.info("RemoveFileTask " + getTaskId() + " started.");
                backup.writeLogIssue(getTaskId(), Call.Type.REMOVE_FILE_C2N,
                    dirName + " " + fileName);

                logger.info("RemoveFileTask " + getTaskId() + " commit.");
                backup.writeLogCommit(getTaskId());

                Meta.getInstance().removeFile(dirName, fileName);
                setFinish();
                // sendFinishCall();
            }
        }
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
        back.setClientTaskId(clientTaskId);
        back.setInitiator(initiator);
        connector.sendCall(back);
        release();
        setFinish();
    }

    private void sendFinishCall()
    {
        Call back = new FinishCall(getTaskId());
        back.setClientTaskId(clientTaskId);
        back.setInitiator(initiator);
        connector.sendCall(back);
    }
}

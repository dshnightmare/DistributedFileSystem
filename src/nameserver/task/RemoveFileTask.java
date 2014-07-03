package nameserver.task;

import nameserver.BackupUtil;
import nameserver.meta.Meta;
import common.network.Connector;
import common.call.Call;
import common.call.c2n.FinishCallC2N;
import common.call.c2n.RemoveFileCallC2N;
import common.call.n2c.AbortCallN2C;
import common.task.Task;
import common.util.Logger;

public class RemoveFileTask
    extends Task
{
    private final static Logger logger = Logger.getLogger(RemoveFileTask.class);

    private String dirName;

    private String fileName;

    private Connector connector;

    private String initiator;

    private long remoteTaskId;

    public RemoveFileTask(long tid, Call call, Connector connector)
    {
        super(tid);
        RemoveFileCallC2N c = (RemoveFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.initiator = c.getInitiator();
        this.connector = connector;
        this.remoteTaskId = call.getFromTaskId();
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
        if (call.getToTaskId() != getTaskId())
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
        Call back = new AbortCallN2C(reason);
        back.setFromTaskId(getTaskId());
        back.setToTaskId(remoteTaskId);
        back.setInitiator(initiator);
        connector.sendCall(back);
        release();
        setFinish();
    }

    private void sendFinishCall()
    {
        Call back = new FinishCallC2N();
        back.setFromTaskId(getTaskId());
        back.setToTaskId(remoteTaskId);
        back.setInitiator(initiator);
        connector.sendCall(back);
    }
}

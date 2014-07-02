package nameserver.task;

import nameserver.BackupUtil;
import nameserver.meta.Meta;
import common.network.Connector;
import common.call.AbortCall;
import common.call.Call;
import common.call.FinishCall;
import common.call.MoveFileCallC2N;
import common.task.Task;
import common.util.Logger;

public class MoveFileTask
    extends Task
{
    private final static Logger logger = Logger.getLogger(MoveFileTask.class);

    private String oldDirName;

    private String oldFileName;

    private String newDirName;

    private String newFileName;

    private Connector connector;

    private String initiator;
    
    private long remoteTaskId;

    public MoveFileTask(long tid, Call call, Connector connector)
    {
        super(tid);
        MoveFileCallC2N c = (MoveFileCallC2N) call;
        this.oldDirName = c.getOldDirName();
        this.oldFileName = c.getOldFileName();
        this.newDirName = c.getNewDirName();
        this.newFileName = c.getNewFileName();
        this.connector = connector;
        this.initiator = c.getInitiator();
        this.remoteTaskId = call.getFromTaskId();
    }

    @Override
    public void run()
    {
        final BackupUtil backup = BackupUtil.getInstance();
        
        synchronized (Meta.getInstance())
        {
            if (!oldFileExists())
            {
                sendAbortCall("Task aborted, old file does not exist.");
            }
            else if (newFileExists())
            {
                sendAbortCall("Task aborted, new file has arealdy existed.");
            }
            else
            {
                logger.info("MoveFileTask " + getTaskId() + " started.");
                backup.writeLogIssue(
                    getTaskId(),
                    Call.Type.MOVE_FILE_C2N,
                    oldDirName + " " + oldFileName + " " + newDirName + " "
                        + newFileName);

                logger.info("MoveFileTask " + getTaskId() + " commit.");
                backup.writeLogCommit(getTaskId());

                Meta.getInstance().renameFile(oldDirName, oldFileName,
                    newDirName, newFileName);
                setFinish();
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

    private boolean oldFileExists()
    {
        if (Meta.getInstance().containFile(oldDirName, oldFileName))
            return true;
        else
            return false;
    }

    private boolean newFileExists()
    {
        if (Meta.getInstance().containFile(newDirName, newFileName))
            return true;
        else
            return false;
    }

    private void sendAbortCall(String reason)
    {
        Call back = new AbortCall(reason);
        back.setFromTaskId(getTaskId());
        back.setToTaskId(remoteTaskId);
        back.setInitiator(initiator);
        connector.sendCall(back);
        release();
        setFinish();
    }
}

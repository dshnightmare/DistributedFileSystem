package nameserver.task;

import nameserver.BackupUtil;
import nameserver.meta.Meta;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.Call;
import common.observe.call.FinishCall;
import common.observe.call.MoveFileCallC2N;
import common.thread.TaskThread;
import common.util.Logger;

public class MoveFileTask
    extends TaskThread
{
    private final static Logger logger = Logger.getLogger(MoveFileTask.class);

    private String oldDirName;

    private String oldFileName;

    private String newDirName;

    private String newFileName;

    private Connector connector;

    private String initiator;
    
    private long clientTaskId;

    public MoveFileTask(long sid, Call call, Connector connector)
    {
        super(sid);
        MoveFileCallC2N c = (MoveFileCallC2N) call;
        this.oldDirName = c.getOldDirName();
        this.oldFileName = c.getOldFileName();
        this.newDirName = c.getNewDirName();
        this.newFileName = c.getNewFileName();
        this.connector = connector;
        this.initiator = c.getInitiator();
        this.clientTaskId = call.getClientTaskId();
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
                sendFinishCall();
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
        setFinish();
    }
}

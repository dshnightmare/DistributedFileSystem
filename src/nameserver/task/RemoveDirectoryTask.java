package nameserver.task;

import nameserver.BackupUtil;
import nameserver.meta.Meta;
import common.call.Call;
import common.call.c2n.RemoveDirectoryCallC2N;
import common.network.Connector;
import common.util.Logger;

public class RemoveDirectoryTask
    extends NameServerTask
{
    /**
     * Logger.
     */
    private final static Logger logger = Logger
        .getLogger(RemoveDirectoryTask.class);

    /**
     * File directory name.
     */
    private String dirName;

    public RemoveDirectoryTask(long tid, Call call, Connector connector)
    {
        super(tid, call, connector);
        RemoveDirectoryCallC2N c = (RemoveDirectoryCallC2N) call;
        this.dirName = c.getDirectoryName();
    }

    @Override
    public void run()
    {
        final BackupUtil backup = BackupUtil.getInstance();

        synchronized (Meta.getInstance())
        {
            if (!directoryExists())
            {
                sendAbortCall("Task aborted, directory does not exist.");
            }
            else
            {
                logger.info("RemoveDirectoryTask " + getTaskId() + " started.");
                backup.writeLogIssue(getTaskId(),
                    Call.Type.REMOVE_DIRECTORY_C2N, dirName);

                logger.info("RemoveDirectoryTask " + getTaskId() + " commit.");
                backup.writeLogCommit(getTaskId());

                sendFinishCall();
                Meta.getInstance().removeDirectory(dirName);
            }

            setFinish();
        }
    }

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
    }

    @Override
    public void release()
    {
        setDead();
    }

    /**
     * Test whether the directory that client wants to remove.
     * 
     * @return
     */
    private boolean directoryExists()
    {
        if (Meta.getInstance().containDirectory(dirName))
            return true;
        else
            return false;
    }
}

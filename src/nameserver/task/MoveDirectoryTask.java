package nameserver.task;

import nameserver.BackupUtil;
import nameserver.meta.Meta;
import common.call.Call;
import common.call.c2n.MoveDirectoryCallC2N;
import common.network.Connector;
import common.util.Logger;

/**
 * Task of moving directory from one place to another, or just renaming it.
 * 
 * @author lishunyang
 * @see NameServerTask
 */
public class MoveDirectoryTask
    extends NameServerTask
{
    /**
     * Logger.
     */
    private final static Logger logger = Logger
        .getLogger(MoveDirectoryTask.class);

    /**
     * Old directory name.
     */
    private String oldDirName;

    /**
     * New directory name.
     */
    private String newDirName;

    /**
     * Construction method.
     * 
     * @param tid
     * @param call
     * @param connector
     */
    public MoveDirectoryTask(long tid, Call call, Connector connector)
    {
        super(tid, call, connector);

        MoveDirectoryCallC2N c = (MoveDirectoryCallC2N) call;
        this.oldDirName = c.getOldDirectoryName();
        this.newDirName = c.getNewDirectoryName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        final BackupUtil backup = BackupUtil.getInstance();

        synchronized (Meta.getInstance())
        {
            if (!oldDirectoryExists())
            {
                sendAbortCall("Task aborted, old directory does not exist.");
            }
            else if (newDirectoryExists())
            {
                sendAbortCall("Task aborted, new directory has arealdy existed.");
            }
            else
            {
                logger.info("MoveDirectoryTask " + getTaskId() + " started.");
                backup.writeLogIssue(getTaskId(), Call.Type.MOVE_DIRECTORY_C2N,
                    oldDirName + " " + newDirName);

                logger.info("MoveDirectoryTask " + getTaskId() + " commit.");
                backup.writeLogCommit(getTaskId());

                sendFinishCall();
                Meta.getInstance().renameDirectory(oldDirName, newDirName);
            }

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
    }

    /**
     * Test whether the directory that client wants to move exists.
     * 
     * @return
     */
    private boolean oldDirectoryExists()
    {
        if (Meta.getInstance().containDirectory(oldDirName))
            return true;
        else
            return false;
    }

    /**
     * Test whether the new directory has already existed.
     * 
     * @return
     */
    private boolean newDirectoryExists()
    {
        if (Meta.getInstance().containDirectory(newDirName))
            return true;
        else
            return false;
    }
}

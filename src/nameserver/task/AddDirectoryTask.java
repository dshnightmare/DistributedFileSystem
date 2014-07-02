package nameserver.task;

import nameserver.BackupUtil;
import nameserver.meta.Directory;
import nameserver.meta.Meta;
import common.call.AbortCall;
import common.call.AddDirectoryCallC2N;
import common.call.Call;
import common.network.Connector;
import common.task.Task;
import common.util.Logger;

public class AddDirectoryTask
    extends Task
{
    private final static Logger logger = Logger
        .getLogger(AddDirectoryTask.class);

    private String dirName;

    private Connector connector;

    private String initiator;

    private long remoteTaskId;

    public AddDirectoryTask(long tid, Call call, Connector connector)
    {
        super(tid);
        AddDirectoryCallC2N c = (AddDirectoryCallC2N) call;
        this.dirName = c.getDirName();
        this.connector = connector;
        this.initiator = c.getInitiator();
        this.remoteTaskId = call.getFromTaskId();
    }

    @Override
    public void handleCall(Call call)
    {
    }

    @Override
    public void run()
    {
        final Meta meta = Meta.getInstance();
        final BackupUtil backup = BackupUtil.getInstance();

        synchronized (meta)
        {
            if (directoryExists())
            {
                sendAbortCall("Task aborted, there has been a directory with the same name.");
                return;
            }
            else
            {
                Directory dir = new Directory(dirName);

                logger.info("AddDirectoryTask " + getTaskId() + " started.");
                backup.writeLogIssue(getTaskId(), Call.Type.ADD_FILE_C2N,
                    dirName + " " + dirName);

                logger.info("AddDirectoryTask " + getTaskId() + " commit.");
                backup.writeLogCommit(getTaskId());

                meta.addDirectory(dir);
                setFinish();
            }
        }
    }

    @Override
    public void release()
    {
    }

    private boolean directoryExists()
    {
        if (Meta.getInstance().containDirectory(dirName))
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

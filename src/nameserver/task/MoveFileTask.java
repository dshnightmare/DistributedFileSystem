package nameserver.task;

import java.nio.channels.SocketChannel;

import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.Call;
import common.observe.call.FinishCall;
import common.observe.call.MoveFileCallC2N;
import common.thread.TaskThread;

public class MoveFileTask
    extends TaskThread
{
    private String oldDirName;

    private String oldFileName;

    private String newDirName;

    private String newFileName;

    private Meta meta;

    private Connector connector;

    private String initiator;

    public MoveFileTask(long sid, Call call, Meta meta, Connector connector)
    {
        super(sid);
        this.meta = meta;
        MoveFileCallC2N c = (MoveFileCallC2N) call;
        this.oldDirName = c.getOldDirName();
        this.oldFileName = c.getOldFileName();
        this.newDirName = c.getNewDirName();
        this.newFileName = c.getNewFileName();
        this.connector = connector;
        this.initiator = c.getInitiator();
    }

    @Override
    public void run()
    {
        Call back = null;

        if (!meta.contains(oldDirName))
        {
            back =
                new AbortCall(getTaskId(), "Task aborted, file does not exist.");
            back.setInitiator(initiator);;
            connector.sendCall(back);
            setFinish();
            return;
        }

        Directory dir = meta.getDirectory(oldDirName);
        if (!dir.contains(oldFileName))
        {
            back =
                new AbortCall(getTaskId(), "Task aborted, file does not exist.");
            back.setInitiator(initiator);;
            connector.sendCall(back);
            setFinish();
            return;
        }

        File file = dir.getFile(oldFileName);
        if (oldFileName.compareTo(newFileName) != 0)
            file.setName(newFileName);
        if (meta.contains(newDirName))
        {
            dir.removeFile(file);
            dir = meta.getDirectory(newDirName);
        }
        else
            dir = new Directory(newDirName);
        dir.addFile(file);

        back = new FinishCall(getTaskId());
        back.setInitiator(initiator);;
        connector.sendCall(back);

        // TODO: Finished! Release locks.

        setFinish();
    }

    @Override
    public void release()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleCall(Call call)
    {
        if (call.getTaskId() != getTaskId())
            return;

        if (call.getType() == Call.Type.HEARTBEAT_S2N)
        {
            renewLease();
        }
    }
}

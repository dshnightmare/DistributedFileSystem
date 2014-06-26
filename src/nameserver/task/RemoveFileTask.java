package nameserver.task;

import java.nio.channels.SocketChannel;

import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Storage;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.Call;
import common.observe.call.FinishCall;
import common.observe.call.RemoveFileCallC2N;
import common.thread.TaskThread;

public class RemoveFileTask
    extends TaskThread
{
    private String dirName;

    private String fileName;

    private Meta meta;

    private Connector connector;

    private SocketChannel channel;

    public RemoveFileTask(long sid, Call call, Meta meta, Connector connector)
    {
        super(sid);
        this.meta = meta;
        RemoveFileCallC2N c = (RemoveFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.channel = c.getChannel();
        this.connector = connector;
    }

    @Override
    public void run()
    {
        Call back = null;

        if (!meta.contains(dirName))
        {
            back =
                new AbortCall(getTaskId(), "Task aborted, file does not exist.");
            back.setChannel(channel);
            connector.sendCall(back);
            setFinish();
            return;
        }

        Directory dir = meta.getDirectory(dirName);
        if (!dir.contains(fileName))
        {
            back =
                new AbortCall(getTaskId(), "Task aborted, file does not exist.");
            back.setChannel(channel);
            connector.sendCall(back);
            setFinish();
            return;
        }

        File file = dir.getFile(fileName);
        for (Storage s : file.getLocations())
            s.removeFile(file);
        dir.removeFile(file);

        // TODO: Finished! Release locks.

        back = new FinishCall(getTaskId());
        back.setChannel(channel);
        connector.sendCall(back);

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

package nameserver.task;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Storage;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.AppendFileCallC2N;
import common.observe.call.AppendFileCallN2C;
import common.observe.call.Call;
import common.observe.call.FinishCall;
import common.thread.TaskThread;

public class AppendFileTask
    extends TaskThread
{
    private String dirName;

    private String fileName;

    private Meta meta;

    private Object syncRoot = new Object();

    private Connector connector;

    private SocketChannel channel;

    public AppendFileTask(long sid, Call call, Meta meta, Connector connector)
    {
        super(sid);
        this.meta = meta;
        AppendFileCallC2N c = (AppendFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.connector = connector;
        this.channel = c.getChannel();
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
        List<String> locations = new ArrayList<String>();
        for (Storage s : file.getLocations())
            locations.add(s.getAddress());
        back = new AppendFileCallN2C(locations);
        back.setChannel(channel);
        back.setTaskId(getTaskId());
        connector.sendCall(back);

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

        if (call.getType() == Call.Type.FINISH)
        {
            synchronized (syncRoot)
            {
                syncRoot.notify();
            }
            return;
        }
    }
}
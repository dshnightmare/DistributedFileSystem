package nameserver.task;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.AddFileCallC2N;
import common.observe.call.AddFileCallN2C;
import common.observe.call.Call;
import common.observe.call.FinishCall;
import common.thread.TaskThread;
import common.util.IdGenerator;

public class AddFileTask
    extends TaskThread
{
    private int duplicate = 1;

    private String dirName;

    private String fileName;

    private Meta meta;

    private Status status;

    private Object syncRoot = new Object();

    private Connector connector;

    private SocketChannel channel;

    public AddFileTask(long sid, Call call, Meta meta, Status status,
        Connector connector)
    {
        super(sid);
        this.meta = meta;
        this.status = status;
        AddFileCallC2N c = (AddFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
        this.connector = connector;
        this.channel = c.getChannel();
    }

    @Override
    public void run()
    {
        Call back = null;

        if (meta.contains(dirName + fileName))
        {
            back =
                new AbortCall(getTaskId(),
                    "Task aborted, there has been a directory with the same name.");
            back.setChannel(channel);
            connector.sendCall(back);
            setFinish();
            return;
        }

        Directory dir = null;
        boolean hasDir = false;
        if (meta.contains(dirName))
        {
            hasDir = true;
            dir = meta.getDirectory(dirName);
            if (dir.contains(fileName))
            {
                back =
                    new AbortCall(getTaskId(),
                        "Task aborted, there has been a file with the same name.");
                back.setChannel(channel);
                connector.sendCall(back);
                setFinish();
                return;
            }
        }
        else
        {
            hasDir = false;
            dir = new Directory(dirName);
        }

        File file = new File(fileName, IdGenerator.getInstance().getLongId());
        List<Storage> storages = status.allocateStorage(duplicate);
        file.setLocations(storages);
        List<String> locations = new ArrayList<String>();
        for (Storage s : storages)
            locations.add(s.getAddress());
        back = new AddFileCallN2C(locations);
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

        dir.addFile(file);
        if (!hasDir)
        {
            meta.addDirectory(dir);
        }

        back = new FinishCall(getTaskId());
        back.setChannel(channel);
        connector.sendCall(back);

        setFinish();
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

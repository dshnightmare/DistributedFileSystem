package nameserver.task;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nameserver.meta.File;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import common.network.Connector;
import common.observe.call.Call;
import common.observe.call.HeartbeatCallS2N;
import common.observe.call.MigrateFileCallN2S;
import common.thread.TaskThread;
import common.util.IdGenerator;

public class HeartbeatTask
    extends TaskThread
{
    private Status status;

    private Connector connector;

    private SocketChannel channel;

    private String address;
    
    private Object syncRoot = new Object();

    public HeartbeatTask(long tid, Call call, Status status, Connector connector)
    {
        super(tid);
        HeartbeatCallS2N c = (HeartbeatCallS2N) call;
        this.channel = c.getChannel();
        this.address = c.getAddress();
        this.status = status;
        this.connector = connector;
    }

    @Override
    public void run()
    {
        Storage storage = status.getStorage(address);
        status.updateTimestamp(storage);
        Map<Storage, List<File>> migrateFiles = storage.cleanMigrateFiles();
        Map<String, List<Long>> mf = new HashMap<String, List<Long>>();

        for (Entry<Storage, List<File>> e : migrateFiles.entrySet())
        {
            List<Long> fi = new ArrayList<Long>();

            for (File f : e.getValue())
            {
                fi.add(f.getId());
            }
            mf.put(e.getKey().getAddress(), fi);
        }
        if (!migrateFiles.isEmpty())
        {
            Call back =
                new MigrateFileCallN2S(IdGenerator.getInstance().getLongId(),
                    mf);
            back.setChannel(channel);
            connector.sendCall(back);
        }
        
        synchronized (syncRoot)
        {
            try
            {
                syncRoot.wait();
            }
            catch (InterruptedException e1)
            {
                e1.printStackTrace();
            }
        }
        
        for (List<File> fl : migrateFiles.values())
        {
            for (File f : fl)
            {
                storage.addFile(f);
                f.addLocation(storage);
            }
        }
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

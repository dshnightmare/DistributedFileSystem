package nameserver.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nameserver.heartbeat.HeartbeatEvent;
import nameserver.heartbeat.HeartbeatListener;
import nameserver.meta.File;
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
    private final Storage storage;

    private final Connector connector;

    private final String initiator;

    private final long period;
    
    private List<HeartbeatListener> listeners = new ArrayList<HeartbeatListener>();

    public HeartbeatTask(long tid, String initiator, Storage storage,
        Connector connector, long period)
    {
        super(tid);
        this.initiator = initiator;
        this.storage = storage;
        this.connector = connector;
        this.period = period;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Thread.sleep(period);
                long currentTime = System.currentTimeMillis();
                if ((currentTime - storage.getHearbeatTime()) > (period * 2 ))
                {
                    fireEvent(new HeartbeatEvent(storage));
                    break;
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
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
            // refresh heartbeat task lease.
            renewLease();

            // refresh storage server heartbeaet timestamp
            storage.setHeartbeatTime(System.currentTimeMillis());

            // update migrated files
            HeartbeatCallS2N c = (HeartbeatCallS2N) call;
            Map<String, List<Long>> migratedFiles = c.getMigratedFiles();
            storage.removeMigrateFiles(migratedFiles);

            // As to heartbeaet call, name server always send the migration call
            // back to storage server. So, if storage server doesn't receive the
            // migration call, it will realize he is dead and should register
            // again.
            Map<Storage, List<File>> migrateFiles = storage.getMigrateFiles();
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

            Call back =
                new MigrateFileCallN2S(IdGenerator.getInstance().getLongId(),
                    mf);
            back.setInitiator(initiator);
            connector.sendCall(back);

            return;
        }
    }
    
    public void addHeartbeatListener(HeartbeatListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeHeartbeatListener(HeartbeatListener listener)
    {
        listeners.remove(listener);
    }
    
    private void fireEvent(HeartbeatEvent event)
    {
        for (HeartbeatListener l : listeners)
            l.handleHeatbeatEvent(event);
    }
}

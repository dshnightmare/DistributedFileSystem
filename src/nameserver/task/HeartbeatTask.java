package nameserver.task;

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
import common.observe.call.RegistrationCallS2N;
import common.observe.event.TaskEvent;
import common.thread.TaskThread;
import common.util.IdGenerator;

public class HeartbeatTask
    extends TaskThread
{
    private final Storage storage;

    private final Connector connector;

    private final String initiator;

    private final long period;

    public HeartbeatTask(long tid, Call call, Connector connector, long period)
    {
        super(tid);
        RegistrationCallS2N c = (RegistrationCallS2N) call;
        this.initiator = c.getInitiator();
        this.storage = Status.getInstance().getStorage(c.getAddress());
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
                if ((currentTime - storage.getHearbeatTime()) > (period * 2))
                {
                    fireEvent(new TaskEvent(TaskEvent.Type.HEARTBEAT_FATAL,
                        this));
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

    public Storage getStorage()
    {
        return storage;
    }
}

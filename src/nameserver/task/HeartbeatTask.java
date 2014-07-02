package nameserver.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import nameserver.meta.File;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import common.network.Connector;
import common.call.Call;
import common.call.FinishCall;
import common.call.HeartbeatCallS2N;
import common.call.MigrateFileCallN2S;
import common.call.RegistrationCallS2N;
import common.event.TaskEvent;
import common.thread.TaskThread;
import common.util.IdGenerator;

public class HeartbeatTask
    extends TaskThread
{
    private Storage storage;

    private final String address;

    private final Connector connector;

    private final String initiator;

    /**
     * How many seconds between two adjacent heartbeat check.
     */
    private final long period;

    public HeartbeatTask(long tid, Call call, Connector connector, long period)
    {
        super(tid);
        // Notice that the type is RegistrationCall.
        RegistrationCallS2N c = (RegistrationCallS2N) call;
        this.initiator = c.getInitiator();
        this.address = c.getAddress();
        this.connector = connector;
        this.period = period;
    }

    @Override
    public void run()
    {
        this.storage =
            new Storage(IdGenerator.getInstance().getLongId(), address);
        Status.getInstance().addStorage(storage);
        // As for registration, send a finish call to notify storage server.
        sendFinishCall();

        while (true)
        {
            try
            {
                TimeUnit.SECONDS.sleep(period);

                if (longTimeNoSee())
                {
                    fireEvent(new TaskEvent(TaskEvent.Type.HEARTBEAT_FATAL,
                        this));
                    break;
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                break;
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
            updateHeartbeatTimestamp();

            removeMigratedFilesFromMigrateList(((HeartbeatCallS2N) call)
                .getMigratedFiles());

            sendMigrationCall();
            return;
        }
    }

    public Storage getStorage()
    {
        return storage;
    }

    private boolean longTimeNoSee()
    {
        final long currentTime = System.currentTimeMillis();
        if ((currentTime - storage.getHearbeatTime()) > (period * 2))
            return true;
        return false;
    }

    private void updateHeartbeatTimestamp()
    {
        storage.setHeartbeatTime(System.currentTimeMillis());
    }

    private void removeMigratedFilesFromMigrateList(
        Map<String, List<Long>> migratedFiles)
    {
        storage.removeMigrateFiles(migratedFiles);
    }

    private void sendMigrationCall()
    {
        // As to heartbeaet call, name server always send the migration call
        // back to storage server. So, if storage server doesn't receive the
        // migration call, it will realize he is dead and should register
        // again.
        Map<Storage, List<File>> migrateFiles = storage.getMigrateFiles();
        Map<String, List<Long>> rawMigrateFiles =
            new HashMap<String, List<Long>>();

        for (Entry<Storage, List<File>> e : migrateFiles.entrySet())
        {
            List<Long> fileList = new ArrayList<Long>();

            for (File f : e.getValue())
            {
                fileList.add(f.getId());
            }
            rawMigrateFiles.put(e.getKey().getAddress(), fileList);
        }

        Call back = new MigrateFileCallN2S(getTaskId(), rawMigrateFiles);
        back.setInitiator(initiator);
        connector.sendCall(back);
    }

    private void sendFinishCall()
    {
        Call back = new FinishCall(getTaskId());
        back.setInitiator(initiator);
        connector.sendCall(back);
    }
}

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
import common.call.n2s.MigrateFileCallN2S;
import common.call.s2n.HeartbeatCallS2N;
import common.event.TaskEvent;
import common.util.Logger;

public class HeartbeatTask
    extends NameServerTask
{

    private final static Logger logger = Logger.getLogger(HeartbeatTask.class);

    private Storage storage;

    /**
     * How many seconds between two adjacent heartbeat check.
     */
    private final long period;

    public HeartbeatTask(long tid, Call call, Connector connector, long period)
    {
        super(tid, call, connector);
        // Notice that the type is RegistrationCall.
        this.period = period;
    }

    @Override
    public void run()
    {
        final Status status = Status.getInstance();

        synchronized (status)
        {
            this.storage =
                new Storage(getInitiator());
            Status.getInstance().addStorage(storage);
        }

        logger.info("New storage server registered.");
        // As for registration, send a migration call to notify storage server.
        sendMigrationCall();

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
        logger.info("Heartbeat Task receive a call: " + call.getType());
        if (call.getToTaskId() != getTaskId())
            return;

        if (call.getType() == Call.Type.HEARTBEAT_S2N)
        {
            updateHeartbeatTimestamp();
            logger.info("Heartbeat update storage server " + storage.getId()
                + " heartbeat timestamp: " + storage.getHearbeatTime());

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
        Map<String, List<String>> migratedFiles)
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
        Map<String, List<String>> rawMigrateFiles =
            new HashMap<String, List<String>>();

        for (Entry<Storage, List<File>> e : migrateFiles.entrySet())
        {
            List<String> fileList = new ArrayList<String>();

            for (File f : e.getValue())
            {
                fileList.add(f.getId());
            }
            rawMigrateFiles.put(e.getKey().getId(), fileList);
        }

        Call back = new MigrateFileCallN2S(rawMigrateFiles);
        sendCall(back);
    }
}

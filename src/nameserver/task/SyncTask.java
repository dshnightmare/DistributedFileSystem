package nameserver.task;

import java.util.List;

import nameserver.meta.File;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.Call;
import common.observe.call.SyncCallN2S;
import common.observe.call.SyncCallS2N;
import common.thread.TaskThread;

public class SyncTask
    extends TaskThread
{
    private String address;

    private String initiator;

    private Connector connector;

    private List<Long> files;

    public SyncTask(long sid, Call call, Connector connector)
    {
        super(sid);
        SyncCallS2N c = (SyncCallS2N) call;
        this.address = c.getAddress();
        this.initiator = c.getInitiator();
        this.files = c.getFiles();
        this.connector = connector;
    }

    @Override
    public void run()
    {
        Call back = null;

        if (!Status.getInstance().contains(address))
        {
            back =
                new AbortCall(getTaskId(),
                    "Task aborted, unidentified storage server.");
            back.setInitiator(initiator);;
            connector.sendCall(back);
            setFinish();
            return;
        }

        Storage storage = Status.getInstance().getStorage(address);
        for (File f : storage.getFiles())
        {
            if (files.contains(f.getId()))
                files.remove(f.getId());
        }

        back = new SyncCallN2S(files);
        back.setInitiator(initiator);;
        back.setTaskId(getTaskId());
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

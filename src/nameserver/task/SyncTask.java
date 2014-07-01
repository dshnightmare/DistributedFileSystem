package nameserver.task;

import java.util.ArrayList;
import java.util.List;

import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Status;
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

    private int duplicate;

    public SyncTask(long sid, Call call, Connector connector, int duplicate)
    {
        super(sid);
        SyncCallS2N c = (SyncCallS2N) call;
        this.address = c.getAddress();
        this.initiator = c.getInitiator();
        this.files = c.getFiles();
        this.connector = connector;
        this.duplicate = duplicate;
    }

    @Override
    public void run()
    {
        synchronized (Meta.getInstance())
        {
            if (!storageExists())
            {
                sendAbortCall("Task aborted, unidentified storage server.");
            }
            else
            {
                List<Long> removeList = new ArrayList<Long>();
                for (Long l : files)
                {
                    File file = Meta.getInstance().getFile(l);
                    if (null == file)
                        removeList.add(l);
                    else
                    {
                        if (file.getLocationsCount() > duplicate)
                            removeList.add(l);
                        else
                            file.addLocation(Status.getInstance().getStorage(
                                address));
                    }
                }
                sendResponseCall(removeList);
            }
        }
    }

    @Override
    public void release()
    {
    }

    @Override
    public void handleCall(Call call)
    {
    }

    private void sendAbortCall(String reason)
    {
        Call back = new AbortCall(getTaskId(), reason);
        back.setInitiator(initiator);
        connector.sendCall(back);
        release();
        setFinish();
    }

    private boolean storageExists()
    {
        return Status.getInstance().contains(address);
    }

    private void sendResponseCall(List<Long> removeList)
    {

        Call back = new SyncCallN2S(removeList);
        back.setInitiator(initiator);
        back.setTaskId(getTaskId());
        connector.sendCall(back);

        setFinish();
    }
}

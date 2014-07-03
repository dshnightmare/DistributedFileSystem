package nameserver.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Storage;
import common.network.Connector;
import common.call.Call;
import common.call.c2n.GetFileCallC2N;
import common.call.n2c.GetFileCallN2C;
import common.util.Logger;

public class GetFileTask
    extends NameServerTask
{
    private final static Logger logger = Logger.getLogger(GetFileTask.class);

    private String dirName;

    private String fileName;

    private Object syncRoot = new Object();

    private File file = null;

    public GetFileTask(long tid, Call call, Connector connector)
    {
        super(tid, call, connector);
        GetFileCallC2N c = (GetFileCallC2N) call;
        this.dirName = c.getDirName();
        this.fileName = c.getFileName();
    }

    @Override
    public void run()
    {
        final Meta meta = Meta.getInstance();

        synchronized (meta)
        {

            if (!fileExists())
            {
                sendAbortCall("Task aborted, file does not exist.");
                setFinish();
                return;
            }
            else
            {
                logger.info("GetFileTask " + getTaskId() + " started.");

                file = Meta.getInstance().getFile(dirName, fileName);
                if (file.tryLockRead(1, TimeUnit.SECONDS))
                {
                    sendResponseCall();
                }
                else
                {
                    sendAbortCall("Task aborted, someone is using the file.");
                    return;
                }
            }
        }

        waitUntilTaskFinish();

        if (isDead())
            return;

        synchronized (meta)
        {
            logger.info("GetFileTask " + getTaskId() + " commit.");

            file.updateVersion();
            file.unlockRead();
            setFinish();
        }
    }

    @Override
    public void release()
    {
        setDead();
        synchronized (syncRoot)
        {
            syncRoot.notify();
        }
        file.unlockRead();
    }

    @Override
    public void handleCall(Call call)
    {
        if (call.getToTaskId() != getTaskId())
            return;

        if (call.getType() == Call.Type.LEASE_C2N)
        {
            renewLease();
            return;
        }

        if (call.getType() == Call.Type.FINISH_C2N)
        {
            synchronized (syncRoot)
            {
                syncRoot.notify();
            }
            return;
        }
    }

    private boolean fileExists()
    {
        return Meta.getInstance().containFile(dirName, fileName);
    }

    private void sendResponseCall()
    {
        List<String> locations = new ArrayList<String>();
        for (Storage s : file.getLocations())
            locations.add(s.getAddress());

        String fileId = file.getId() + "-" + file.getVersion();

        Call back = new GetFileCallN2C(fileId, locations);
        sendCall(back);
    }

    private void waitUntilTaskFinish()
    {
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
    }
}

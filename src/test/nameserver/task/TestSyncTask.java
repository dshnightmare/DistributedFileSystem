package test.nameserver.task;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.RegisterStorageTask;
import nameserver.task.SyncTask;
import common.network.ServerConnector;
import common.network.StorageConnector;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.RegistrationCallS2N;
import common.observe.call.SyncCallN2S;
import common.observe.call.SyncCallS2N;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThread;

public class TestSyncTask
    extends TestCase
{
    private static ServerConnector NConnector;

    private static StorageConnector SConnector;

    @Override
    protected void setUp()
    {
        NConnector = ServerConnector.getInstance();
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        SConnector = StorageConnector.getInstance();
        SConnector.addListener(new SCallListener());
        NConnector.addListener(new NCallListener());
    }

    public void testTask()
    {
        File file = new File("b", 1);
        Storage storage = new Storage(1, "localhost");
        storage.addFile(file);

        List<Long> files = new ArrayList<Long>();
        files.add((long) 1);
        files.add((long) 2);
        SyncCallS2N call = new SyncCallS2N("localhost", files);

        SConnector.sendCall(call);

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void tearDown()
    {
    }

    private class NCallListener
        implements CallListener
    {
        @Override
        public void handleCall(Call call)
        {
            System.out.println("Server received a call: " + call.getType());
            TaskThread task = new SyncTask(1, call, NConnector);
            new Thread(task).start();
        }
    }

    private class SCallListener
        implements CallListener
    {

        @Override
        public void handleCall(Call call)
        {
            System.out.println("Server sent a call: " + call.getType());
            if (Call.Type.SYNC_N2S == call.getType())
            {
                SyncCallN2S c = (SyncCallN2S) call;
                for (Long l : c.getFiles())
                    System.out.println(l);
            }
        }
    }
}

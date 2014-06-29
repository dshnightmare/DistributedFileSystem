package test.nameserver.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.meta.File;
import nameserver.meta.Storage;
import nameserver.task.SyncTask;
import common.network.ServerConnector;
import common.network.XConnector;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.SyncCallN2S;
import common.observe.call.SyncCallS2N;
import common.thread.TaskThread;
import common.util.Configuration;

public class TestSyncTask
    extends TestCase
{
    private static ServerConnector NConnector;

    private static XConnector SConnector;

    @Override
    protected void setUp()
    {
        NConnector = ServerConnector.getInstance();
        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        SConnector = XConnector.getInstance();
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
            TimeUnit.SECONDS.sleep(1);
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
            TaskThread task =
                new SyncTask(1, call, NConnector, Configuration.getInstance()
                    .getInteger(Configuration.DUPLICATE_KEY));
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

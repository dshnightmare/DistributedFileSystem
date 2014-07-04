package test.nameserver.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.SyncTask;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.call.Call;
import common.call.CallListener;
import common.call.n2s.SyncCallN2S;
import common.call.s2n.SyncCallS2N;
import common.task.Task;
import common.util.Configuration;

public class TestSyncTask
    extends TestCase
{
    private static ServerConnector NConnector;

    private static ClientConnector SConnector;

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
        SConnector = ClientConnector.getInstance();
        SConnector.addListener(new SCallListener());
        NConnector.addListener(new NCallListener());
    }

    public void testTask()
    {
        File file = new File("b", 1);
        Storage storage = new Storage("localhost");
        Status.getInstance().addStorage(storage);
        Meta.getInstance().addFile("/a/", file);

        List<String> files = new ArrayList<String>();
        files.add("1_0");
        files.add("2_0");
        SyncCallS2N call = new SyncCallS2N(files);

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
            System.out.println("<---: " + call.getType());
            if (Call.Type.SYNC_S2N == call.getType())
            {
                Task task =
                    new SyncTask(1, call, NConnector, Configuration
                        .getInstance().getInteger(Configuration.DUPLICATE_KEY));
                new Thread(task).start();
            }
        }
    }

    private class SCallListener
        implements CallListener
    {

        @Override
        public void handleCall(Call call)
        {
            System.out.println("--->: " + call.getType());
            if (Call.Type.SYNC_N2S == call.getType())
            {
                SyncCallN2S c = (SyncCallN2S) call;
                System.out.println("Sync file list:");
                for (String id : c.getFiles())
                    System.out.println(id);
            }
        }
    }
}

package test.nameserver;

import java.util.concurrent.TimeUnit;

import common.network.ClientConnector;
import common.call.Call;
import common.call.CallListener;
import common.call.all.FinishCall;
import common.call.c2n.AddFileCallC2N;
import common.call.n2c.AddFileCallN2C;
import nameserver.NameServer;
import junit.framework.TestCase;

public class TestNameServer
    extends TestCase
{
    private static ClientConnector CConnector;

    private static CCallListener CListener;

    private static NameServer ns;

    @Override
    protected void setUp()
    {
        ns = new NameServer();
        try
        {
            ns.initilize();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        CConnector = ClientConnector.getInstance();
        CListener = new CCallListener();
        CConnector.addListener(CListener);
    }

    public void testHandleCall()
    {
        Call call = null;

        CListener.setSleepTime(1);
        call = new AddFileCallC2N("/a/", "b");
        CConnector.sendCall(call);

        try
        {
            TimeUnit.SECONDS.sleep(2);
        }
        catch (InterruptedException e1)
        {
            e1.printStackTrace();
        }

        CListener.setSleepTime(10);
        call = new AddFileCallC2N("/", "b");
        CConnector.sendCall(call);

        for (int i = 0; i < 20; i++)
        {
            try
            {
                System.out.println("Sleep " + i + "s.");
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void tearDown()
    {
    }

    private class CCallListener
        implements CallListener
    {
        private long sleepTime = 0;

        public void setSleepTime(long sleepTime)
        {
            this.sleepTime = sleepTime;
        }

        @Override
        public void handleCall(Call call)
        {
            System.out.println("--->: " + call.getType());

            if (Call.Type.ADD_FILE_N2C == call.getType())
            {
                AddFileCallN2C c = (AddFileCallN2C) call;
                System.out.println("task type: " + c.getType());
                System.out.print("location: ");
                for (String l : c.getLocations())
                    System.out.print(l + " ");
                System.out.println();

                try
                {
                    TimeUnit.SECONDS.sleep(sleepTime);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                FinishCall ack = new FinishCall();
                ack.setToTaskId(call.getFromTaskId());
                CConnector.sendCall(ack);
            }
        }
    }
}

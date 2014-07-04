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
        CConnector.addListener(new CCallListener());
    }

    public void testHandleCall()
    {
        Call call = null;

        call = new AddFileCallC2N("/a/", "b");
        CConnector.sendCall(call);
        
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
    
    private class CCallListener
    implements CallListener
{
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

            FinishCall ack = new FinishCall();
            ack.setToTaskId(call.getFromTaskId());
            CConnector.sendCall(ack);
        }
    }
}
}

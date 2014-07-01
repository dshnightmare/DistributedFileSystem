package test.nameserver;


import java.util.concurrent.TimeUnit;

import common.network.ClientConnector;
import common.observe.call.AddFileCallC2N;
import common.observe.call.AddFileCallN2C;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.FinishCall;
import nameserver.NameServer;
import junit.framework.TestCase;

public class TestNameServer
    extends TestCase
{
    private static ClientConnector CConnector;

    @Override
    protected void setUp()
    {
        NameServer.getInstance();
        
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
            System.out.println("task id: " + c.getTaskId());
            System.out.println("task type: " + c.getType());
            System.out.println("initiator: " + c.getInitiator());
            System.out.print("location: ");
            for (String l : c.getLocations())
                System.out.print(l + " ");
            System.out.println();

            FinishCall ack = new FinishCall(call.getTaskId());
            CConnector.sendCall(ack);
        }
    }
}
}

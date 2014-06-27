package test.nameserver;


import common.observe.call.AddFileCallC2N;
import common.observe.call.Call;
import nameserver.NameServer;
import junit.framework.TestCase;

public class TestNameServer
    extends TestCase
{
    private static NameServer ns;

    @Override
    protected void setUp()
    {
        ns = new NameServer();
    }

    public void testHandleCall()
    {
        ns.init();

        Call call = null;

        call = new AddFileCallC2N("/a/", "b");
        ns.handleCall(call);
    }

    @Override
    protected void tearDown()
    {
    }
}

package test;

import nameserver.heartbeat.CardiacArrest;
import nameserver.heartbeat.CardiacArrestListener;
import nameserver.heartbeat.CardiacArrestMonitor;
import nameserver.meta.StorageStatus;
import junit.framework.TestCase;

public class TestCardiacArrestMonitor
    extends TestCase
{
    private static CardiacArrestMonitor monitor;

    private static StorageStatus nodeA;

    private static StorageStatus nodeB;

    private static boolean nodeAAlive = true;

    private static boolean nodeBAlive = true;

    private static long monitorPeriod = 5000; // 5 seconds

    private static long renewPeriod = 3000; // 3 seconds

    @Override
    protected void setUp()
    {
        monitor = new CardiacArrestMonitor(monitorPeriod);
        nodeA = new StorageStatus(1, "localhost");
        nodeB = new StorageStatus(2, "localhost");

        Thread heartbeatReporter = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    nodeA.setAlive(true);
                    try
                    {
                        Thread.sleep(renewPeriod);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        heartbeatReporter.start();

        monitor.setEventListener(new CardiacArrestListener()
        {
            @Override
            public void handle(CardiacArrest event)
            {
                System.out.println("thread " + event.getStorageNode().getId()
                    + " is dead.");
                if (1 == event.getStorageNode().getId())
                    nodeAAlive = false;
                else
                    nodeBAlive = false;
            }
        });
    }

    public void testMonitoring()
    {
        monitor.startMonitoring(nodeA);
        monitor.startMonitoring(nodeB);

        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        assertTrue(nodeAAlive);
        assertFalse(nodeBAlive);
    }

    @Override
    protected void tearDown()
    {
        monitor.stopAllMonitoring();
    }
}

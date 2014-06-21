package test;

import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThread;
import common.thread.TaskThreadMonitor;
import common.thread.TaskThreadPool;
import junit.framework.TestCase;

public class TestTaskMonitor
    extends TestCase
{
    private static TaskThreadMonitor monitor;

    private static TaskThreadPool pool;

    private static long period = 1000;

    private static TaskThread taskA;

    private static TaskThread taskB;

    @Override
    protected void setUp()
    {
        pool = new TaskThreadPool();
        monitor = new TaskThreadMonitor(pool, period);
        monitor.addListener(new TaskEventListener()
        {
            @Override
            public void handle(TaskEvent event)
            {
                System.out.println(event.getType() + ": thread "
                    + event.getTaskThread().getSid());
            }
        });
        
        taskA = new TaskThread(1)
        {
            @Override
            public void run()
            {
                while (true)
                {
                    System.out.println(this.getSid());
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        taskA.renewLease();
        pool.addThread(taskA);
        
        taskB = new TaskThread(2)
        {
            @Override
            public void run()
            {
                while (true)
                {
                    System.out.println(this.getSid());
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        pool.addThread(taskB);
    }

    public void testStartMonitoring()
    {
        monitor.startMonitoring();

        try
        {
            Thread.sleep(15000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        monitor.stopMonitoring();
    }

    @Override
    protected void tearDown()
    {

    }
}

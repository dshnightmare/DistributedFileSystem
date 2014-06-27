package test;

import common.observe.call.Call;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskLease;
import common.thread.TaskThread;
import common.thread.TaskThreadMonitor;
import junit.framework.TestCase;

public class TestTaskMonitor
    extends TestCase
{
    private static TaskThreadMonitor monitor;

    private static long period = 1000;

    private static TaskThread taskA;

    private static TaskThread taskB;

    @Override
    protected void setUp()
    {
        monitor = new TaskThreadMonitor(period);
        monitor.addListener(new TaskEventListener()
        {
            @Override
            public void handle(TaskEvent event)
            {
                System.out.println(event.getType() + ": thread "
                    + event.getTaskThread().getTaskId());
            }
        });
        
        
    }

    public void testStartMonitoring()
    {
        monitor.startMonitoring();

        taskA = new TaskThread(1)
        {
            @Override
            public void run()
            {
                while (true)
                {
                    System.out.println(this.getTaskId());
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

            @Override
            public void release()
            {
            }

            @Override
            public void handleCall(Call call)
            {
                // TODO Auto-generated method stub
                
            }
        };
        taskA.setLease(new TaskLease(3000));
        taskA.renewLease();
        monitor.addThread(taskA);
        
        taskB = new TaskThread(2)
        {
            @Override
            public void run()
            {
                while (true)
                {
                    System.out.println(this.getTaskId());
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

            @Override
            public void release()
            {
            }

            @Override
            public void handleCall(Call call)
            {
                // TODO Auto-generated method stub
                
            }
        };
        taskB.setLease(new TaskLease(7000));
        monitor.addThread(taskB);
        
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

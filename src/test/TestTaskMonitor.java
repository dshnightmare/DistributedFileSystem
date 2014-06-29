package test;

import java.util.concurrent.TimeUnit;

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

    private static TaskThread taskA;

    private static TaskThread taskB;

    @Override
    protected void setUp()
    {
        monitor = TaskThreadMonitor.getInstance();
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
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    if (!isLeaseValid())
                        break;
                }
            }

            @Override
            public void release()
            {
            }

            @Override
            public void handleCall(Call call)
            {
            }
        };
        taskA.setLease(new TaskLease(3000));
        taskA.renewLease();
        monitor.addThread(taskA);
        new Thread(taskA).start();
        
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
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    if (!isLeaseValid())
                        break;
                }
            }

            @Override
            public void release()
            {
            }

            @Override
            public void handleCall(Call call)
            {
            }
        };
        taskB.setLease(new TaskLease(7000));
        monitor.addThread(taskB);
        new Thread(taskB).start();
        
        try
        {
            TimeUnit.SECONDS.sleep(15);
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
        monitor.stopMonitoring();
    }
}

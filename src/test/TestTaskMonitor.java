package test;

import java.util.concurrent.TimeUnit;

import common.call.Call;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.TaskLease;
import common.task.Task;
import common.task.TaskMonitor;
import junit.framework.TestCase;

public class TestTaskMonitor
    extends TestCase
{
    private static TaskMonitor monitor;

    private static Task taskA;

    private static Task taskB;

    @Override
    protected void setUp()
    {
        monitor = TaskMonitor.getInstance();
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
        taskA = new Task(1)
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
        
        taskB = new Task(2)
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

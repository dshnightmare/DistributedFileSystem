package test.nameserver;

import nameserver.heartbeat.CardiacArrestMonitor;
import nameserver.meta.StorageStatusList;
import nameserver.task.TaskRegistration;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThread;
import junit.framework.TestCase;

public class TestTaskRegistration
    extends TestCase
{
    @Override
    protected void setUp()
    {
    }

    public void testTask()
    {
        StorageStatusList storages = new StorageStatusList();
        assertEquals(0, storages.getSize());
        
        CardiacArrestMonitor monitor = new CardiacArrestMonitor(5000);
        TaskThread task =
            new TaskRegistration(1, "localhost", storages, monitor);
        task.addListener(new TaskEventListener()
        {
            @Override
            public void handle(TaskEvent event)
            {
                System.out.println(event.getType());
            }
        });
        new Thread(task).start();
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        assertEquals(1, storages.getSize());
    }

    @Override
    protected void tearDown()
    {
    }
}

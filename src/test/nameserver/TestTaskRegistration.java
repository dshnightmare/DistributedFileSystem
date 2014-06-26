package test.nameserver;

import nameserver.meta.Status;
import nameserver.task.RegisterStorageTask;
import common.network.ServerConnector;
import common.observe.call.RegistrationCallS2N;
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
        Status status = new Status();

        assertNull(status.getStorage("localhost"));

        ServerConnector connector = new ServerConnector();
        connector.start();

        RegistrationCallS2N call = new RegistrationCallS2N("localhost");
        TaskThread task = new RegisterStorageTask(1, call, status, connector);
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
        assertNotNull(status.getStorage("localhost"));
    }

    @Override
    protected void tearDown()
    {
    }
}

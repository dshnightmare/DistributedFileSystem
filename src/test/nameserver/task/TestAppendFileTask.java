package test.nameserver.task;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import nameserver.meta.Status;
import nameserver.meta.Storage;
import nameserver.task.AppendFileTask;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.observe.call.AppendFileCallC2N;
import common.observe.call.AppendFileCallN2C;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.FinishCall;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.thread.TaskThread;

public class TestAppendFileTask
    extends TestCase
{
    private static ServerConnector NConnector;

    private static ClientConnector CConnector;

    @Override
    protected void setUp()
    {
        NConnector = ServerConnector.getInstance();
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
        NConnector.addListener(new NCallListener());
    }

    public void testTask()
    {
        Directory dir = new Directory("/a/");
        File file = new File("b", 1);
        Storage storage = new Storage(1, "localhost");

        file.addLocation(storage);
        storage.addFile(file);
        dir.addFile(file);
        Meta.getInstance().addDirectory(dir);
        Status.getInstance().addStorage(storage);

        dir = Meta.getInstance().getDirectory("/a/");
        assertNotNull(dir);

        AppendFileCallC2N call = new AppendFileCallC2N("/a/", "b");
        CConnector.sendCall(call);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        dir = Meta.getInstance().getDirectory("/a/");
        assertNotNull(dir);
    }

    @Override
    protected void tearDown()
    {
    }

    private class NCallListener
        implements CallListener
    {
        @Override
        public void handleCall(Call call)
        {
            System.out.println("<---: " + call.getType());
            if (Call.Type.APPEND_FILE_C2N == call.getType())
            {
                TaskThread task = new AppendFileTask(1, call, NConnector);
                task.addListener(new TaskListener());
                new Thread(task).start();
            }
        }
    }

    private class CCallListener
        implements CallListener
    {

        @Override
        public void handleCall(Call call)
        {
            System.out.println("--->: " + call.getType());
            if (Call.Type.APPEND_FILE_N2C == call.getType())
            {
                AppendFileCallN2C c = (AppendFileCallN2C) call;
                System.out.println("task id: " + c.getTaskId());
                System.out.println("call type: " + c.getType());
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

    private class TaskListener
        implements TaskEventListener
    {

        @Override
        public void handle(TaskEvent event)
        {
            System.out.println("Task " + event.getTaskThread().getTaskId()
                + " " + event.getType());
        }

    }
}

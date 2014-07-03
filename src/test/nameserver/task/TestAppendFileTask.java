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
import common.call.AppendFileCallC2N;
import common.call.AppendFileCallN2C;
import common.call.Call;
import common.call.CallListener;
import common.call.FinishCall;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.Task;

public class TestAppendFileTask
    extends TestCase
{
    private static Task task;
    
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
                task = new AppendFileTask(1, call, NConnector);
                task.addListener(new TaskListener());
                new Thread(task).start();
            }
            else if (Call.Type.FINISH == call.getType())
            {
                task.handleCall(call);
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
                System.out.println("call type: " + c.getType());
                System.out.print("location: ");
                for (String l : c.getLocations())
                    System.out.print(l + " ");
                System.out.println();

                FinishCall ack = new FinishCall();
                ack.setToTaskId(1);
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

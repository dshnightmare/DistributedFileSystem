package test.nameserver.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import nameserver.status.Status;
import nameserver.status.Storage;
import nameserver.task.HeartbeatTask;
import common.network.ClientConnector;
import common.network.ServerConnector;
import common.call.Call;
import common.call.CallListener;
import common.call.n2s.MigrateFileCallN2S;
import common.call.s2n.HeartbeatCallS2N;
import common.call.s2n.RegistrationCallS2N;
import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.task.Task;

public class TestHeartbeatTask
    extends TestCase
{
    private static ServerConnector NConnector;

    private static ClientConnector SConnector;

    private static Task task;

    private static long taskId = 1;

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
        SConnector = ClientConnector.getInstance();
        NConnector.addListener(new NCallListener());
        SConnector.addListener(new SCallListener());
    }

    public void testTask()
    {
        Status status = Status.getInstance();

        RegistrationCallS2N rcall = new RegistrationCallS2N("localhost");
        SConnector.sendCall(rcall);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        Call hcall = new HeartbeatCallS2N(new HashMap<String, List<String>>());
        hcall.setToTaskId(taskId);
        SConnector.sendCall(hcall);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // We have only one storage.
        Storage storage = status.getStorages().get(0);
        long timestamp1 = storage.getHearbeatTime();
        System.out.println("Timestamp1: " + timestamp1);

        SConnector.sendCall(hcall);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // We have only one storage.
        storage = status.getStorages().get(0);
        long timestamp2 = storage.getHearbeatTime();
        System.out.println("Timestamp2: " + timestamp2);
        assertTrue(timestamp2 > timestamp1);
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
            if (Call.Type.REGISTRATION_S2N == call.getType())
            {
                task = new HeartbeatTask(taskId, call, NConnector, 2000);
                task.addListener(new TaskListener());
                new Thread(task).start();
            }
            else if (Call.Type.HEARTBEAT_S2N == call.getType())
            {
                task.handleCall(call);
            }
        }
    }

    private class SCallListener
        implements CallListener
    {
        @Override
        public void handleCall(Call call)
        {
            System.out.println("--->: " + call.getType());
            if (Call.Type.FINISH == call.getType())
            {
                System.out.println("Get finish call.");
            }
            else if (Call.Type.MIGRATE_FILE_N2S == call.getType())
            {
                MigrateFileCallN2S c = (MigrateFileCallN2S) call;
                for (Entry<String, List<String>> s : c.getFiles().entrySet())
                {
                    System.out.println("Get from storage: " + s.getKey());
                    for (String id : s.getValue())
                        System.out.println("\t" + id);
                }
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

package nameserver.task;

import java.util.List;
import java.util.Random;

import nameserver.meta.DirectoryTree;
import nameserver.meta.FileNode;
import nameserver.meta.Node;
import nameserver.meta.StorageStatus;
import nameserver.meta.StorageStatusList;
import common.observe.call.AddFileCallN2C;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.call.FinishCall;
import common.thread.TaskThread;

public class TaskAdd
    extends TaskThread
    implements CallListener
{
    private int duplicate = 1;

    private String path;

    private DirectoryTree directory;

    private boolean isRecursive;

    private Object syncRoot = new Object();

    private StorageStatusList storages;

    public TaskAdd(long sid, String path, DirectoryTree directory,
        StorageStatusList storages, boolean isRecursive, int duplicate)
    {
        super(sid);
        this.path = path;
        this.directory = directory;
        this.storages = storages;
        this.isRecursive = isRecursive;
        this.duplicate = duplicate;
    }

    @Override
    public void run()
    {
        String parentPath = DirectoryTree.getPrefixPath(path);
        String fileName = DirectoryTree.getSuffixPath(path);
        Node parentNode = null;
        try
        {
            parentNode = directory.getNode(parentPath);
        }
        catch (Exception e)
        {
            try
            {
                parentNode = directory.createPath(parentPath, isRecursive);
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
                // TODO: Send back error call to client.
            }
        }

        // There could be phantom effact, so we should add synchronize here.
        Random rand = new Random(System.currentTimeMillis());
        Node node = new FileNode(fileName, rand.nextLong());

        List<StorageStatus> targets = storages.allocateStorages(duplicate);
        Call back = new AddFileCallN2C(targets);
        back.setTaskId(getTaskId());
        // TODO: Send to client.

        try
        {
            synchronized (syncRoot)
            {
                syncRoot.wait();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        parentNode.addChild(node);

        back = new FinishCall(getTaskId());
        // Send to client.

        setFinish();
    }

    @Override
    public void release()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void handleCall(Call call)
    {
        if (call.getTaskId() != getTaskId())
            return;

        if (call.getType() == Call.Type.HEARTBEAT_S2N)
        {
            renewLease();
        }

        if (call.getType() == Call.Type.FINISH)
        {
            synchronized (syncRoot)
            {
                syncRoot.notify();
            }
            return;
        }
    }
}

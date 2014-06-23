package nameserver.task;

import nameserver.meta.DirectoryTree;
import common.observe.call.AddFileCallC2N;
import common.observe.call.Call;
import common.observe.call.MoveFileCallC2N;
import common.observe.call.RemoveFileCallC2N;
import common.thread.TaskThread;

public class TaskFactory
{
    private DirectoryTree directory;

    public TaskFactory(DirectoryTree directory)
    {
        this.directory = directory;
    }

    public TaskThread createThread(Call call)
    {
        TaskThread thread = null;
        switch (call.getType())
        {
        case ADD_FILE_C2N:
            thread = createTaskAdd(call);
            break;
        case MOVE_FILE_C2N:
            thread = createTaskMove(call);
            break;
        case REGISTRATION_S2N:
            thread = createTaskRegistration(call);
            break;
        case REMOVE_FILE_C2N:
            thread = createTaskRemove(call);
            break;
        case SYNC_S2N:
            thread = createTaskSync(call);
            break;
        default:
            break;
        }

        return thread;
    }

    private TaskAdd createTaskAdd(Call call)
    {
        AddFileCallC2N ac = (AddFileCallC2N) call;
        return new TaskAdd(ac.getTaskId(), ac.getFilePath());
    }

    private TaskMove createTaskMove(Call call)
    {
        MoveFileCallC2N mc = (MoveFileCallC2N) call;
        return new TaskMove(call.getTaskId(), mc.getOldPath(), mc.getNewPath(),
            directory);
    }

    private TaskRegistration createTaskRegistration(Call call)
    {
        return new TaskRegistration(call.getTaskId());
    }

    private TaskRemove createTaskRemove(Call call)
    {
        RemoveFileCallC2N rc = (RemoveFileCallC2N) call;
        return new TaskRemove(call.getTaskId(), rc.getPath(), directory);
    }

    private TaskSync createTaskSync(Call call)
    {
        return new TaskSync(call.getTaskId());
    }
}

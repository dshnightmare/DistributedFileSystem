package nameserver.task;

import nameserver.heartbeat.CardiacArrestMonitor;
import nameserver.meta.DirectoryTree;
import nameserver.meta.StorageStatusList;
import common.observe.call.AddFileCallC2N;
import common.observe.call.Call;
import common.observe.call.MoveFileCallC2N;
import common.observe.call.RegistrationCallS2N;
import common.observe.call.RemoveFileCallC2N;
import common.thread.TaskThread;
import common.util.Configuration;
import common.util.Constant;

public class TaskFactory
{
    private final DirectoryTree directory;

    private final StorageStatusList activeStorages;

    private final CardiacArrestMonitor cardiacArrestMonitor;

    private final Configuration conf;

    private final int duplicate;

    public TaskFactory(DirectoryTree directory,
        StorageStatusList activeStorages,
        CardiacArrestMonitor cardiacArrestMonitor)
    {
        this.directory = directory;
        this.activeStorages = activeStorages;
        this.cardiacArrestMonitor = cardiacArrestMonitor;
        this.conf = Configuration.getInstance();
        duplicate = conf.getInteger(Constant.DUPLICATE_KEY);
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
        return new TaskAdd(ac.getTaskId(), ac.getFilePath(), directory,
            ac.isRecursive(), duplicate);
    }

    private TaskMove createTaskMove(Call call)
    {
        MoveFileCallC2N mc = (MoveFileCallC2N) call;
        return new TaskMove(call.getTaskId(), mc.getOldPath(), mc.getNewPath(),
            directory);
    }

    private TaskRegistration createTaskRegistration(Call call)
    {
        RegistrationCallS2N rc = (RegistrationCallS2N) call;
        return new TaskRegistration(rc.getTaskId(), rc.getAddress(),
            activeStorages, cardiacArrestMonitor);
    }

    private TaskRemove createTaskRemove(Call call)
    {
        RemoveFileCallC2N rc = (RemoveFileCallC2N) call;
        return new TaskRemove(rc.getTaskId(), rc.getPath(), directory);
    }

    private TaskSync createTaskSync(Call call)
    {
        return new TaskSync(call.getTaskId());
    }
}

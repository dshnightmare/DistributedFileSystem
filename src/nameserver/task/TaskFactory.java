package nameserver.task;

import common.call.Call;
import common.network.Connector;
import common.network.ServerConnector;
import common.task.TaskLease;
import common.util.Configuration;
import common.util.IdGenerator;

/**
 * A task factory.
 * 
 * @author lishunyang
 * @see NameServerTask
 */
public class TaskFactory
{
    /**
     * Create <tt>NameServerTask</tt>.
     * <p>
     * As for name server, we only care about some call, such as *_C2N and
     * *_S2N.
     * 
     * @param call
     * @return
     */
    public static NameServerTask createTask(Call call)
    {
        final Connector connector = ServerConnector.getInstance();
        final long taskId = IdGenerator.getInstance().getLongId();
        final Configuration conf = Configuration.getInstance();
        NameServerTask task = null;

        switch (call.getType())
        {
        case ADD_DIRECTORY_C2N:
            task = new AddDirectoryTask(taskId, call, connector);
            break;
        case ADD_FILE_C2N:
            task =
                new AddFileTask(taskId, call, connector,
                    conf.getInteger(Configuration.DUPLICATE_KEY));
            break;
        case APPEND_FILE_C2N:
            task = new AppendFileTask(taskId, call, connector);
            break;
        case GET_DIRECTORY_C2N:
            task = new GetDirectoryTask(taskId, call, connector);
            break;
        case GET_FILE_C2N:
            task = new GetFileTask(taskId, call, connector);
            break;
        case MOVE_FILE_C2N:
            task = new GetFileTask(taskId, call, connector);
            break;
        case REMOVE_FILE_C2N:
            task = new RemoveFileTask(taskId, call, connector);
            break;
        default:
            break;
        }

        task.setLease(new TaskLease(conf
            .getLong(Configuration.LEASE_PERIOD_KEY)));

        return task;
    }
}

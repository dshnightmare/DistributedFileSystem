package nameserver.task;

import nameserver.heartbeat.CardiacArrestMonitor;
import nameserver.meta.StorageStatus;
import nameserver.meta.StorageStatusList;
import common.thread.TaskThread;
import common.util.IdGenerator;

public class TaskRegistration
    extends TaskThread
{
    private String address;

    private StorageStatusList activeStorages;

    private CardiacArrestMonitor cardiacArrestMonitor;

    public TaskRegistration(long sid, String address,
        StorageStatusList activeStorages,
        CardiacArrestMonitor cardiacArrestMonitor)
    {
        super(sid);
        this.address = address;
        this.activeStorages = activeStorages;
        this.cardiacArrestMonitor = cardiacArrestMonitor;
    }

    @Override
    public void run()
    {
        long id = IdGenerator.getInstance().getLongId();
        StorageStatus node = new StorageStatus(id, address);
        activeStorages.addNode(node);
        cardiacArrestMonitor.startMonitoring(node);
        setFinish();
    }

    @Override
    public void release()
    {
        // TODO Auto-generated method stub

    }

}

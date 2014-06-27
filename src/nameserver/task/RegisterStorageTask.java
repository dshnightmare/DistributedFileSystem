package nameserver.task;

import java.nio.channels.SocketChannel;

import nameserver.meta.Status;
import nameserver.meta.Storage;
import common.network.Connector;
import common.observe.call.AbortCall;
import common.observe.call.Call;
import common.observe.call.FinishCall;
import common.observe.call.RegistrationCallS2N;
import common.thread.TaskThread;
import common.util.IdGenerator;

public class RegisterStorageTask
    extends TaskThread
{
    private String address;

    private Status status;

    private String initiator;

    private Connector connector;

    public RegisterStorageTask(long sid, Call call, Status status,
        Connector connector)
    {
        super(sid);
        RegistrationCallS2N c = (RegistrationCallS2N) call;
        this.address = c.getAddress();
        this.initiator = c.getInitiator();
        this.status = status;
        this.connector = connector;
    }

    @Override
    public void run()
    {
        Call back = null;

        if (status.contains(address))
        {
            back =
                new AbortCall(getTaskId(),
                    "There has been a storage server using the same address.");
            back.setInitiator(initiator);;
            connector.sendCall(back);
            setFinish();
            return;
        }

        Storage storage =
            new Storage(IdGenerator.getInstance().getLongId(), address);
        status.addStorage(storage);

        // TODO: Add heartbeat monitor.

        back = new FinishCall(getTaskId());
        back.setInitiator(initiator);;
        connector.sendCall(back);
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
    }
}

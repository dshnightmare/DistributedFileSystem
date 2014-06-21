package nameserver;

import java.io.IOException;

import nameserver.heartbeat.CardiacArrest;
import nameserver.heartbeat.CardiacArrestListener;
import nameserver.heartbeat.CardiacArrestMonitor;
import common.observe.call.Call;
import common.observe.call.CallListener;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.util.Configuration;
import common.util.Constant;
import common.util.Logger;

public class NameServer implements TaskEventListener, CardiacArrestListener, CallListener
{

    private static final Logger logger = Logger.getLogger(NameServer.class);
    private CardiacArrestMonitor heartbeatMonitor;
    
    public void init()
    {
        try
        {
            Configuration conf = Configuration.getInstance();
            heartbeatMonitor = new CardiacArrestMonitor(conf.getLong(Constant.HEARTBEAT_INTERVAL_KEY));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void handle(TaskEvent event)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handleCall(Call call)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handle(CardiacArrest OMG)
    {
        logger.info("StorageNode " + OMG.getStorageNode() + " is dead, hurry up");
    }

}

package nameserver;

import nameserver.heartbeat.HeartbeatEvent;
import nameserver.heartbeat.HeartbeatEventListener;
import nameserver.heartbeat.HeartbeatMonitor;
import common.observe.event.TaskEvent;
import common.observe.event.TaskEventListener;
import common.util.Logger;

public class NameServer implements TaskEventListener, HeartbeatEventListener
{

    private static final Logger logger = Logger.getLogger(NameServer.class);
    private HeartbeatMonitor heartbeatMonitor;
    
    public void init()
    {
        heartbeatMonitor = new HeartbeatMonitor(60);
    }
    
    @Override
    public void handle(HeartbeatEvent event)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handle(TaskEvent event)
    {
        // TODO Auto-generated method stub
        
    }

}

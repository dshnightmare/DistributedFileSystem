package nameserver.heartbeat;

public interface HeartbeatListener
{
    public void handleHeatbeatEvent(HeartbeatEvent event);
}

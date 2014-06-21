package nameserver.heartbeat;

public interface HeartbeatEventListener
{
    public void handle(HeartbeatEvent event);
}

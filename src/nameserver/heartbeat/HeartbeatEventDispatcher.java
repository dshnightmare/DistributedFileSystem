package nameserver.heartbeat;

public interface HeartbeatEventDispatcher
{
    public void setEventListener(HeartbeatEventListener listener);

    public void removeEventListener(HeartbeatEventListener listener);

    public void fireEvent(HeartbeatEvent event);
}

package nameserver.heartbeat;

public interface HeartbeatEventDispatcher
{
    public void setEventListener(HeartbeatEventListener listener);

    public void removeEventListener();

    public void fireEvent(HeartbeatEvent event);
}

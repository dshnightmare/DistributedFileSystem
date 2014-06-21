package nameserver.heartbeat;

public interface CardiacArrestDispatcher
{
    public void setEventListener(CardiacArrestListener listener);

    public void removeEventListener();

    public void fireEvent(CardiacArrest event);
}

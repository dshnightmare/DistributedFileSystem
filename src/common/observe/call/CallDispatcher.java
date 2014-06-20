package common.observe.call;

public interface CallDispatcher
{
    public void addListener(CallListener listener);
    public void removeListener(CallListener listener);
}

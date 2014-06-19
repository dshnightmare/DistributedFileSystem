package common.observe.call;

public interface RequestDispatcher
{
    public void addListener(RequestListener listener);
    public void removeListener(RequestListener listener);
}

package common.observe.request;

public interface RequestDispatcher
{
    public void addListener(RequestListener listener);
    public void removeListener(RequestListener listener);
}

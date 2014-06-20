package common.observe.event;

public interface TaskEventDispatcher
{
    public void addListener(TaskEventListener listener);

    public void removeListener(TaskEventListener listener);

    public void fireEvent(TaskEvent event);
}

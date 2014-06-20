package common.observe.event;

import common.thread.TaskThread;

public class TaskEvent
{
    private EventType type;

    private TaskThread thread;

    public TaskEvent(EventType type, TaskThread thread)
    {
        this.type = type;
        this.thread = thread;
    }

    public EventType getType()
    {
        return type;
    }

    public TaskThread getTaskThread()
    {
        return thread;
    }

    public static enum EventType
    {
        TASK_FINISHED, TASK_ABORTED, INVALID,
    }
}

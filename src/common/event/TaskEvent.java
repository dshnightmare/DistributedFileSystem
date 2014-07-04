package common.event;

import common.task.Task;

public class TaskEvent
{
    private Type type;

    private Task thread;

    public TaskEvent(Type type, Task thread)
    {
        this.type = type;
        this.thread = thread;
    }

    public Type getType()
    {
        return type;
    }

    public Task getTaskThread()
    {
        return thread;
    }

    public static enum Type
    {
        /**
         * 
         */
        TASK_FINISHED("TASK_FINISHED"),

        /**
         * 
         */
        TASK_ABORTED("TASK_ABORTED"),

        /**
         * 
         */
        HEARTBEAT_FATAL("HEARTBEAT_FATAL"),

        /**
         * 
         */
        INVALID("INVALID");

        private String name;

        private Type(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }
}
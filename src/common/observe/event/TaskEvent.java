package common.observe.event;

import common.thread.TaskThread;

public class TaskEvent
{
    private Type type;

    private TaskThread thread;

    public TaskEvent(Type type, TaskThread thread)
    {
        this.type = type;
        this.thread = thread;
    }

    public Type getType()
    {
        return type;
    }

    public TaskThread getTaskThread()
    {
        return thread;
    }

//    public static enum Type
//    {
//        TASK_FINISHED("TASK_FINISHED"), TASK_ABORTED("TASK_ABORTED"), INVALID(
//            "INVALID");
//
//        private String name;
//
//        private Type(String name)
//        {
//            this.name = name;
//        }
//
//        @Override
//        public String toString()
//        {
//            return name;
//        }
//    }
    
    public static enum Type
    {
        TASK_FINISHED, TASK_ABORTED, INVALID
    }
}

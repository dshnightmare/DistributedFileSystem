package nameserver.status;

public class StatusEvent
{
    private Type type;

    private Storage storage;

    public StatusEvent(Type type, Storage storage)
    {
        this.type = type;
        this.storage = storage;
    }

    public Type getType()
    {
        return type;
    }

    public Storage getStorage()
    {
        return storage;
    }

    public static enum Type
    {
        STORAGE_REGISTERED("STORAGE_REGISTERED"),
        STORAGE_DEAD("STORAGE_DEAD"),
        LOAD_CHANGED("LOAD_CHANGED"),
        TASK_SUM_CHANGED("TASK_SUM_CHANGED"),
        HEARTBEAT("HEARTBEAT");

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

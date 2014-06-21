package common.protocol;

public abstract class Command
{
    private final Type type;

    public Command(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    public static enum Type
    {
        HEARTBEAT("HEARTBEAT"), REGISTRATION("REGISTRATION");

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

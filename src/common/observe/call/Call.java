package common.observe.call;

import java.io.Serializable;

public abstract class Call implements Serializable {

    private static final long serialVersionUID = -1157466700148810064L;
    private final Type type;

    public Call(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    public static enum Type
    {
        INVALID("INVALID"),
        HEARTBEAT("HEARTBEAT"), 
        REGISTRATION("REGISTRATION"), 
        ADD_FILE("ADD_FILE"), 
        ADD_FILE_SS("ADD_FILE_SS"),
        GET_FILE_SS("GET_FILE_SS"),
        REMOVE_FILE("REMOVE_FILE"), 
        MOVE_FILE("MOVE_FILE"),
        SYNC("SYNC");

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

package common.observe.call;

public class CallFactory
{
    private CallFactory()
    {
    }
    
    public static Call createCall(Call.Type type)
    {
        switch (type)
        {
        case ADD_FILE:
            return createAddFileCall();
        case HEARTBEAT:
            return createHeartbeatCall();
        case MOVE_FILE:
            return createMoveFileCall();
        case REGISTRATION:
            return createRegistrationCall();
        case REMOVE_FILE:
            return createRemoveFileCall();
        case SYNC:
            return createSyncCall();
        default:
            return null;
        }
    }
    
    public static Call createAddFileCall()
    {
        return new AddFileCall();
    }
    
    public static Call createMoveFileCall()
    {
        return new MoveFileCall();
    }
    
    public static Call createRemoveFileCall()
    {
        return new RemoveFileCall();
    }
    
    public static Call createHeartbeatCall()
    {
        return new HeartbeatCall();
    }
    
    public static Call createSyncCall()
    {
        return new SyncCall();
    }
    
    public static Call createRegistrationCall()
    {
        return new RegistrationCall();
    }
}

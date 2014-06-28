package common.observe.call;

public class LeaseCall extends Call
{
    private static final long serialVersionUID = -2940145842011756383L;

    public LeaseCall(long taskId)
    {
        super(Call.Type.LEASE);
        setTaskId(taskId);
    }
}

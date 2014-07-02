package common.call;

public class AbortCall
    extends Call
{
    private static final long serialVersionUID = -4194888093602447089L;

    private String reason;

    public AbortCall(long taskId, String reason)
    {
        super(Call.Type.ABORT);
        super.setTaskId(taskId);
        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }
}

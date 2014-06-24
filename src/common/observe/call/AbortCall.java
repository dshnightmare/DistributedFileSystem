package common.observe.call;

public class AbortCall
    extends Call
{
    private static final long serialVersionUID = -4194888093602447089L;

    public AbortCall(long taskId)
    {
        super(Call.Type.ABORT);
        super.setTaskId(taskId);
    }
}

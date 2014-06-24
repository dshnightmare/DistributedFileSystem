package common.observe.call;

public class FinishCall
    extends Call
{
    public FinishCall(long taskId)
    {
        super(Call.Type.FINISH);
        super.setTaskId(taskId);
    }

}

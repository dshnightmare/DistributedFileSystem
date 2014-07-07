package common.call.all;

import common.call.Call;
/**
 * when a task finished successfully, a finish call will be sent to the remote entity.
 * @author gengyufeng
 *
 */
public class FinishCall
    extends Call
{
    private static final long serialVersionUID = 8622180358347032874L;

    public FinishCall()
    {
        super(Call.Type.FINISH);
    }

}

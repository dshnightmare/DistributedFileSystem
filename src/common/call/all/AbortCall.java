package common.call.all;

import common.call.Call;

public class AbortCall
    extends Call
{
    private static final long serialVersionUID = -4194888093602447089L;

    private String reason;

    public AbortCall(String reason)
    {
        super(Call.Type.ABORT);
        this.reason = reason;
    }
    
    public String getReason()
    {
        return reason;
    }
}

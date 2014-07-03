package common.call.n2c;

import common.call.Call;

public class AbortCallN2C
    extends Call
{
    private static final long serialVersionUID = -4194888093602447089L;

    private String reason;

    public AbortCallN2C(String reason)
    {
        super(Call.Type.ABORT_N2C);
        this.reason = reason;
    }
    
    public String getReason()
    {
        return reason;
    }
}

package common.call.c2n;

import common.call.Call;

public class LeaseCallC2N
    extends Call
{
    private static final long serialVersionUID = -2940145842011756383L;

    public LeaseCallC2N()
    {
        super(Call.Type.LEASE_C2N);
    }
}
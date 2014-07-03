package common.call.c2n;

import common.call.Call;
import common.call.Call.Type;

public class FinishCallC2N
    extends Call
{
    private static final long serialVersionUID = 8622180358347032874L;

    public FinishCallC2N()
    {
        super(Call.Type.FINISH_C2N);
    }

}

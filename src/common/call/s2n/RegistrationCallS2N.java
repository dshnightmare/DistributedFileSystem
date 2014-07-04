package common.call.s2n;

import common.call.Call;

public class RegistrationCallS2N
    extends Call
{
    private static final long serialVersionUID = 1475266407427118687L;

    public RegistrationCallS2N()
    {
        super(Call.Type.REGISTRATION_S2N);
    }
}

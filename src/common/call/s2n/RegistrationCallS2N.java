package common.call.s2n;

import common.call.Call;
import common.call.Call.Type;

public class RegistrationCallS2N
    extends Call
{
    private static final long serialVersionUID = 1475266407427118687L;

    private String address;

    public RegistrationCallS2N(String address)
    {
        super(Call.Type.REGISTRATION_S2N);
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }
}

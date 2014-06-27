package common.observe.call;

public class HeartbeatCallS2N
    extends Call
{
    private static final long serialVersionUID = 5334179702773690697L;

    private String address;

    public HeartbeatCallS2N(String address)
    {
        super(Call.Type.HEARTBEAT_S2N);
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }
}

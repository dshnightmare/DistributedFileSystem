package common.call;

import java.util.List;
import java.util.Map;

public class HeartbeatCallS2N
    extends Call
{
    private static final long serialVersionUID = 5334179702773690697L;

    private final String address;

    private final Map<String, List<Long>> migratedFiles;

    public HeartbeatCallS2N(String address, Map<String, List<Long>> migratedFiles)
    {
        super(Call.Type.HEARTBEAT_S2N);
        this.address = address;
        this.migratedFiles = migratedFiles;
    }

    public String getAddress()
    {
        return address;
    }

    public Map<String, List<Long>> getMigratedFiles()
    {
        return migratedFiles;
    }
}

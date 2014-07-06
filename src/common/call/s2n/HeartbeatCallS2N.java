package common.call.s2n;

import java.util.List;
import java.util.Map;

import common.call.Call;

public class HeartbeatCallS2N
    extends Call
{
    private static final long serialVersionUID = 5334179702773690697L;

    /**
     * Files that have been migrated.
     * <p>
     * {storage id, list of file id}
     */
    private final int load;
    public int getLoad() {
		return load;
	}

	private final Map<String, List<String>> migratedFiles;

    public HeartbeatCallS2N(Map<String, List<String>> migratedFiles, int load)
    {
        super(Call.Type.HEARTBEAT_S2N);
        this.migratedFiles = migratedFiles;
        this.load = load;
    }

    public Map<String, List<String>> getMigratedFiles()
    {
        return migratedFiles;
    }
}

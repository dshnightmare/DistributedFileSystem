package common.call.s2n;

import java.util.List;

import common.call.Call;

public class SyncCallS2N
    extends Call
{

    private static final long serialVersionUID = 5000625749190241770L;

    private final String address;

    private final List<String> files;

    public SyncCallS2N(String address, List<String> files)
    {
        super(Call.Type.SYNC_S2N);
        this.address = address;
        this.files = files;
    }

    public String getAddress()
    {
        return address;
    }

    public List<String> getFiles()
    {
        return files;
    }
}

package common.call.s2n;

import java.util.List;

import common.call.Call;

public class SyncCallS2N
    extends Call
{

    private static final long serialVersionUID = 5000625749190241770L;

    private final List<String> files;

    public SyncCallS2N(List<String> files)
    {
        super(Call.Type.SYNC_S2N);
        this.files = files;
    }

    public List<String> getFiles()
    {
        return files;
    }
}

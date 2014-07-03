package common.call.n2s;

import java.util.List;

import common.call.Call;
import common.call.Call.Type;

public class SyncCallN2S
    extends Call
{
    private List<Long> files;

    public SyncCallN2S(List<Long> files)
    {
        super(Call.Type.SYNC_N2S);
        this.files = files;
    }

    public List<Long> getFiles()
    {
        return files;
    }
}

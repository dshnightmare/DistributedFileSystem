package common.observe.call;

import java.util.List;

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

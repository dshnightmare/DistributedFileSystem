package common.call;

import java.util.List;
import java.util.Map;

public class MigrateFileCallN2S
    extends Call
{
    private static final long serialVersionUID = -5525795765372507337L;

    private Map<String, List<Long>> files;

    public MigrateFileCallN2S(long taskId, Map<String, List<Long>> files)
    {
        super(Call.Type.MIGRATE_FILE_N2S);
        super.setTaskId(taskId);
        this.files = files;
    }

    public Map<String, List<Long>> getFiles()
    {
        return files;
    }
}

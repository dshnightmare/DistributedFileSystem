package common.observe.call;

public class AddFileCallC2N
    extends Call
{
    private static final long serialVersionUID = -805598603013291328L;

    private String filePath;

    private boolean recursive;

    public AddFileCallC2N(String filePath, boolean recursive)
    {
        super(Call.Type.ADD_FILE_C2N);
        this.filePath = filePath;
        this.recursive = recursive;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public boolean isRecursive()
    {
        return recursive;
    }
}

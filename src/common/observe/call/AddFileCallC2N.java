package common.observe.call;

public class AddFileCallC2N extends Call
{
    private static final long serialVersionUID = -805598603013291328L;
    private String filePath;

    public AddFileCallC2N(String filePath)
    {
        super(Call.Type.ADD_FILE_C2N);
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return filePath;
    }
}

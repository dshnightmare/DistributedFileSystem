package common.observe.call;

public class RemoveFileCallC2N extends Call
{

    private static final long serialVersionUID = 7834245963090160026L;
    private String path;

    public RemoveFileCallC2N(String path)
    {
        super(Call.Type.REMOVE_FILE_C2N);
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }
}

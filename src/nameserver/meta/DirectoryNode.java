package nameserver.meta;

public class DirectoryNode extends Node
{

    public DirectoryNode(String name)
    {
        super(name);
    }

    @Override
    public boolean isDirectory()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isFile()
    {
        // TODO Auto-generated method stub
        return false;
    }

}

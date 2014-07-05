package nameserver;

public class NameServerLauncher
{
    public static void main(String[] args)
    {
        NameServer nameServer = new NameServer();
        
        try
        {
            nameServer.initilize();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        
    }
}

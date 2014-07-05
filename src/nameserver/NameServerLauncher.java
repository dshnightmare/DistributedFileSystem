package nameserver;

import common.util.Logger;

public class NameServerLauncher
{
    private final static Logger logger = Logger
        .getLogger(NameServerLauncher.class);

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

        logger.info("NameServer started.");
    }
}

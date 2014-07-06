package storageserver;

import java.io.IOException;

import common.util.Logger;

public class StorageServerLauncher
{
    private final static Logger logger = Logger
        .getLogger(StorageServerLauncher.class);

    public static final String USAGE =
        "USAGE:\tant run_storage -Dbase=<base directory> -Dport=<storage port>";

    public static void main(String[] args)
    {

        final String baseDirectory = System.getProperty("base", "");
        final int port = Integer.valueOf(System.getProperty("port", "-1"));
        
        System.out.println(baseDirectory);
        System.out.println(port);

        if (baseDirectory.isEmpty() || port < 0)
        {
            System.out.println("Failed to start StorageServer, bad parameter.");
            System.out.println(USAGE);
            return;
        }

        StorageServer storageServer;
        try
        {
            storageServer = new StorageServer("baseDirectory");
            storageServer.initAndstart(port);
        }
        catch (Exception e)
        {
            logger.info("Failed to start StorageServer.");
            e.printStackTrace();
        }

        logger.info("StorageServer started.");
    }

}

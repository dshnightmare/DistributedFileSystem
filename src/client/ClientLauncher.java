package client;

import common.util.Log;

public class ClientLauncher
{
    public static void main(String[] args)
    {
        ClientGUI client = new ClientGUI();
        client.init();
        
        Log.info("Client initialize finished.");
    }
}

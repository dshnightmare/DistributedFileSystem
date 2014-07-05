package test.nameserver.ui;

import java.util.concurrent.TimeUnit;

import nameserver.status.StatusEvent;
import nameserver.status.Storage;
import nameserver.status.StatusEvent.Type;
import nameserver.ui.NameServerGUI;
import junit.framework.TestCase;

public class TestGUI
    extends TestCase
{
    public void testGUI()
    {
        NameServerGUI gui = NameServerGUI.getInstance();

        gui.init();

        Storage storage = new Storage("localhost");
        storage.setLoad(70);
        gui.handle(new StatusEvent(Type.STORAGE_REGISTERED, storage));

        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        storage = new Storage("localhost2");
        storage.setLoad(20);
        gui.handle(new StatusEvent(Type.STORAGE_REGISTERED, storage));

        try
        {
            TimeUnit.SECONDS.sleep(3);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

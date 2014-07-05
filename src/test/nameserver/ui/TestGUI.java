package test.nameserver.ui;

import java.util.concurrent.TimeUnit;

import nameserver.meta.File;
import nameserver.meta.Meta;
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
        
        Meta.getInstance().addFile("/123/456/789/", new File("111", 222));
        Meta.getInstance().setFileValid("/123/456/789/", "111", true);

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
            TimeUnit.SECONDS.sleep(30);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

package nameserver.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import nameserver.meta.StatusEvent;
import nameserver.meta.StatusEventListener;
import nameserver.meta.Storage;

public class NameServerGUI
    implements StatusEventListener
{
    private static NameServerGUI instance = new NameServerGUI();

    private JFrame frame = new JFrame("Test");

    private JPanel panel = new JPanel();

    private Map<Storage, StorageInfo> storages =
        new HashMap<Storage, StorageInfo>();

    private NameServerGUI()
    {
    }

    public static NameServerGUI getInstance()
    {
        return instance;
    }

    public void init()
    {
        frame.setBounds(100, 100, 450, 300);
        frame.setLayout(new BorderLayout());
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        frame.add(panel);
        frame.setVisible(true);
    }

    @Override
    public synchronized void handle(StatusEvent event)
    {
        switch (event.getType())
        {
        case STORAGE_DEAD:
            removeStoragePanel(event.getStorage());
            break;
        case STORAGE_REGISTERED:
            addStoragePanel(event.getStorage());
            break;
        }
    }

    private void addStoragePanel(Storage storage)
    {
        final StorageInfo info = new StorageInfo();

        info.updateId(storage.getId());
        info.updateLoad(storage.getLoad());
        storages.put(storage, info);
        panel.add(info.getPanel());

        frame.validate();
    }

    private void removeStoragePanel(Storage storage)
    {
        final StorageInfo panel = storages.remove(storage);

        frame.remove(panel.getPanel());

        frame.validate();
    }
}

package nameserver.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import nameserver.status.StatusEvent;
import nameserver.status.StatusEventListener;
import nameserver.status.Storage;

public class NameServerGUI
    implements StatusEventListener
{
    private static NameServerGUI instance = new NameServerGUI();

    private JFrame frame = new JFrame("Test");

    private JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP,
        JTabbedPane.WRAP_TAB_LAYOUT);

    private JPanel storagePanel = new JPanel();

    private JPanel directoryPanel = new JPanel();

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

        storagePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));

        tab.addTab("Storages", storagePanel);
        tab.addTab("Directory", directoryPanel);
        tab.setSelectedIndex(0);
        frame.add(tab, BorderLayout.CENTER);

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
        storagePanel.add(info.getPanel());

        frame.validate();
    }

    private void removeStoragePanel(Storage storage)
    {
        final StorageInfo panel = storages.remove(storage);

        frame.remove(panel.getPanel());

        frame.validate();
    }
}
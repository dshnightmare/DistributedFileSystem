package nameserver.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import nameserver.status.Storage;

public class StorageInfo
{
    private JPanel panel;

    private JTextArea id;

    private JProgressBar load;

    private JTextArea taskSum;

    private static final ImageIcon serverIcon = new ImageIcon("ico/server.png");

    public StorageInfo()
    {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        id = new JTextArea();
        load = new JProgressBar();
        load.setMinimum(0);
        load.setMaximum(100);

        taskSum = new JTextArea();

        JLabel icon = new JLabel();
        icon.setIcon(serverIcon);
        icon.setHorizontalAlignment(JLabel.CENTER);
        panel.add(icon, BorderLayout.CENTER);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.add(load);
        subPanel.add(taskSum);
        panel.add(id, BorderLayout.NORTH);
        panel.add(subPanel, BorderLayout.SOUTH);
    }

    public void update(Storage storage)
    {
        this.id.setText(storage.getId());
        this.load.setValue(storage.getStorageLoad());
        this.taskSum.setText("Running task: " + storage.getTaskSum());
    }

    public JPanel getPanel()
    {
        return panel;
    }
}

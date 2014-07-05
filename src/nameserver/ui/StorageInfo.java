package nameserver.ui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

public class StorageInfo
{
    private JPanel panel;

    private JTextArea id;

    private JProgressBar load;

    private static final ImageIcon serverIcon = new ImageIcon("ico/server.png");

    public StorageInfo()
    {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        id = new JTextArea();
        load = new JProgressBar();
        load.setMinimum(0);
        load.setMaximum(100);

        JLabel icon = new JLabel();
        icon.setIcon(serverIcon);
        icon.setHorizontalAlignment(JLabel.CENTER);
        panel.add(icon, BorderLayout.CENTER);

        panel.add(id, BorderLayout.NORTH);
        panel.add(load, BorderLayout.SOUTH);
    }

    public void updateId(String id)
    {
        System.out.println("Update Id");
        this.id.setText(id);
        panel.updateUI();
    }

    public void updateLoad(int load)
    {
        System.out.println("Update Load");
        this.load.setValue(load);
        panel.updateUI();
    }

    public JPanel getPanel()
    {
        return panel;
    }
}

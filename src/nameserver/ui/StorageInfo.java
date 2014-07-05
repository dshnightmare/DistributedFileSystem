package nameserver.ui;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class StorageInfo
{
    private JPanel panel;

    private JTextArea id;

    private JTextArea load;

    public StorageInfo()
    {
        panel = new JPanel();
        id = new JTextArea();
        load = new JTextArea();

        panel.add(id);
        panel.add(load);
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
        this.load.setText(String.valueOf(load));
        panel.updateUI();
    }

    public JPanel getPanel()
    {
        return panel;
    }
}

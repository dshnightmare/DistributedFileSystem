package nameserver.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import client.ClientGUI;
import nameserver.meta.Meta;

public class MetaInfo
{
    private Meta meta;

    private JPanel panel;

    private DefaultListModel model;

    private JList list;

    public MetaInfo(Meta meta)
    {
        this.meta = meta;
        panel = new JPanel();

        // create directory
        JButton button = new JButton("refresh");
        button.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                updateList();
            }
        });

        model = new DefaultListModel();
        list = new JList(model);

        panel.add(button, BorderLayout.EAST);
        panel.add(list, BorderLayout.WEST);
    }

    public void updateList()
    {
        model.clear();
        synchronized (meta)
        {
            for (Entry<String, Map<String, String>> e : meta
                .getValidDirectoryList().entrySet())
            {
                if (0 == e.getValue().size())
                {
                    model.addElement(e.getKey());
                }
                for (Entry<String, String> en : e.getValue().entrySet())
                {
                    model.addElement(e.getKey() + en.getKey() + "(" + en.getValue() + ")");
                }
            }
        }

        panel.updateUI();
    }

    public JPanel getPanel()
    {
        return panel;
    }
}

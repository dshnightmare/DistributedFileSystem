package nameserver.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import nameserver.meta.Meta;

/**
 * Meta data information Panel.
 * <p>
 * This panel is pretty ugly..
 * 
 * @author lishunyang
 * 
 */
public class MetaInfo
{
    /**
     * Reference of meta data.
     */
    private Meta meta;

    /**
     * Panel instance.
     */
    private JPanel panel;

    /**
     * Used for change list content dynamically.
     */
    private DefaultListModel model;

    /**
     * Meta data list.
     */
    private JList list;

    /**
     * Construction method.
     * 
     * @param meta
     */
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

    /**
     * Update showing list.
     */
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
                    model.addElement(e.getKey() + en.getKey() + "("
                        + en.getValue() + ")");
                }
            }
        }

        panel.updateUI();
    }

    /**
     * Get panel instance.
     * 
     * @return
     */
    public JPanel getPanel()
    {
        return panel;
    }
}

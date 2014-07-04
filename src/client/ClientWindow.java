package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import common.util.Log;
import common.util.WrapLayout;

public class ClientWindow
{
	
	JFrame frame = new JFrame("DFS");
	JPanel bottomPanel = new JPanel();
	JScrollPane sp;
	//main file panel
	public JPanel filePanel;
	//file item
	public JPanel fileItem;
	
	//current directory
	private String currentDirectory = "/";
	
	private Client client = Client.getInstance();
	
	public void init(){
		frame.setTitle("DFS -- "+currentDirectory);
		frame.setLayout(new BorderLayout());
		filePanel = new JPanel();
		filePanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 15));
		
		List<String> fakeFileList = new ArrayList(Arrays.asList("1.txt", "b/", "c.rmvb")); 
		for(String filename : fakeFileList){
			fileItem = new JPanel();
			fileItem.setLayout(new BorderLayout(0, 1));
			JLabel icon = new JLabel();
			icon.setIcon(IconFactory.getIcon(filename));
			icon.setHorizontalAlignment(JLabel.CENTER);
			JLabel text = new JLabel(filename.replace("/", ""));
			text.setHorizontalAlignment(JLabel.CENTER);
			fileItem.add(icon, BorderLayout.CENTER);
			fileItem.add(text, BorderLayout.SOUTH);
			
			fileItem.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					// TODO double click: open directory
					if(e.getClickCount() == 2){
						JPanel item = (JPanel)e.getSource();
						String pathString = currentDirectory+((JLabel)item.getComponent(1)).getText();
						if( ((ImageIcon)((JLabel)item.getComponent(0)).getIcon()).getDescription().equals("dir"))
						JOptionPane.showMessageDialog(frame, "go into direct "+pathString);
						List<String> dirs = client.getDirectorySync(pathString);	//will block here
						Log.debug(""+dirs.size());
					}
					else if (e.getClickCount() == 1) {
						if(((JPanel)e.getSource()).getBackground() == Color.cyan){
							((JPanel)e.getSource()).setBackground(null);
						}
						else {
							((JPanel)e.getSource()).setBackground(Color.cyan);
						}
					}
				}
			});
			fileItem.setToolTipText(text.getText());
			fileItem.setPreferredSize(new Dimension(45, 55));
			filePanel.add(fileItem);
		}
			
		sp = new JScrollPane(filePanel);
		frame.add(sp, BorderLayout.CENTER);
		
		//bottom -- add file
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		JButton addButton = new JButton("上传文件");
		addButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JFileChooser fChooser = new JFileChooser(".");
				fChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int ret = fChooser.showOpenDialog(frame);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fChooser.getSelectedFile();
					JOptionPane.showMessageDialog(frame, file.getName());
				}
		    }
		});
		bottomPanel.add(addButton);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		
		frame.setSize(700, 433);
		frame.setVisible(true);
	}
	
	/**
	 * return ImageIcon according to file suffix
	 * @author geng yufeng
	 *
	 */
	public static class IconFactory{
		private static final ImageIcon dirIcon = new ImageIcon("ico/folder.png");
		private static final ImageIcon txtIcon = new ImageIcon("ico/pict.png");
		private static final ImageIcon videoIcon = new ImageIcon("ico/pict.png");
		private static final ImageIcon imgIcon = new ImageIcon("ico/pict.png");
		private static final ImageIcon otherIcon = new ImageIcon("ico/pict.png");
		public static ImageIcon getIcon(String filename){
			int lasts = filename.lastIndexOf("/");
			if (lasts == filename.length()-1) {
				dirIcon.setDescription("dir");
				return dirIcon;
			}
			filename = filename.substring(lasts+1, filename.length());
			String[] tmp = filename.split("\\.");
			String suffix = tmp[tmp.length-1];
			if (suffix.equals("txt")) {
				return txtIcon;
			}
			else if (suffix.equals("avi") || suffix.equals("rmvb")) {
				return videoIcon;
			}
			else if (suffix.equals("txt")) {
				return txtIcon;
			}
			return otherIcon;
		}
	}
}
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
import javax.swing.text.FlowView;
import javax.swing.text.View;

import common.util.Log;
import common.util.WrapLayout;

public class ClientGUI
{
	
	JFrame frame = new JFrame("DFS");
	JPanel bottomPanel = new JPanel();
	JPanel topPanel = new JPanel();
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
			
		sp = new JScrollPane(filePanel);
		frame.add(sp, BorderLayout.CENTER);
		
		showDirectory(currentDirectory);
		
		//top 
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 20));

		//create directory
		JButton createDirButton = new JButton("新建目录");
		createDirButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				String subdir = JOptionPane.showInputDialog("请输入子目录名称");
				if(null == subdir)
					return;
				client.createDirectorySync(subdir);
			}
		});
		
		//upload file
		JButton addButton = new JButton("上传文件");
		addButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JFileChooser fChooser = new JFileChooser(".");
				fChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int ret = fChooser.showOpenDialog(frame);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fChooser.getSelectedFile();
					//TODO feed file to task
					client.addFileAsync(currentDirectory, file.getName());
				}
		    }
		});
		
		//refresh
		JButton refreshButton = new JButton("刷新");
		refreshButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				showDirectory(currentDirectory);
			}
		});
		
		//add buttons 
		topPanel.add(createDirButton);
		topPanel.add(addButton);
		topPanel.add(refreshButton);
		
		// TODO bottom panel
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 10));
		
		
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		
		frame.setSize(700, 433);
		frame.setVisible(true);
	}
	
	private void showDirectory(String dir){
		filePanel.removeAll();
		List<String> itemList = client.getDirectorySync(dir);
		for(String filename : itemList){
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
		filePanel.updateUI();
	}
	
	/**
	 * return ImageIcon according to file suffix
	 * @author geng yufeng
	 *
	 */
	private static class IconFactory{
		private static final ImageIcon dirIcon = new ImageIcon("ico/folder.png");
		private static final ImageIcon txtIcon = new ImageIcon("ico/txt.png");
		private static final ImageIcon videoIcon = new ImageIcon("ico/avi.png");
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
package client;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.text.FlowView;
import javax.swing.text.View;

import common.event.TaskEvent;
import common.event.TaskEventListener;
import common.util.Log;
import common.util.WrapLayout;

public class ClientGUI
	implements TaskEventListener
{
	
	JFrame frame = new JFrame("DFS");
	JPanel bottomPanel = new JPanel();
	JPanel topPanel = new JPanel();
	JScrollPane sp;
	//main file panel
	public JPanel filePanel;
	//file item
	public JPanel fileItem;
	//goback button
	JButton backButton;
	//right click
	JPopupMenu rightClickPop = new JPopupMenu();
	JPopupMenu mainRightClickPop = new JPopupMenu();
	JMenuItem menuDelete = new JMenuItem("删除");
	JMenuItem menuCut = new JMenuItem("剪切");
	JMenuItem menuPaste = new JMenuItem("粘贴");
	JMenuItem menuPaste1 = new JMenuItem("粘贴");
	JMenuItem menuRefresh = new JMenuItem("刷新");
	
	//current directory
	private String currentDirectory = "/";
	private String selectedItem = null;
	private String cutDir = null, cutName = null;
	
	private Client client = Client.getInstance();
	
	public void init(){
		frame.setTitle("DFS -- "+currentDirectory);
		frame.setLayout(new BorderLayout());
		filePanel = new JPanel();
		filePanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 15));

		menuRefresh.addActionListener(new ActionListener() {     
	           public void actionPerformed(ActionEvent e) {
	        	   showDirectory(currentDirectory);
	           }
	        });
		menuPaste.addActionListener(new ActionListener() {     
	           public void actionPerformed(ActionEvent e) {
					client.addListener(ClientGUI.this);
					Log.debug("To move from:"+cutDir+cutName
							+" to:"+currentDirectory+cutName);
					client.moveFileDirectASync(cutDir, cutName, currentDirectory, cutName);
					cutDir = null;
					cutName = null;
	           }
	        });
		mainRightClickPop.add(menuRefresh);
		mainRightClickPop.add(menuPaste);
		filePanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON3) {
					return;
				}
				JPanel item = (JPanel)e.getSource();
				if (null == cutDir) {
					menuPaste.setEnabled(false);
				}
				else {
					menuPaste.setEnabled(true);
				}
				mainRightClickPop.show(e.getComponent(), e.getX(), e.getY());
			}
		});
			
		sp = new JScrollPane(filePanel);
		frame.add(sp, BorderLayout.CENTER);
		
		showDirectory(currentDirectory);
		
		//right click popup menu
		menuDelete.addActionListener(new ActionListener() {     
           public void actionPerformed(ActionEvent e) {  
				client.addListener(ClientGUI.this);
				Log.debug("To delete:"+currentDirectory+selectedItem);
				client.removeFileDirectASync(currentDirectory, selectedItem);
				selectedItem = null;
           }
        });
		menuCut.addActionListener(new ActionListener() {     
           public void actionPerformed(ActionEvent e) {
        	   cutDir = currentDirectory;
        	   cutName = selectedItem;
           }
        });
		menuPaste1.addActionListener(new ActionListener() {     
	           public void actionPerformed(ActionEvent e) {
					client.addListener(ClientGUI.this);
					Log.debug("To move from:"+cutDir+cutName
							+" to:"+currentDirectory+cutName);
					client.moveFileDirectASync(cutDir, cutName, currentDirectory, cutName);
					cutDir = null;
					cutName = null;
	           }
	        });
		
		rightClickPop.add(menuDelete);
		rightClickPop.add(menuCut);
		rightClickPop.add(menuPaste1);
		
		//top 
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 20));

		//create directory
		JButton createDirButton = new JButton("新建目录");
		createDirButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				String subdir = JOptionPane.showInputDialog("请输入子目录名称");
				if(null == subdir || subdir.equals(""))
					return;
				client.addListener(ClientGUI.this);
				client.createDirectoryASync(currentDirectory+subdir+"/");
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
					client.addListener(ClientGUI.this);
					client.addFileAsync(currentDirectory, file.getName());
				}
		    }
		});
		
		
		//goback
		backButton = new JButton("返回上层");
		backButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				int lastx = currentDirectory
						.substring(0, currentDirectory.length()-1).lastIndexOf("/");
				currentDirectory = currentDirectory.substring(0, lastx+1);
				if (currentDirectory.equals("/")) {
					backButton.setEnabled(false);
				}
				showDirectory(currentDirectory);
				frame.setTitle("DFS -- "+currentDirectory);
			}
		});
		backButton.setEnabled(false);
		
		//add buttons 
		topPanel.add(createDirButton);
		topPanel.add(addButton);
		topPanel.add(backButton);
		
		
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		
		frame.setSize(700, 433);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/**
	 * show the contents of directory
	 * @param dir full path directory address
	 */
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
					JPanel item = (JPanel)e.getSource();
					//right click
					if(e.getButton() == MouseEvent.BUTTON3){
						for(int i=0; i<filePanel.getComponents().length; i++){
							((JPanel)filePanel.getComponent(i)).setBackground(null);
						}
						item.setBackground(Color.cyan);
						selectedItem = ((JLabel)item.getComponent(1)).getText();
						if (((ImageIcon)((JLabel)item.getComponent(0))
							.getIcon()).getDescription().equals("dir")) {
							selectedItem += "/";
						}
						if (null == cutDir) {
							menuPaste.setEnabled(false);
						}
						else {
							menuPaste.setEnabled(true);
						}
						rightClickPop.show(e.getComponent(), e.getX(), e.getY());
						return;
					}
					if(e.getClickCount() == 2){
						//directory
						if( ((ImageIcon)((JLabel)item.getComponent(0))
								.getIcon()).getDescription().equals("dir")){
							String pathString = currentDirectory+((JLabel)item.getComponent(1)).getText()+"/";
							currentDirectory = pathString;
							backButton.setEnabled(true);
							showDirectory(pathString);
							frame.setTitle("DFS -- "+currentDirectory);
						}
					}
					//one click for choosing
					else if (e.getClickCount() == 1) {
						Color origin = ((JPanel)e.getSource()).getBackground();
						for(int i=0; i<filePanel.getComponents().length; i++){
							((JPanel)filePanel.getComponent(i)).setBackground(null);
						}
						if(origin == Color.cyan){
							item.setBackground(null);
							selectedItem = null;
						}
						else {
							item.setBackground(Color.cyan);
							selectedItem = ((JLabel)((JPanel)e.getSource()).getComponent(1)).getText();
							if (((ImageIcon)((JLabel)item.getComponent(0))
								.getIcon()).getDescription().equals("dir")) {
								selectedItem += "/";
							}
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
		private static final ImageIcon imgIcon = new ImageIcon("ico/pic.png");
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
			else if (suffix.equals("png") || suffix.equals("jpg")) {
				return imgIcon;
			}
			return otherIcon;
		}
	}

	@Override
	public void handle(TaskEvent event) {
		// TODO Auto-generated method stub
		showDirectory(currentDirectory);
	}
}
package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import common.util.WrapLayout;

public class ClientWindow
{
	
	JFrame frame = new JFrame("DFS");
	JPanel bottomPanel = new JPanel();
	JScrollPane sp;
	JPanel panel;
	JPanel fileIcon;
	
	//current directory
	private String currentDirectory = ".";
	
	public void init(){
		frame.setLayout(new BorderLayout());
		panel = new JPanel();
		panel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 15));
		

		for(int i=0; i<20; i++){
			fileIcon = new JPanel();
			fileIcon.setSize(20, 30);
			fileIcon.setLayout(new BorderLayout(0, 1));
			JLabel icon = new JLabel();
			JLabel text = new JLabel("file_"+i);
			icon.setIcon(new ImageIcon("ico/folder.png"));
			fileIcon.add(icon, BorderLayout.CENTER);
			fileIcon.add(text, BorderLayout.SOUTH);
			
			fileIcon.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					JOptionPane.showMessageDialog(frame, "go into direct "
				+((JLabel)((JPanel)e.getSource()).getComponent(1)).getText());
				}
			});
			fileIcon.setToolTipText("will this");
			panel.add(fileIcon);
		}
			
		sp = new JScrollPane(panel);
		frame.add(sp, BorderLayout.CENTER);
		
		//bottom-- add file
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
}
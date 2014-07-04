package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import common.util.WrapLayout;

public class ClientWindow
{
	
	JFrame frame = new JFrame("DFS");
	JScrollPane sp;
	JPanel panel;
	JPanel fileIcon;
	
	public void init(){
		panel = new JPanel();
		panel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 15));
		

		for(int i=0; i<20; i++){
			fileIcon = new JPanel();
			fileIcon.setSize(20, 30);
			fileIcon.setLayout(new BorderLayout(0, 1));
			JLabel fileicon = new JLabel();
			fileicon.setIcon(new ImageIcon("ico/dsk.png"));
			fileIcon.add(fileicon, BorderLayout.CENTER);
			fileIcon.add(new JLabel("wfjm"), BorderLayout.SOUTH);
			panel.add(fileIcon);
		}
			
		sp = new JScrollPane(panel);
		
		frame.add(sp);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}
}
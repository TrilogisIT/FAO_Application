/*
 * Copyright (C) 2014 Trilogis S.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.trilogis.worldwind.tilecreation.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import it.trilogis.worldwind.tilecreation.constants.GUIConstants;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * @author nmeneghini
 * @version $Id: AboutDialog.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class AboutDialog extends JFrame{

	
	private static final long serialVersionUID = -6538846370052276559L;

	public AboutDialog(){
		JPanel mainPanel = new JPanel();
    	
    	
    	this.setContentPane(mainPanel);
    	mainPanel.setLayout(new BorderLayout(0, 0));
    	
    	JPanel panel = new JPanel();
    	panel.setBorder(new EmptyBorder(15, 15, 15, 15));
    	mainPanel.add(panel, BorderLayout.NORTH);
    	panel.setLayout(new BorderLayout(0, 0));
    	
    	BufferedImage myPicture=null;
		try {
			myPicture = ImageIO.read(new File(GUIConstants.IMAGE_URL_ICON_TRILOGIS));
		} catch (IOException e) {
		}
    	JLabel label = null;
    	if(myPicture!=null){
    		label= new JLabel(new ImageIcon(myPicture));
    	}else{
    		label = new JLabel("Trilogis Srl");
    	}
    	label.setVerticalAlignment(SwingConstants.TOP);
    	panel.add(label, BorderLayout.NORTH);
    	label.setHorizontalAlignment(SwingConstants.LEFT);
    	label.setFont(new Font("Tahoma", Font.BOLD, 16));
    	
    	JPanel panel_3 = new JPanel();
    	panel.add(panel_3, BorderLayout.SOUTH);
    	
    	JPanel panel_2 = new JPanel();
    	
    	JLabel label_1 = new JLabel("<html>Contacts: <br/> Trilogis S.r.l. <br/> Via F.Zeni n.8 - 38068 Rovereto (TN) Italy	<br/>Telephone number: +39 0464 443222 	<br/>Fax: +39 0464 443223"
    			+"<br/>	Website: www.trilogis.it<br/>Email: info@trilogis.it<br/>	Facebook: Trilogis	<br/>	Twitter: @trilogis	<br/> Google+: +Trilogis srl</html>");
    	panel_2.add(label_1);
    	label_1.setVerticalAlignment(SwingConstants.TOP);
    	label_1.setHorizontalAlignment(SwingConstants.LEFT);
    	label_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
    	
    	
    	GroupLayout gl_panel_3 = new GroupLayout(panel_3);
    	gl_panel_3.setHorizontalGroup(
    		gl_panel_3.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel_3.createSequentialGroup()
    				.addGap(10)
    				.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    	);
    	gl_panel_3.setVerticalGroup(
    		gl_panel_3.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel_3.createSequentialGroup()
    				.addGap(5)
    				.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    	);
    	
    	panel_3.setLayout(gl_panel_3);
    	
    	JPanel panel_1 = new JPanel();
    	mainPanel.add(panel_1, BorderLayout.SOUTH);
    	this.setSize(300, 350);
    	this.setLocationRelativeTo(null);
    	this.setResizable(false);
    	this.setAlwaysOnTop(true);
    	
        ImageIcon icon = new ImageIcon(GUIConstants.IMAGE_URL_ABOUT_ICON);
        this.setIconImage(icon.getImage());
        this.setTitle(GUIConstants.ABOUT_TITLE);

    	this.setVisible(true);
	}
}

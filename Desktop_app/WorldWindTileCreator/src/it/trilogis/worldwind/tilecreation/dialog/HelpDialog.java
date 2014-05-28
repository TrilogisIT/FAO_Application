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

import it.trilogis.worldwind.tilecreation.constants.GUIConstants;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

/**
 * @author nmeneghini
 * @version $Id: HelpDialog.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class HelpDialog  extends JFrame{

	
	private static final long serialVersionUID = -6538846370052276559L;

	public HelpDialog(){
    	
    	JPanel mainPanel = new JPanel();
    	
    	
    	this.setContentPane(mainPanel);
    	mainPanel.setLayout(new BorderLayout(0, 0));
    	
    	JPanel panel = new JPanel();
    	panel.setBorder(new EmptyBorder(5, 5, 5, 5));
    	mainPanel.add(panel, BorderLayout.NORTH);
    	panel.setLayout(new BorderLayout(0, 0));
    	JLabel label = new JLabel("How to use");
    	label.setVerticalAlignment(SwingConstants.TOP);
    	panel.add(label, BorderLayout.NORTH);
    	label.setHorizontalAlignment(SwingConstants.LEFT);
    	label.setFont(new Font("Tahoma", Font.BOLD, 16));
    	
    	JPanel panel_3 = new JPanel();
    	panel.add(panel_3, BorderLayout.CENTER);
    	
    	JPanel panel_2 = new JPanel();
    	
    	JLabel label_1 = new JLabel("<html> How to create tiles for eLocust3 3D mobile app <br/> 1. Set folder where you want to generate the tiles. "
    								+"<br/> 2. Set bounding box of the part of the world you want to see. "
    								+"<br/> 3. Enable the layers which you want to show. <br/> 4. Select for each one the files image georeferenced."
    								+"<br/> 5. Click on import all. <br/> 6. Click on create package. "
    								+"<br/><br/> Now you have the wwtiles.zip file: <br/> copy it on the device or on SDcard and insert it in the device. "
    								+"<br/>Run eLocust3 3D and save the world from locusts!!");
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
    	
//    	JPanel panel_4 = new JPanel();
//    	panel.add(panel_4, BorderLayout.SOUTH);
    	
    	JPanel panelInf = new JPanel();
    	mainPanel.add(panelInf, BorderLayout.CENTER);
    	panelInf.setBorder(new EmptyBorder(5, 5, 5, 5));
    	panelInf.setLayout(new BorderLayout(0, 0));
    	JLabel labelInf = new JLabel("Information about layers");
    	labelInf.setVerticalAlignment(SwingConstants.TOP);
    	panelInf.add(labelInf, BorderLayout.NORTH);
    	labelInf.setHorizontalAlignment(SwingConstants.LEFT);
    	labelInf.setFont(new Font("Tahoma", Font.BOLD, 16));
    	
    	JPanel panelLay = new JPanel();
    	panelInf.add(panelLay, BorderLayout.CENTER);
    	
    	JPanel panel_des = new JPanel();
    	
    	JLabel label_des = new JLabel("<html> TPC: Tactical FAO map "
    								+"<br/> LandSat: Satellite imagery from NASA "
    								+"<br/> Greenness: Indicates the number of dekads passed since vegetation onset <br/> Rainfall: Accumulated rainfall during a period based on estimates "
    								+"<br/> Elevation: Indicates the altitude to generate the elevation relief <br/> Boundaries: Political boundaries ");
    	panel_des.add(label_des);
    	label_des.setVerticalAlignment(SwingConstants.TOP);
    	label_des.setHorizontalAlignment(SwingConstants.LEFT);
    	label_des.setFont(new Font("Tahoma", Font.PLAIN, 12));
    	
    	
    	GroupLayout gl_panel_lay = new GroupLayout(panelLay);
    	gl_panel_lay.setHorizontalGroup(
    			gl_panel_lay.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel_lay.createSequentialGroup()
    				.addGap(10)
    				.addComponent(panel_des, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    	);
    	gl_panel_lay.setVerticalGroup(
    			gl_panel_lay.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel_lay.createSequentialGroup()
    				.addGap(5)
    				.addComponent(panel_des, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    	);
    	
    	panelLay.setLayout(gl_panel_lay);
    	
    	
    	
    	this.setSize(440, 420);
    	this.setLocationRelativeTo(null);
    	this.setResizable(false);
    	this.setAlwaysOnTop(true);
    	
        ImageIcon icon = new ImageIcon(GUIConstants.IMAGE_URL_HELP_ICON);
        this.setIconImage(icon.getImage());
        this.setTitle(GUIConstants.HELP_TITLE);

    	this.setVisible(true);
	}
}

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
package it.trilogis.worldwind.tilecreation.properties;

import it.trilogis.worldwind.tilecreation.constants.PropertiesConstants;
import it.trilogis.worldwind.tilecreation.swing.utils.ImprovedFormattedTextField;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 * @author nmeneghini
 * @version $Id: PropertiesUtils.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
 public class PropertiesUtils {

	public static void assignListFilesFromProperties(String propertyName, JFileChooser fileChooser, JList list){
		String[] files = PropertiesManager.getArrayStringProperty(propertyName);
		
	    if(!(null==files || files.length==0)){
	    	ArrayList<File> filesAccepted = new ArrayList<File>();
	    	list.setModel(new DefaultListModel());
	    	for(int i=0;i<files.length;i++){
	    		File file = new File(fileChooser.getCurrentDirectory()+File.separator+files[i]);
	    		if(file.exists()){
                    ((DefaultListModel)list.getModel()).add(i,files[i]);
	    			//if(fileChooser.accept(file)){
	    				filesAccepted.add(file);
	    			//}
	    		}
	    	}
	    	File[] temp = new File[filesAccepted.size()];
			fileChooser.setSelectedFiles(filesAccepted.toArray(temp));
	   	}
	}
	
	
	public static void assignValueFromProperties(String propertyName, JTextField field){
		field.setText(PropertiesManager.getStringProperty(propertyName));
	}
	
	public static void assignValueFromProperties(String propertyName, ImprovedFormattedTextField field){
	    field.setValue(PropertiesManager.getDoubleProperty(propertyName));
	} 
	
	
	public static void assignDirPathFromProperties(String propertyName, JFileChooser fileChooser){
		File dir;
	    String dirname=PropertiesManager.getStringProperty(propertyName);
	    if(null==dirname || dirname.equals("")){
	    	dir= new File(PropertiesConstants.DEFAULT_DIRECTORY);
	    }else{
		    dir = new File(dirname);
		    if(!dir.exists() || !dir.isDirectory()){
		    	dir=new File(PropertiesConstants.DEFAULT_DIRECTORY);
		    }
		}
	    fileChooser.setCurrentDirectory(dir);
	}
	
}

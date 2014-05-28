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
package it.trilogis.android.fao.utils;

import it.trilogis.android.fao.constants.Constants;

import java.io.File;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.terrain.BasicElevationModelFactory;
import gov.nasa.worldwind.terrain.ElevationModel;
import gov.nasa.worldwind.util.DataConfigurationUtils;
import gov.nasa.worldwind.util.WWXML;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class WorldWindUtils {


    public static void setFallbackParams(Document dataConfig, String filename, AVList params) {
		XPath xpath = WWXML.makeXPath();
		Element domElement = dataConfig.getDocumentElement();

		// If the data configuration document doesn't define a cache name, then compute one using the file's path
		// relative to its file cache directory.
		String s = WWXML.getText(domElement, "DataCacheName", xpath);
		if (s == null || s.length() == 0) DataConfigurationUtils.getDataConfigCacheName(filename, params);

		// If the data configuration document doesn't define the data's extreme elevations, provide default values using
		// the minimum and maximum elevations of Earth.
		String type = DataConfigurationUtils.getDataConfigType(domElement);
		if (type.equalsIgnoreCase("ElevationModel")) {
			if (WWXML.getDouble(domElement, "ExtremeElevations/@min", xpath) == null) params.setValue(AVKey.ELEVATION_MIN, Earth.ELEVATION_MIN);
			if (WWXML.getDouble(domElement, "ExtremeElevations/@max", xpath) == null) params.setValue(AVKey.ELEVATION_MAX, Earth.ELEVATION_MAX);
		}
	}

	public static ElevationModel getElevationModel(File xml) {
		try {
			if(xml.exists()){
				Document doc=null;
				
				// Get standard document
				doc = WWXML.openDocument(xml);
				doc = DataConfigurationUtils.convertToStandardDataConfigDocument(doc);
				
				AVList params = new AVListImpl();
				WorldWindUtils.setFallbackParams(doc, Constants.ELEVATION_NAME, params);
				
				// Return elevation model in xml file
				BasicElevationModelFactory b = new BasicElevationModelFactory();
				return (ElevationModel) b.createFromConfigSource(xml, params);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

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
package it.trilogis.worldwind.tilecreation.constants;

import java.awt.Color;

/**
 * @author nmeneghini
 * @version $Id: GUIConstants.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class GUIConstants {


	//GUI COLOR CONSTANTS
	public final static Color COLOR_NEUTRAL = new Color(255,255,255);
	public final static Color COLOR_BACKGROUND = new Color(60,179,113);
	public final static Color COLOR_OVERLAY = new Color(99,151,208);
	public final static Color COLOR_ELEVATION = new Color(205,127,50);
	public final static Color COLOR_BOUNDARIES = new Color(127,10,10);
	
	//GUI Title layer type
	public final static String LAYER_TYPE_BACKGROUND = "Background";
	public final static String LAYER_TYPE_OVERLAY = "Overlay";
	public final static String LAYER_TYPE_ELEVATION = "Elevation";
	public final static String LAYER_TYPE_BOUNDARIES = "Boundaries";
	
	
	//GUI Title layer name
	public final static String LAYER_NAME_TPC = "TPC";
	public final static String LAYER_NAME_LANDSAT = "LandSat";
	public final static String LAYER_NAME_GREENNESS = "Greenness";
	public final static String LAYER_NAME_RAINFALL = "Rainfall";
	public final static String LAYER_NAME_ELEVATION = "Elevation";
	public final static String LAYER_NAME_BOUNDARIES = "Boundaries";
	
	
	//Images url
	public final static String IMAGE_URL_APP_ICON = "res/images/app_icon.png";
	public final static String IMAGE_URL_FILE_ICON = "res/images/file_icon.png";
	public final static String IMAGE_URL_ABOUT_ICON = "res/images/about_trilogis_icon.png";
	public final static String IMAGE_URL_HELP_ICON = "res/images/help_icon.png";
	public final static String IMAGE_URL_ICON_TRILOGIS = "res/images/logo_tri.png";
	
	
	//Titles
	public final static String APPLICATION_TITLE = "Tile Creator Elocust 3 3D";
	public final static String ABOUT_TITLE = "Trilogis about";
	public final static String HELP_TITLE = "Help";
}

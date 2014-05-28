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
package it.trilogis.android.fao.constants;

import gov.nasa.worldwind.render.Color;

/**
 * @author Nicola Meneghini
 */
public class Constants {

	// Default values of view
	public final static double DEFAULT_VIEW_HEADING = 0d;
	public final static double DEFAULT_VIEW_TILT = 45d; 
	public final static double LAST_LOCATION_VIEW_DISTANCE_KM = 256000d;
	public final static double DEFAULT_LATITUDE = 5d; 
	public final static double DEFAULT_LONGITUDE =20d;
	public final static double DEFAULT_VIEW_DISTANCE_KM = 20000000d;
	public final static double DEFAULT_GPS_DISTANCE_KM = 70000d;
	
	
	// Default settings
	public final static int DEFAULT_EXAGGERATION = 1;
	public final static boolean DEFAULT_GPS_UPDATE = true;
	public final static int DEFAULT_GPS_SECONDS = 1;
	public final static int DEFAULT_GPS_METERS = 10;
	
	
	// Properties name
	public final static String PROP_EXAGGERATION = "exaggeration";
	public final static String PROP_GPS_UPDATE = "gpsUpdate";
	public final static String PROP_GPS_SECONDS = "gpsSeconds";
	public final static String PROP_GPS_METERS = "gpsMeters";
	
    
    public final static long GPS_RESET = 120000;
	
	// GPSMarker constants
	public final static double MARKER_SIDE = 0.01;
	public final static Color MARKER_COLOR = new Color(248/255d,153/255d,29/255d);
	public final static double MARKER_HEIGHT = 1200;
	
	
	// Trilogis About local URL
	public static final String TRILOGIS_ABOUT_URL = "file:///android_asset/trilogisabout/trilogisc.html";

	// Files constants
	public final static String EXTERNAL_PATH = "/mnt/external_sdcard/";
	public final static String FILE_NAME = "wwtiles.zip";
	public final static String URLWORLD = "WorldWindData/";
	public final static String URLTILES = "Earth/";

	// Layer constants
	public final static String ELEVATION_NAME = "Elevation2";
	public final static String[] layers = new String[]{"TPC","LandSat","Greenness","Rainfall","Boundaries"};
	
}
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

package it.trilogis.android.fao;

import gov.nasa.worldwind.BasicFactory;
import gov.nasa.worldwind.BasicView;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindowGLSurfaceView;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.GPSMarker;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.terrain.ElevationModel;
import gov.nasa.worldwind.terrain.ZeroElevationModel;
import it.trilogis.android.fao.constants.Constants;
import it.trilogis.android.fao.unzip.ClearCacheTask;
import it.trilogis.android.fao.unzip.UnzipTask;
import it.trilogis.android.fao.utils.FileUtils;
import it.trilogis.android.fao.utils.WorldWindUtils;
import it.trilogis.android.ww.dialogs.GpsSettingsDialog;
import it.trilogis.android.ww.dialogs.SettingsDialogFragment;
import it.trilogis.android.ww.dialogs.TrilogisAboutDialogFragment;
import it.trilogis.android.ww.dialogs.SettingsDialogFragment.OnSettingsChangedListener;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * @author Nicola Dorigatti, Nicola Meneghini
 */
public class WorldWindActivity extends FragmentActivity implements UnzipTask.Callbacks, ClearCacheTask.Callbacks, GpsStatus.Listener{
	
	private LocationManager lm = null;
	
	private RenderableLayer markerLayer;
	private boolean firstLoc =true;
	

    // Setup the config file at runtime
    static {
        System.setProperty("gov.nasa.worldwind.app.config.document", "config/wwandroidfao.xml");
    }

    public static final String TAG = "FAOWWApplication";
    
    private boolean fileEx = false;
    private boolean fileFound=false;
    private File mFile =null;
    
    // WW items
    protected WorldWindowGLSurfaceView wwd;

    private boolean[] layersExist = new boolean[]{false,false,false,false,false};
    
    private boolean[] layersEnabled = new boolean[]{true,true,true,true,true};
    
    private int exaggeration;
    private int gpsMeters;
    private int gpsSeconds;
    private boolean gpsUpdate;
    private boolean gpsIsOn;
    Timer timerGPS;  
    SettingsDialogFragment settings=null;
    GpsSettingsDialog gpsSettings = null;
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        checkExistinglayers();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
        
        for(int i = 0;i<Constants.layers.length;i++){
        	layersEnabled[i] = prefs.getBoolean(Constants.layers[i], layersExist[i]);
        	if(layersEnabled[i]&&!layersExist[i]){
        		layersEnabled[i]=false;
        	}
        }
        
        if(layersEnabled[0]&&layersEnabled[1]){
        	layersEnabled[1]=false;
        }
        exaggeration = prefs.getInt(Constants.PROP_EXAGGERATION, Constants.DEFAULT_EXAGGERATION);
        gpsUpdate = prefs.getBoolean(Constants.PROP_GPS_UPDATE, Constants.DEFAULT_GPS_UPDATE);
        gpsSeconds = prefs.getInt(Constants.PROP_GPS_SECONDS, Constants.DEFAULT_GPS_SECONDS);
        gpsMeters = prefs.getInt(Constants.PROP_GPS_METERS, Constants.DEFAULT_GPS_METERS);
        
        //control if it's possible save cache files
        File fileDir = getExternalCacheDir();
        if(null==fileDir){
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        	alert.setMessage(R.string.alert_message_nocache);
        	alert.setCancelable(false);
        	alert.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
        	alert.show();
        }
        if (null != fileDir && fileDir.exists() && fileDir.canWrite()) {
            // create .nomedia file, so pictures will not be visible in the gallery (otherwise, it's really awful to see all of the tiles as images!)
            File output = new File(fileDir, ".nomedia");
            if (output.exists()) {
                Log.d(TAG, "No need to create .nomedia file, it's already there! : " + output.getAbsolutePath());
            } else {
                // lets create the file
                boolean fileCreated = false;
                try {
                    fileCreated = output.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, "IOException while creating .nomedia: " + e.getMessage());
                }
                if (!fileCreated) {
                    Log.e(TAG, ".nomedia file not created!");
                } else {
                    Log.d(TAG, ".nomedia file created!");
                }
            }
            //find and unpack zip
            findFileZip();
        }
        
        
        // Setup system property for the file store
        System.setProperty("gov.nasa.worldwind.platform.user.store", fileDir.getAbsolutePath());
        Log.w(TAG, "Directory is: " + fileDir.getAbsolutePath());
        // set the contentview
        this.setContentView(R.layout.activity_world_wind);
        //can start using context and views
        
        //location initialize
        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        lm.addGpsStatusListener(this);
		        
        // And initialize the WorldWindow Model and View
        this.wwd = (WorldWindowGLSurfaceView) this.findViewById(R.id.wwd);
        this.wwd.setModel((Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME));
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
           	this.setupView();
        } else {
           	//if is call by another app
        	this.setupCustomView(extras);
        }
        this.setupTextViews();
    }
    
	@Override
    protected void onPause() {
        super.onPause();
        
        // Pause the OpenGL ES rendering thread.
        this.wwd.onPause();
        
        // Pause GPS search and timer
        if(null!=timerGPS)
        	timerGPS.cancel();
        if(null!=lm)
        	lm.removeUpdates(locationListener);	
    }
    

    @Override
    protected void onResume() {
        super.onResume();
                
        // Resume the OpenGL ES rendering thread.
        this.wwd.onResume();
        
        // Set view at last GPS position and search position
        if(gpsUpdate){
			setLookAtLastLocation();
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsSeconds*1000,gpsMeters, locationListener);
	        startGPSTimer();
		}

        // Add enable layers
        addElevationLayer();
        addLayers();
    }


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.options, menu);
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_trilogis:
            	// Trilogis about
            	TrilogisAboutDialogFragment aboutDialog = new TrilogisAboutDialogFragment();
				aboutDialog.show(getSupportFragmentManager(), "TrilogisAboutDialog");
                break;
            case R.id.settings:
            	// Settings
            	settings = new SettingsDialogFragment(layersExist,layersEnabled,exaggeration,gpsSeconds,gpsMeters,gpsUpdate);
            	settings.setOnSettingChangeListener(settingsListener);
            	settings.setCancelable(false);
            	settings.show(getFragmentManager(), "Settings");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    

    // Set the view with default parameters.
    protected void setupView() {
        BasicView view = (BasicView) this.wwd.getView();
        Globe globe = this.wwd.getModel().getGlobe();
        
        // Set the initial position to "Africa"
        view.setLookAtPosition(Position.fromDegrees(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE,globe.getElevation(Angle.fromDegrees(Constants.DEFAULT_LATITUDE), Angle.fromDegrees(Constants.DEFAULT_LONGITUDE))));
        view.setHeading(Angle.fromDegrees(Constants.DEFAULT_VIEW_HEADING));
        view.setTilt(Angle.fromDegrees(Constants.DEFAULT_VIEW_TILT));
        view.setRange(Constants.DEFAULT_VIEW_DISTANCE_KM);
    }

    // Set the view with custom parameters passed with bundle.
    protected void setupCustomView(Bundle bundle) {
    	try{
    		
    		// Get parameters
    		double lat = bundle.getDouble("latitude",Constants.DEFAULT_LATITUDE);
	    	double lon = bundle.getDouble("longitude",Constants.DEFAULT_LONGITUDE);
	    	double range = bundle.getDouble("range",Constants.DEFAULT_VIEW_DISTANCE_KM);
	    	double heading = bundle.getDouble("heading",Constants.DEFAULT_VIEW_HEADING);
	    	double tilt = bundle.getDouble("tilt",Constants.DEFAULT_VIEW_TILT);
	    	
	        BasicView view = (BasicView) this.wwd.getView();
	        Globe globe = this.wwd.getModel().getGlobe();
	        
	        // Set the initial position to lat and long passed, where you can see the WMS Layers
	        if(lat>0&&lon>0){
		        view.setLookAtPosition(Position.fromDegrees(lat, lon,
		            globe.getElevation(Angle.fromDegrees(lat), Angle.fromDegrees(lon))));
	        }else{
	        	view.setLookAtPosition(Position.fromDegrees(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE,globe.getElevation(Angle.fromDegrees(Constants.DEFAULT_LATITUDE), Angle.fromDegrees(Constants.DEFAULT_LONGITUDE))));
	        }
	        
	        // Set range
	        if(range>0){
		        view.setRange(range);
    		}else{
	            view.setRange(Constants.DEFAULT_VIEW_DISTANCE_KM);
	        }
	        
	        // Set the direction of view
	        if(heading>=0&&heading<=360){
	        	view.setHeading(Angle.fromDegrees(heading));
	        }else{
	        	view.setHeading(Angle.fromDegrees(Constants.DEFAULT_VIEW_HEADING));
	        }
	        
	        // Set angle of view
	        if(tilt>=0&&tilt<=90){
	        	view.setTilt(Angle.fromDegrees(tilt));
	        }else{
	            view.setTilt(Angle.fromDegrees(Constants.DEFAULT_VIEW_TILT));
	        }
	        
    	}catch(Exception e){
    		setupView();
    	}
    }
    
    
    protected void setupTextViews() {
    	// Set textviews where wwd print latitude and longitude of the point clicked 
        TextView latTextView = (TextView) findViewById(R.id.latvalue);
        this.wwd.setLatitudeText(latTextView);
        TextView lonTextView = (TextView) findViewById(R.id.lonvalue);
        this.wwd.setLongitudeText(lonTextView);
        this.wwd.setCoordinateInDegrees(true);
    }

    
    private void findFileZip(){
    	
        try{
        	mFile = new File(Constants.EXTERNAL_PATH+"/",Constants.FILE_NAME);
        	if( mFile.exists() ){
        		fileEx=true;
        		fileFound=true;
        	}else{
        		fileEx=false;
            	fileFound=false;
        	}
        }catch(Exception e){
        	fileEx=false;
        	fileFound=false;
        }
        if(!fileEx){
        	mFile = new File(Environment.getExternalStorageDirectory()+"/", Constants.FILE_NAME);
        	if( mFile.exists() ){
        		fileFound=true;
        	}
        }
        
        if(fileFound){
        	File dir = new File(getExternalCacheDir()+"/"+Constants.URLWORLD);
        	long size = FileUtils.folderSize(dir);
        	if(size>1048576){
        		AlertDialog.Builder alertDialog = new AlertDialog.Builder(WorldWindActivity.this);

        		alertDialog.setCancelable(false);
            	alertDialog.setTitle(R.string.unzip_dialog_title);
            	alertDialog.setMessage(R.string.unzip_dialog_text);

        	    alertDialog.setPositiveButton(R.string.replace, new DialogInterface.OnClickListener() {

        	      public void onClick(DialogInterface dialog, int id) {
        	    	  // Clear cache and start unzip when it finish
        	    	  ClearCacheTask c = new ClearCacheTask(true,WorldWindActivity.this,WorldWindActivity.this);
        	    	  c.execute();
        	    } }); 

        	    alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

        	      public void onClick(DialogInterface dialog, int id) {
        	    	  // Do nothing
        	    }}); 

        	    alertDialog.setNeutralButton(R.string.append, new DialogInterface.OnClickListener() {

        	      public void onClick(DialogInterface dialog, int id) {
        	    	  unzipFile();
        	    }});
            	alertDialog.show();
        	}else{
        		unzipFile();
        	}
        	
        }else{
        	// Control if there are tiles cached
        	File dir = new File(getExternalCacheDir()+"/"+Constants.URLWORLD);
        	long size = FileUtils.folderSize(dir);
        	if(size<1048576){
        		AlertDialog.Builder alert = new AlertDialog.Builder(this);
            	alert.setMessage(R.string.alert_message_nolayers);
            	alert.setCancelable(false);
            	alert.setNegativeButton(android.R.string.ok, null);
            	alert.show();
        	}
        	
        }
    }
    
   
    public void redrawWorld() {
		wwd.redraw();
	}


	// Define a listener that responds to location updates
 	LocationListener locationListener = new LocationListener() {
 		public void onLocationChanged(Location location) {
 			if (null!=wwd) {
 				
 				BasicView view = (BasicView) wwd.getView();
 				Globe globe = wwd.getModel().getGlobe();
 				Position pos =Position.fromDegrees(location.getLatitude(),
 		 				location.getLongitude(),
 		 				globe.getElevation(Angle.fromDegrees(location.getLatitude()),Angle.fromDegrees(location.getLongitude())));

 				if(firstLoc){
 					firstLoc=false;
 					view.animateTo(pos,Constants.DEFAULT_GPS_DISTANCE_KM,view.getHeading(),view.getTilt(),wwd);
 				}
 				addMarker(pos);
 				
 				double lat = location.getLatitude();
            	int deglat = (int)lat;
            	if(lat<0){
                	lat=(lat-deglat)*(-60);
            	}else{
                	lat=(lat-deglat)*60;
            	}
            	int minlat = (int)lat;
            	lat=(lat-minlat)*60;
            	int seclat= (int)lat;            	
            	
            	double lon =  location.getLongitude();
            	int deglon = (int)lon;
            	if(lon<0){
                	lon=(lon-deglon)*(-60);
            	}else{
                	lon=(lon-deglon)*60;
            	}
            	int minlon = (int)lon;
            	lon=(lon-minlon)*60;
            	int seclon= (int)lon;

 				TextView latTextView = (TextView) findViewById(R.id.poslatvalue);
 				latTextView.setText(""+deglat+"° "+ minlat+"\' "+seclat+"\"");
 		        TextView lonTextView = (TextView) findViewById(R.id.poslonvalue);
 		        lonTextView.setText(""+deglon+"° "+ minlon+"\' "+seclon+"\"");
 		        
 		        
 		        if(null!=gpsSettings&&gpsSettings.isAdded()){
 		        	gpsSettings.dismiss();
 		        }
 		        resetGPSTimer();
 			}
 		
 		}

 		public void onStatusChanged(String provider, int status, Bundle extras) {
 			// do nothing
 		}

 		public void onProviderEnabled(String provider) {
 			//Search the position if button it was clicked
 			gpsIsOn=true;
 			if(null!=gpsSettings&&gpsSettings.isAdded()){
 				gpsSettings.dismiss();
 			}
 		}

 		public void onProviderDisabled(String provider) {
 			gpsIsOn=false;
 			if(null!=gpsSettings&&gpsSettings.isAdded()){
 				gpsSettings.dismiss();
 			}
 			gpsSettings = new GpsSettingsDialog(getString(R.string.turn_on_gps),gpsListener,false);
 			gpsSettings.show(getSupportFragmentManager(), "GPS settings");
 			
 		}
 	};
 	
 	// Set View at last know location
 	protected void setLookAtLastLocation(){
 		Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(null!=lastLocation){
    		
    		BasicView view = (BasicView) wwd.getView();
				Globe globe = wwd.getModel().getGlobe();
			Position pos = new Position(Angle.fromDegrees(lastLocation.getLatitude()),
					Angle.fromDegrees(lastLocation.getLongitude()),
     				globe.getElevation(Angle.fromDegrees(lastLocation.getLatitude()),Angle.fromDegrees(lastLocation.getLongitude())));
    		view.setLookAtPosition(pos);
    		addMarker(pos);
    		view.setRange(Constants.LAST_LOCATION_VIEW_DISTANCE_KM);
    	}
 	}
 	
 	// Move view at last know location
 	protected void moveAtLastLocation(){
 		Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(null!=lastLocation){
    		
    		addMarker(Position.fromDegrees(lastLocation.getLatitude(), lastLocation.getLongitude()));
    		
    		BasicView view = (BasicView) wwd.getView();
			view.animateTo(Position.fromDegrees(lastLocation.getLatitude(), lastLocation.getLongitude()), Constants.DEFAULT_GPS_DISTANCE_KM, view.getHeading(), view.getTilt(), wwd);
    	}
 	}

 	
 	public void unzipFile() {
 		final UnzipTask unzipTask = new UnzipTask(mFile,WorldWindActivity.this,WorldWindActivity.this);
		unzipTask.execute();
	}
 	
 	
 	
 	// ---------------- CLEAR CACHE TASK CALLBACKS -------------------------
 	
 	@Override
	public void unzipTileFile() {
 		unzipFile();
	}
 	
 	// ---------------------------------------------------------------------
 	
 	
 	
 	

 	// ---------------- UNZIP TASK CALLBACKS -------------------------------
 	
 	@Override
	public void finishActivity() {
		WorldWindActivity.this.finish();
	}
 	
	@Override
	public void finishUnzip() {
		
		removeLayers();
		
		checkExistinglayers();
		
		
		for(int i = 0;i<Constants.layers.length;i++){
        	layersEnabled[i] = layersExist[i];
        }
		
		if(layersEnabled[0]&&layersEnabled[1]){
			layersEnabled[1]=false;
		}
		
		// Restore preferences
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		for(int i = 0;i<Constants.layers.length;i++){
			if(layersExist[i]){
				editor.putBoolean(Constants.layers[i], layersEnabled[i]);
			}
	    }
		editor.commit();
		
		// Add Layers
		addElevationLayer();
		addLayers();
		
		wwd.redraw();
	}
	

 	// ---------------------------------------------------------------------

	protected void setEnableLayer(String layerName, boolean enable) {
		for(Layer lyr : wwd.getModel().getLayers()){
			if(null!=lyr){
				if(lyr.getName().equals(layerName)){
					lyr.setEnabled(enable);
				}
			}
		}
	}
	
	
	
	protected void removeLayer(String layerName) {
		for(Layer lyr : wwd.getModel().getLayers()){
			if(null!=lyr){
				if(lyr.getName().equals(layerName)){
					wwd.getModel().getLayers().remove(lyr);
				}
			}
		}
	}
	
	
	protected void removeLayers(){
		for(Layer lyr : wwd.getModel().getLayers()){
			if(null!=lyr){
				for(int i=0;i<Constants.layers.length;i++){
					if(lyr.getName().equals(Constants.layers[i])){
						wwd.getModel().getLayers().remove(lyr);
					}
				}
			}
		}
	}
	
	
	private Layer createLayer(String path) {
		File xml = new File(path);
        if (xml.exists())
        {
            Object o = BasicFactory.create(AVKey.LAYER_FACTORY, xml);
            return (Layer) o;
        }
        return null;
    }
	
	
	// Add Elevation layer from file xml in cache folder
    private void addElevationLayer() {
    	if(exaggeration==0){
    		wwd.getModel().getGlobe().setElevationModel(new ZeroElevationModel());
    	}else{
	    	File xml = new File(getExternalCacheDir()+"/"+Constants.URLWORLD+Constants.ELEVATION_NAME+"/",Constants.ELEVATION_NAME+".xml");
			if(xml.exists()){
				ElevationModel em = WorldWindUtils.getElevationModel(xml);
				em.setNetworkRetrievalEnabled(false);
				wwd.getModel().getGlobe().setElevationModel(em);
				wwd.getSceneController().setVerticalExaggeration(this.exaggeration);
			}else{
				wwd.getModel().getGlobe().setElevationModel(new ZeroElevationModel());
			}
    	}
	}
    
    // Set vertical exaggeration and elevation layer if need
    private void setVerticalExaggeration(int exaggeration) {
    	this.exaggeration=exaggeration;
    	if(this.exaggeration==0){
    		wwd.getModel().getGlobe().setElevationModel(new ZeroElevationModel());
    	}else{
	    	File xml = new File(getExternalCacheDir()+"/"+Constants.URLWORLD+Constants.ELEVATION_NAME+"/",Constants.ELEVATION_NAME+".xml");
			if(xml.exists()){
				if(wwd.getModel().getGlobe().getElevationModel() instanceof ZeroElevationModel){
					ElevationModel em = WorldWindUtils.getElevationModel(xml);
					em.setNetworkRetrievalEnabled(false);
					wwd.getModel().getGlobe().setElevationModel(em);
				}else{
					wwd.getSceneController().setVerticalExaggeration(this.exaggeration);
				}
				
			}else{
				wwd.getModel().getGlobe().setElevationModel(new ZeroElevationModel());
			}
    	}
	}
    
    
    // Add all layers except elevation from files xml in cache folder
    private void addLayers() {
    	
    	for(int i = 0;i<Constants.layers.length;i++){
    		File xml = new File(getExternalCacheDir()+"/"+Constants.URLWORLD+Constants.layers[i]+"/",Constants.layers[i]+".xml");
    		if(xml.exists()){
    			Layer lyr=createLayer(xml.getAbsolutePath());
				if(null!=lyr){
	    			lyr.setName(Constants.layers[i]);
	    			lyr.setNetworkRetrievalEnabled(false);
	    			if(!layersEnabled[i]){
						lyr.setEnabled(false);
					}
					wwd.getModel().getLayers().addIfAbsent(lyr);
				}
    		}else{
    			removeLayer(Constants.layers[i]);
    		}
    	}
		
	}
    
    

    // Check if files xml to define layers are present or not.
    private void checkExistinglayers() {
    	
    	for(int i = 0;i<Constants.layers.length;i++){
    		File xml = new File(getExternalCacheDir()+"/"+Constants.URLWORLD+Constants.layers[i]+"/",Constants.layers[i]+".xml");
    		if(xml.exists()){
    			layersExist[i]=true;
    		}else{
    			layersExist[i]=false;
    			layersEnabled[i]=false;
    		}
    	}
    	
	}
    
    // Add marker in view at position
    private void addMarker(Position pos){
	    
    	GPSMarker c = new GPSMarker(Position.fromDegrees(pos.latitude.degrees, pos.longitude.degrees,0),Constants.MARKER_SIDE,Constants.MARKER_HEIGHT);
	    c.setAltitudeMode(AVKey.RELATIVE_TO_GROUND);
	    //c.setGPSMarkerType(AVKey.LINEAR);
	    ShapeAttributes attrs = new BasicShapeAttributes();
	    attrs.setOutlineColor(Constants.MARKER_COLOR);
	    c.setAttributes(attrs);
    	
    	if(null!=markerLayer){
    		this.wwd.getModel().getLayers().remove(markerLayer);
    		markerLayer.clearList();
    		markerLayer=null;
    	}
    
    	markerLayer = new RenderableLayer();
    	markerLayer.addRenderable(c);

    	this.wwd.getModel().getLayers().add(markerLayer);
    }
	
	
	
	//Callbacks from settings dialog
	SettingsDialogFragment.OnSettingsChangedListener settingsListener = new OnSettingsChangedListener() {

		@Override
		public void onSettingChange() {
			// Change preferences
			SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
			 
			for(int i = 0;i<Constants.layers.length;i++){
				if(layersExist[i]){
					editor.putBoolean(Constants.layers[i], layersEnabled[i]);
				}
		    }
			editor.putInt(Constants.PROP_EXAGGERATION,exaggeration);
			editor.putBoolean(Constants.PROP_GPS_UPDATE,gpsUpdate);
			editor.putInt(Constants.PROP_GPS_SECONDS,gpsSeconds);
			editor.putInt(Constants.PROP_GPS_METERS,gpsMeters);
			 
			editor.commit();
		}

		@Override
		public void onLayerChange(int index, boolean checked) {
			setEnableLayer(Constants.layers[index],checked);
			layersEnabled[index]=checked;
		}

		@Override
		public void onExaggerationChange(int exaggeration) {
			setVerticalExaggeration(exaggeration);
		}

		@Override
		public void onGpsChange(int gpsS, int gpsM) {
			gpsMeters=gpsM;
			gpsSeconds=gpsS;
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsSeconds*1000,gpsMeters, locationListener);
		}

		@Override
		public void onGpsEnableChange(boolean enable) {
			gpsUpdate = enable;
			if(gpsUpdate){
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsSeconds*1000,gpsMeters, locationListener);
				startGPSTimer();
			}else{
				lm.removeUpdates(locationListener);
				timerGPS.cancel();
			}
		}
	};


	GpsSettingsDialog.OnGpsCloseListener gpsListener = new GpsSettingsDialog.OnGpsCloseListener() {
		
		@Override
		public void onCloseGpsSettings(boolean reset) {
			//no gpsUpdate
			if(!reset){
				if(!gpsIsOn){
					gpsUpdate=false;
					lm.removeUpdates(locationListener);
					if(null!=timerGPS){
						timerGPS.cancel();
					}
					if(null!=settings&&settings.isAdded()){
						settings.setGpsUpdate(gpsUpdate);
					}
				}
			}else{
				startGPSTimer();
			}
		}

		@Override
		public void onNoGpsUpdate() {
			gpsUpdate=false;
			lm.removeUpdates(locationListener);
			if(null!=timerGPS){
				timerGPS.cancel();
			}
		}
	};


	@Override
	public void onGpsStatusChanged(int event) {
		if(event==GpsStatus.GPS_EVENT_STARTED){
			gpsIsOn=true;
			gpsUpdate=true;
			startGPSTimer();
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsSeconds*1000,gpsMeters, locationListener);
			if(null!=gpsSettings&&gpsSettings.isAdded()){
 				gpsSettings.dismiss();
 			}
		}
	}
	

	public void startGPSTimer(){
		TimerTask taskGPS= new TimerTask() {  
			  
			@Override  
			public void run() {
			    if(null!=gpsSettings&&gpsSettings.isAdded()){
			 		gpsSettings.dismiss();
			 	}
			    gpsSettings = new GpsSettingsDialog(getString(R.string.reset_gps),gpsListener,true);
			 	gpsSettings.show(getSupportFragmentManager(), "GPS settings");
			}  
		};
		if(null!=timerGPS){
			timerGPS.cancel();
		}
		timerGPS =new Timer();
		timerGPS.scheduleAtFixedRate(taskGPS, Constants.GPS_RESET, Long.MAX_VALUE);
	}
	
	public void resetGPSTimer(){
		if(null!=timerGPS){
			timerGPS.cancel();
		}
		startGPSTimer();
	}

	
	
	  
}

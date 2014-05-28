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
package it.trilogis.android.ww.dialogs;


import it.trilogis.android.fao.R;
import it.trilogis.android.fao.R.id;
import it.trilogis.android.fao.R.layout;
import it.trilogis.android.fao.R.string;
import gov.nasa.worldwind.WorldWindowGLSurfaceView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

/**
 * @author Nicola Meneghini
 */
public class SettingsDialogFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener{
	private Switch swtTPC,swtLandsat,swtGreeness,swtRainfall,swtBoundaries,swtGPS;
	private SeekBar exa,sec,met;
	private TextView exatxt,sectxt,mettxt;
	private boolean[] checked=new boolean[]{false,false,false,false,false};
	private boolean[] exist=new boolean[]{false,false,false,false,false};
	private int exaggeration,gpsSeconds,gpsMeters;
	private boolean gpsChanged = false;
	private boolean exaggerationChanged = false;
	private boolean gpsEnable;
	
	
	public interface OnSettingsChangedListener {
		//public abstract void onSettingChange(boolean[] checked, int exa, int gpsS, int gpsM);
		public abstract void onLayerChange(int index,boolean checked);
		public abstract void onExaggerationChange(int exa);
		public abstract void onGpsChange(int gpsS,int gpsM);
		public abstract void onGpsEnableChange(boolean enable);
		public abstract void onSettingChange();
	}
	
	
	protected OnSettingsChangedListener settingsListener = null;
	
	public SettingsDialogFragment(){
		super();
	}
	
	public SettingsDialogFragment(boolean[] layersExist,boolean[] layersChecked,int exaggeration,int gpsSeconds,int gpsMeters,boolean gpsEnable){
		super();
		if(null!=layersExist&&layersExist.length==5){
			exist=layersExist;
		}
		if(null!=layersChecked&&layersChecked.length==5){
			checked=layersChecked.clone();
		}
		this.exaggeration=exaggeration;
		this.gpsSeconds=gpsSeconds;
		this.gpsMeters=gpsMeters;
		this.gpsEnable=gpsEnable;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.settings_layout, null);
		
		
		swtTPC= (Switch) view.findViewById(R.id.swt_tpc);
		swtLandsat= (Switch) view.findViewById(R.id.swt_landsat);
		swtGreeness= (Switch) view.findViewById(R.id.swt_greenness);
		swtRainfall= (Switch) view.findViewById(R.id.swt_rainfall);
		swtBoundaries= (Switch) view.findViewById(R.id.swt_boundaries);
		swtGPS = (Switch) view.findViewById(R.id.swt_gps);
		initSwitch();
		
		exa = (SeekBar) view.findViewById(R.id.bar_exaggeration);
		exa.setProgress(exaggeration);
		exatxt = (TextView) view.findViewById(R.id.txt_exaggeration);
		exatxt.setText(getString(R.string.exaggeration)+" "+exaggeration);
		
		exa.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				exaggeration=progress;
				exatxt.setText(getString(R.string.exaggeration)+" "+exaggeration);
				//settingsListener.onExaggerationChange(exaggeration);
				exaggerationChanged = true;
			}
		});
		
		sec = (SeekBar) view.findViewById(R.id.bar_gps_seconds);
		sec.setProgress(gpsSeconds-1);
		sec.setEnabled(gpsEnable);
		sectxt = (TextView) view.findViewById(R.id.txt_gps_seconds);
		sectxt.setText(getString(R.string.gps_seconds)+" "+gpsSeconds);
	
		
		sec.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				gpsChanged=true;
				gpsSeconds= progress+1;
				sectxt.setText(getString(R.string.gps_seconds)+" "+gpsSeconds);
			}
		});
		
		
		met = (SeekBar) view.findViewById(R.id.bar_gps_meters);
		met.setProgress(gpsMeters-10);
		met.setEnabled(gpsEnable);
		mettxt = (TextView) view.findViewById(R.id.txt_gps_meters);
		mettxt.setText(getString(R.string.gps_meters)+" "+gpsMeters);
		
		met.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				gpsChanged=true;
				gpsMeters= progress+10;
				mettxt.setText(getString(R.string.gps_meters)+" "+gpsMeters);
			}
		});
		
		
		
		
		builder.setView(view).setPositiveButton(getString(android.R.string.ok),new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(gpsChanged){
					settingsListener.onGpsChange(gpsSeconds, gpsMeters);
				}
				if(exaggerationChanged){
					settingsListener.onExaggerationChange(exaggeration);
				}
				settingsListener.onSettingChange();
				//getSettingsValues();
				//settingsListener.onSettingChange(checked,exaggeration,gpsSeconds,gpsMeters);
			}
			
		}).setTitle("Settings");

		return builder.create();
	}

	protected void getSettingsValues() {
		checked[0]=swtTPC.isChecked();
		checked[1]=swtLandsat.isChecked();
		checked[2]=swtGreeness.isChecked();
		checked[3]=swtRainfall.isChecked();
		checked[4]=swtBoundaries.isChecked();
		exaggeration=exa.getProgress();
		gpsSeconds=sec.getProgress()+1;
		gpsMeters=sec.getProgress()+10;
		
		//Get gpsSec and gpsMeters
	}

	private void initSwitch() {

		if(exist[0]){
			swtTPC.setChecked(checked[0]);
			swtTPC.setOnCheckedChangeListener(this);
		}
		swtTPC.setEnabled(exist[0]);
		
		if(exist[1]){
			swtLandsat.setChecked(checked[1]);
			swtLandsat.setOnCheckedChangeListener(this);
		}
		swtLandsat.setEnabled(exist[1]);
		
		if(exist[2]){
			swtGreeness.setChecked(checked[2]);
			swtGreeness.setOnCheckedChangeListener(this);
		}
		swtGreeness.setEnabled(exist[2]);
		
		if(exist[3]){
			swtRainfall.setChecked(checked[3]);
			swtRainfall.setOnCheckedChangeListener(this);
		}
		swtRainfall.setEnabled(exist[3]);
		
		if(exist[4]){
			swtBoundaries.setChecked(checked[4]);
			swtBoundaries.setOnCheckedChangeListener(this);
		}
		swtBoundaries.setEnabled(exist[4]);
		
		swtGPS.setChecked(gpsEnable);
		swtGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				gpsEnable=isChecked;
				settingsListener.onGpsEnableChange(gpsEnable);
				sec.setEnabled(gpsEnable);
				met.setEnabled(gpsEnable);
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Switch swt = (Switch) buttonView;
		swt.setChecked(isChecked);
		
		switch (swt.getId()){
		case R.id.swt_tpc:
			if(isChecked){
				if(swtLandsat.isEnabled()){
					if(swtLandsat.isChecked()){
						swtLandsat.setChecked(false);
						checked[1]=false;
						settingsListener.onLayerChange(1, false);
					}
				}
				checked[0]=true;
				settingsListener.onLayerChange(0, true);
			}else{
				checked[0]=false;
				settingsListener.onLayerChange(0, false);
			}
			break;
		case R.id.swt_landsat:
			if(isChecked){
				if(swtTPC.isEnabled()){
					if(swtTPC.isChecked()){
						swtTPC.setChecked(false);
						checked[0]=false;
						settingsListener.onLayerChange(0, false);
					}
				}
				checked[1]=true;
				settingsListener.onLayerChange(1, true);
			}else{
				checked[1]=false;
				settingsListener.onLayerChange(1, false);
			}
			break;
		case R.id.swt_greenness:
			checked[2]=isChecked;
			settingsListener.onLayerChange(2, isChecked);
			break;
			
		case R.id.swt_rainfall:
			checked[3]=isChecked;
			settingsListener.onLayerChange(3, isChecked);
			break;
			
		case R.id.swt_boundaries:
			checked[4]=isChecked;
			settingsListener.onLayerChange(4, isChecked);
			break;
			
		}
	}
	
	
	public void setOnSettingChangeListener(OnSettingsChangedListener listener){
		this.settingsListener=listener;
	}
	
	public void setGpsUpdate(boolean gpsUpdate){
		this.gpsEnable=gpsUpdate;
		if(this.isAdded()&&null!=swtGPS){
			swtGPS.setChecked(gpsEnable);
		}
	}
	
}

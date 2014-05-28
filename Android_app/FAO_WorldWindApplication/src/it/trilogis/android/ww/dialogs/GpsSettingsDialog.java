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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * @author Nicola Meneghini
 */
public class GpsSettingsDialog  extends DialogFragment {
	
	private String message;
	private boolean reset=false;
	
	public interface OnGpsCloseListener {
		public abstract void onCloseGpsSettings(boolean reset);
		public abstract void onNoGpsUpdate();
	}
	
	protected OnGpsCloseListener gpsListener = null;
	
	public GpsSettingsDialog(){
		
	};
	
	public GpsSettingsDialog(String mex,OnGpsCloseListener gpsListener,boolean reset){
		this.message=mex;
		this.gpsListener=gpsListener;
		this.reset=reset;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
			builder.setMessage(message);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.settings,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Call gps intent
					startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
				}
			});	
			builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//callback
					if(null!=gpsListener)
						gpsListener.onCloseGpsSettings(reset);
				}
			});
			if(reset){
				builder.setNeutralButton(R.string.no_gps_update, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						gpsListener.onNoGpsUpdate();
					}
				});
			}
			
			return builder.create();
	}
	
	public boolean getReset(){
		return reset;
	}

}

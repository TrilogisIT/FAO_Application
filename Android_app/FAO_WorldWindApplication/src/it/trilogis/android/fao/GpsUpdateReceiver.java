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

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * @author Nicola Meneghini
 */
public class GpsUpdateReceiver extends BroadcastReceiver{
      
    @Override
    public void onReceive(Context context, Intent intent)
    {
    	final Context conte=context;
    		AlertDialog.Builder builder=new AlertDialog.Builder(context);
			builder.setMessage(R.string.turn_on_gps);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.settings,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					//Call gps intent
					conte.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				}
			});	
			
			builder.create();
			builder.show();
			
    }

}

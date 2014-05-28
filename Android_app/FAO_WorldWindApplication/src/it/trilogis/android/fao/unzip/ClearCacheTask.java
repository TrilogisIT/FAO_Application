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
package it.trilogis.android.fao.unzip;

import it.trilogis.android.fao.R;
import it.trilogis.android.fao.utils.FileUtils;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * @author Nicola Meneghini
 */
public class ClearCacheTask extends AsyncTask<String, Integer, String>{

	private ProgressDialog progress;
	private Context context;
	private boolean unzip =false;
	
	private Callbacks mCallbacks = sDummyCallbacks;

	
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void unzipTileFile();
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {

		@Override
		public void unzipTileFile(){
			// do nothing
		}

	};
	
	
	public ClearCacheTask(boolean unzip, Context context, Callbacks callback){
		this.unzip = unzip;
		this.context = context;
		this.mCallbacks = callback;
	}
	
	
	@Override
    protected void onPreExecute() {
        super.onPreExecute();

		// instantiate progressDialog
        progress = new ProgressDialog(context);
    	progress.setCancelable(false);
    	progress.setMessage(context.getString(R.string.clearing_cache_message));
    	progress.show();
    }

	@Override
	protected String doInBackground(String... params) {
		try{
			File directory = context.getExternalCacheDir();
			FileUtils.deleteDirectory(directory);
		}catch(Exception e){
			
		}
		return null;
	}
	
	@Override
    protected void onPostExecute(String result) {
		progress.dismiss();
		if(unzip){
			//call unzipTask
			mCallbacks.unzipTileFile();
		}
	}
}
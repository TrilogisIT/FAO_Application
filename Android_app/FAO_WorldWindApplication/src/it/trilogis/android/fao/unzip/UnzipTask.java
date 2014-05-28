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
import it.trilogis.android.fao.constants.Constants;
import it.trilogis.android.fao.utils.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * @author Nicola Meneghini
 */
public class UnzipTask extends AsyncTask<String, Integer, String> {

	private ProgressDialog mProgressDialog;
    private Context context;
    private File mFile;
    
    
    private Callbacks mCallbacks = sDummyCallbacks;

	
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void finishActivity();

		public void finishUnzip();
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {

		@Override
		public void finishActivity(){
			// do nothing
		}

		@Override
		public void finishUnzip() {
			// do nothing
		}
	};

    public UnzipTask(File file,Context context,Callbacks c) {
        this.mFile=file;
        this.context = context;
        mCallbacks = c;
    }

    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

		// instantiate progressDialog
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getString(R.string.unzip_message));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    
    
    @Override
    protected String doInBackground(String... sUrl) {
    	try {
    		ZipInputStream zin = null;
    		ZipFile z=null;
        	File w = new File(context.getExternalCacheDir(), Constants.URLWORLD);
        	if( !w.exists() )
        		w.mkdirs();
	    
        	File t = new File(w, Constants.URLTILES);
        	if( !t.exists() )
        		t.mkdirs();

        	try{
        		zin = new ZipInputStream(new FileInputStream(mFile));
                z = new ZipFile(mFile);
        		int count =0;
        		int progress=0;
        		int progressAt=z.size()/100;
                ZipEntry entry;
                String name, dir;
                while ((entry = zin.getNextEntry()) != null){	
                	count++;
                	if(count==progressAt){
                		progress++;
                		count=0;
                		publishProgress(progress);
                	}
	                name = entry.getName();
	                if( entry.isDirectory() ){
	                	FileUtils.makedirectory(w,name);
	                    continue;
	                }else{
	                	if(name.contains("\\")){
	                    	name = name.replace('\\', '/');
	                    	String nameFolder = name.substring(0, name.lastIndexOf('/')+1);
	                    	String realNameFile = name.substring(name.lastIndexOf('/')+1, name.length());
	                    	if( nameFolder != null ){
	                    		FileUtils.makedirectory(w,nameFolder);
	                    	}
	                    	FileUtils.extractFile(zin, new File(w+"/"+nameFolder), realNameFile);
	                    	continue;
	                    	
	                	}
	                }
	                dir = FileUtils.directoryPart(name);
	                if( dir != null )
	                    FileUtils.makedirectory(w,dir);

	                FileUtils.extractFile(zin, w, name);
	                    
                }
                z.close();
                zin.close();
        	}catch(Exception e){
        		return "Error";
        	} finally {
        		if(zin!=null){
        			zin.close();
        		}
        		if(z!=null){
        			z.close();
        		}
            }
        }catch(Exception e){
        	return "Error";
    	}
        return null;
    }
    

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mProgressDialog.dismiss();
        if (result != null){
        	Toast.makeText(context,context.getString(R.string.unzip_error)+result, Toast.LENGTH_LONG).show();
        	mCallbacks.finishActivity();
        }else{

        	try{
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(context);
            	builder.setCancelable(false);
            	builder.setNegativeButton(R.string.no, null);
            	builder.setPositiveButton(context.getString(R.string.yes)+context.getString(R.string.recommended),new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DeleteZipTask d = new DeleteZipTask();
			    		d.execute();
					}
				});
            	builder.setTitle(R.string.delete_zip_title);
            	builder.setMessage(R.string.delete_zip_text);
            	builder.show();
                
        	}catch(Exception e){
            	
            }
        	Toast.makeText(context,context.getString(R.string.unzip_ok), Toast.LENGTH_SHORT).show();
            mCallbacks.finishUnzip();
        }

    }
    
    

    
    private class DeleteZipTask extends AsyncTask<String, Integer, String>{

    	private ProgressDialog progress;
    	
    	
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();

    		// instantiate progressDialog
            progress = new ProgressDialog(context);
        	progress.setCancelable(false);
        	progress.setMessage(context.getString(R.string.deleting_zip));
        	progress.show();
        }

		@Override
		protected String doInBackground(String... params) {
			try{
				if( mFile.exists() ){
                	mFile.delete();
                }
			}catch(Exception e){
				
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(String result) {
			progress.dismiss();
		}
    	
    }
    
}
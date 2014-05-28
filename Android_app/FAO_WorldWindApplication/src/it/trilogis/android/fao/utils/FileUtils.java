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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

/**
 * @author Nicola Meneghini
 */
public class FileUtils {

	private static final int BUFFER_SIZE = 4096;


    /**
     * Delete a directory and all files that it contains
     * 
     * @param directory
     * 		directory
     */
    public static void deleteDirectory(File directory){
    	try{
	        for (File file : directory.listFiles()) {
	            if (file.isFile())
	                file.delete();
	            else
	                deleteDirectory(file);
	        }
	        directory.delete();
    	}catch(Exception e){

    	}
    }

    
    
    /**
     * Extract a file zip in a directory
     * 
     * @param inputStream
     * 			Stream of zip file
     * @param outputDirectory
     * 			Directory where the zip will be unzipped
     * @param fileName
     * 			name of the file
     * @throws IOException
     */
    public static void extractFile(ZipInputStream inputStream, File outputDirectory, String fileName) throws IOException{
    	byte[] buffer = new byte[BUFFER_SIZE];
    	BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(outputDirectory,fileName)));
    	int count = -1;
    	//read input and write on output stream
    	while ((count = inputStream.read(buffer)) != -1){
    		outputStream.write(buffer, 0, count);
    	}
    	
    	outputStream.close();
    }

    
    
    /**
     * Create a directory
     * 
     * @param outputDirectory
     * 			Directory of output
     * @param directoryName
     * 			name of the directory 
     */
    public static void makedirectory(File outputDirectory,String directoryName){
    	File directory = new File(outputDirectory, directoryName);
    	if( !directory.exists() ){
    		directory.mkdirs();
    	}
    }
    
    
    /**
     * Return the path of last directory that contain the file 
     * 
     * @param filePath
     * 			Path of file
     * 			
     * @return String the path of directory that contains the file
     */
    public static String directoryPart(String filePath){
    	int s = filePath.lastIndexOf( File.separatorChar );
    	return s == -1 ? null : filePath.substring( 0, s );
    }
    
    

    /**
     * Calculate the size of folder
     * 
     * @param directory
     * @return long Number of bytes
     */
    public static long folderSize(File directory) {
    	try{
	        long length = 0;
	        for (File file : directory.listFiles()) {
	            if (file.isFile())
	                length += file.length();
	            else
	                length += folderSize(file);
	        }
	        return length;
    	}catch(Exception e){
    		return 0;
    	}
    }
    
    
}

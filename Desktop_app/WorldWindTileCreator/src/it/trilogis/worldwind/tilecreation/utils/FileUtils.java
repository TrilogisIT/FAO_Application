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
package it.trilogis.worldwind.tilecreation.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author nmeneghini
 * @version $Id: FileUtils.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class FileUtils {
	
    private static final int BUFFER_SIZE = 2048;

    
    /**
     * Create a zip file from a directory
     * 
     * @param cacheDir
     * 			the directory ready to zip
     * @param zipFileName
     * 			name of file zip create
     * @return boolean the result of zip operation
     */
    public static boolean createZipFile(File cacheDir, String zipFileName) {
        File zipFile = new File(cacheDir.getParentFile().getAbsolutePath() + File.separator + zipFileName + ".zip");
        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            processDirFilesToZip(cacheDir, cacheDir.getAbsolutePath() + File.separator, zos);
            // close the ZipOutputStream
            zos.close();
            return true;
        } catch (IOException ioe) {
            System.out.println("Error creating zip file: " + ioe);
        }
        return false;
    }
    	  
    
    /**
     * This method implement zip operation.
     * 
     * @param directory 
     * @param baseName
     * @param zipOutputStream
     * @throws IOException
     */
    private static void processDirFilesToZip(File directory, String baseName, ZipOutputStream zipOutputStream) throws IOException {
        if (null != directory && directory.isDirectory() && directory.canRead() && null != zipOutputStream) {

            // get content
            File[] children = directory.listFiles();
            // empty
            if (null == children || children.length < 1) {
                return;
            }
            // create byte buffer
            byte[] buffer = new byte[BUFFER_SIZE];
            // place holder for name
            String fileName = null;
            // place holder for zip entry
            ZipEntry zipentry = null;
            // iterate over files
            for (File child : children) {
                if (null != child && child.isDirectory()) {
                    // delegate
                	
                	processDirToZip(child, baseName, zipOutputStream);
                    
                } else {
                    // save name
                    fileName = child.getName(); // getAbsolutePath().substring(baseName.length());
                    
                    // create object of FileInputStream for source file
                    FileInputStream fin = new FileInputStream(child);
                    // create zip entry
                    zipentry = new ZipEntry(fileName);
                    // add entry in zip
                    zipOutputStream.putNextEntry(zipentry);
                    // write content of file
                    int count;
                    while ((count = fin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        zipOutputStream.write(buffer, 0, count);
                    }
                    // close entry
                    zipOutputStream.closeEntry();
                    // close the InputStream
                    fin.close();
                }
            }
        }
    }

    /**
     * @param directory
     * @param baseName
     * @param zipOutputStream
     * @throws IOException
     */
    private static void processDirToZip(File directory, String baseName, ZipOutputStream zipOutputStream) throws IOException {
        if (null != directory && directory.isDirectory() && directory.canRead() && null != zipOutputStream) {

            // get content
            File[] children = directory.listFiles();
            // empty
            if (null == children || children.length < 1) {
                return;
            }
            // create byte buffer
            byte[] buffer = new byte[BUFFER_SIZE];
            // place holder for name
            String fileName = null;
            // place holder for zip entry
            ZipEntry zipentry = null;
            // iterate over files
            for (File child : children) {
                if (null != child && child.isDirectory()) {
                    // delegate
                	processDirToZip(child, baseName, zipOutputStream);
                } else {
                    // save name
                    fileName = child.getAbsolutePath().substring(baseName.length());
                    // create object of FileInputStream for source file
                    FileInputStream fin = new FileInputStream(child);
                    // create zip entry
                    zipentry = new ZipEntry(fileName);
                    // add entry in zip
                    zipOutputStream.putNextEntry(zipentry);
                    // write content of file
                    int count;
                    while ((count = fin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        zipOutputStream.write(buffer, 0, count);
                    }
                    // close entry
                    zipOutputStream.closeEntry();
                    // close the InputStream
                    fin.close();
                }
            }
        }
    }

    
    /**
     * Delete a directory and all files that it contains
     * 
     * @param path
     * 		path of directory
     * @return boolean result of operation
     */
    public static boolean deleteNonEmptyDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteNonEmptyDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
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
    
}

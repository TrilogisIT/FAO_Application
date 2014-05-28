/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.formats.dds;

import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.formats.dds.BasicColorBlockExtractor;
import gov.nasa.worldwind.formats.dds.ColorBlockExtractor;
import gov.nasa.worldwind.formats.dds.DXTCompressionAttributes;
import gov.nasa.worldwind.formats.dds.DXTCompressor;
import gov.nasa.worldwind.formats.pkm.JavaETC1;

import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * @author nicastel
 * @author dcollins
 * @version $Id: DXT1Compressor.java 733 2012-09-02 17:15:09Z dcollins $
 */
public class ETC1Compressor implements DXTCompressor
{
    public ETC1Compressor()
    {
    }

    public int getDXTFormat()
    {
        return ETCConstants.D3DFMT_ETC1;
    }

    public int getCompressedSize(java.awt.image.BufferedImage image, DXTCompressionAttributes attributes)
    {
        if (image == null)
        {
            String message = Logging.getMessage("nullValue.ImageIsNull");
            throw new IllegalArgumentException(message);
        }
        if (attributes == null)
        {
            String message = Logging.getMessage("nullValue.AttributesIsNull");
            throw new IllegalArgumentException(message);
        }

        // TODO: comment, provide documentation reference

        int width = Math.max(image.getWidth(), 4);
        int height = Math.max(image.getHeight(), 4);

        return (width * height) / 2;
    }
    
    public void compressImage(java.awt.image.BufferedImage image, DXTCompressionAttributes attributes,
        java.nio.ByteBuffer buffer)
    {
        if (image == null)
        {
            String message = Logging.getMessage("nullValue.ImageIsNull");
            throw new IllegalArgumentException(message);
        }
        if (attributes == null)
        {
            String message = Logging.getMessage("nullValue.AttributesIsNull");
            throw new IllegalArgumentException(message);
        }
        if (buffer == null)
        {
            String message = Logging.getMessage("nullValue.BufferNull");
            throw new IllegalArgumentException(message);
        }
        
        // TODO
	   	int width = Math.max(image.getWidth(), 1);
	    int height = Math.max(image.getHeight(), 1);
        
        int encodedImageSize = JavaETC1.getEncodedDataSize(width, height);
        System.out.println("encodedImageSize : "+encodedImageSize);
        
        // TODO
//    	ByteBuffer bufferIn = ByteBuffer.allocateDirect(2 * image.getHeight()).order(ByteOrder.nativeOrder());
//    	image.copyPixelsToBuffer(bufferIn);
    	
//    	ByteBuffer bufferIn = ByteBuffer.allocateDirect(3 * image.getHeight()).order(ByteOrder.nativeOrder());
        
        
        
//        private DirectBufferedImage( Type type/*, byte[] buffer*/, ColorModel model, WritableRaster raster, boolean rasterPremultiplied )
//        {
//            super( model, raster, rasterPremultiplied, null );
//            
//            this.directType = type;
//            
//            this.numBytes = raster.getDataBuffer().getSize();
//            //this.data = buffer;
//        }
        
        
    	byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData(); 
    	
    	
    	
    	ByteBuffer bufferIn = ByteBuffer.allocateDirect(data.length).order(ByteOrder.nativeOrder());
    	
    	for(int i = 0;i<data.length;i=i+3){
    		bufferIn.put(data[i+2]);
    		bufferIn.put(data[i+1]);
    		bufferIn.put(data[i]);
    	}
    	
//    	bufferIn.put(data);
        

//    	byte[] dat = bufferIn.array();
//
//    	buffer.reset();
    	
    	
    	
//    	int i = 0;
//        while (data.length> i) {
//        	bufferIn.putShort(data[i]);
//        	i++;
//        }
        
//    	short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData(); 
//    	int i = 0;
////    	int numByte = encodedImageSize;
//    	ByteBuffer bufferIn = ByteBuffer.allocate(2*data.length).order(ByteOrder.nativeOrder());
//    	while (data.length> i) {
//    		bufferIn.putShort(data[i]);
//    	    i++;
//    	}
    	
    	
        // TODO ANDROID
//    	ByteBuffer bufferIn = ByteBuffer.allocateDirect(
//    			image.getRowBytes() * image.getHeight()).order(
//				ByteOrder.nativeOrder());
//    	image.copyPixelsToBuffer(bufferIn);
    	
    	
    	
    	
    	bufferIn.rewind();       
    	
    	ByteBuffer bufferOut = ByteBuffer.allocateDirect(encodedImageSize);
    	
    	JavaETC1.encodeImage(bufferIn, image.getWidth(), image.getHeight(), 3, 3 * width, bufferOut);
        
        bufferOut.rewind();   
        
        buffer.put(bufferOut);        
    }

    protected ColorBlockExtractor getColorBlockExtractor(java.awt.image.BufferedImage image)
    {
        return new BasicColorBlockExtractor(image);
    }
}

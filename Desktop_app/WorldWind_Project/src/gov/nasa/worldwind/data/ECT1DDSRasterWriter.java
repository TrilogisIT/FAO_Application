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
 package gov.nasa.worldwind.data;

import gov.nasa.worldwind.formats.dds.ETC1DDSCompressor;
import gov.nasa.worldwind.util.WWIO;

import java.io.File;
import java.io.IOException;

/**
 * @author nmeneghini
 * @version $Id: ECT1DDSRasterWriter.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class ECT1DDSRasterWriter extends AbstractDataRasterWriter
{
    protected static final String[] ddsMimeTypes = {"image/dds"};
    protected static final String[] ddsSuffixes = {"dds"};

    public ECT1DDSRasterWriter()
    {
    	super(javax.imageio.ImageIO.getWriterMIMETypes(), getImageIOWriterSuffixes());
    }

    protected boolean doCanWrite(DataRaster raster, String formatSuffix, File file)
    {
        return (raster != null) && (raster instanceof BufferedImageRaster);
    }

    protected void doWrite(DataRaster raster, String formatSuffix, File file) throws IOException
    {
        BufferedImageRaster bufferedImageRaster = (BufferedImageRaster) raster;
        java.awt.image.BufferedImage image = bufferedImageRaster.getBufferedImage();
        
        java.nio.ByteBuffer byteBuffer = ETC1DDSCompressor.compressImage(image);
        
        // Do not force changes to the underlying filesystem. This drastically improves write performance.
        boolean forceFilesystemWrite = false;
        
        file = new File(file.getAbsolutePath().replace(".png", "") + ".dds");
        
        
        WWIO.saveBuffer(byteBuffer, file, forceFilesystemWrite);
    }
    
    private static String[] getImageIOWriterSuffixes() {
        java.util.Iterator<javax.imageio.spi.ImageWriterSpi> iter;
        try {
            iter = javax.imageio.spi.IIORegistry.getDefaultInstance().getServiceProviders(javax.imageio.spi.ImageWriterSpi.class, true);
        } catch (Exception e) {
            return new String[0];
        }

        java.util.Set<String> set = new java.util.HashSet<String>();
        while (iter.hasNext()) {
            javax.imageio.spi.ImageWriterSpi spi = iter.next();
            String[] names = spi.getFileSuffixes();
            set.addAll(java.util.Arrays.asList(names));
        }

        String[] array = new String[set.size()];
        set.toArray(array);
        return array;
    }
    
    
    
}

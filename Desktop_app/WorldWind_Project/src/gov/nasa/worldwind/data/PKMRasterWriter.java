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

import gov.nasa.worldwind.formats.pkm.JavaETC1;
import gov.nasa.worldwind.formats.pkm.JavaETC1Util;
import gov.nasa.worldwind.formats.pkm.PNGDecoder;
import gov.nasa.worldwind.formats.pkm.JavaETC1Util.ETC1Texture;
import gov.nasa.worldwind.formats.pkm.PNGDecoder.Format;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

/**
 * @author nmeneghini
 * @version $Id: PKMRasterWriter.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class PKMRasterWriter extends AbstractDataRasterWriter {

	private static final String MIMETYPE = "image/pkm";
	private static final String SUFFIX = "pkm";
	
	
    public PKMRasterWriter() {
    	super(javax.imageio.ImageIO.getWriterMIMETypes(), getImageIOWriterSuffixes());
    }
    
    protected boolean doCanWrite(DataRaster raster, String formatSuffix, java.io.File file) {
        return (raster != null) && (raster instanceof BufferedImageRaster);
    }

    protected void doWrite(DataRaster raster, String formatSuffix, java.io.File file) throws java.io.IOException {
        this.writeImage(raster, formatSuffix, file);

    }

    protected void writeImage(DataRaster raster, String formatSuffix, java.io.File file) throws java.io.IOException {
        BufferedImageRaster bufferedImageRaster = (BufferedImageRaster) raster;
        BufferedImage image = bufferedImageRaster.getBufferedImage();
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteArrayOutputStream osalpha = new ByteArrayOutputStream();
        
        ImageIO.write(image, formatSuffix, os);
        
        try {
                InputStream input = new ByteArrayInputStream(os.toByteArray());
                
                PNGDecoder decoder = new PNGDecoder(input);

                ByteBuffer buf = ByteBuffer.allocateDirect(3*decoder.getWidth()*decoder.getHeight());
                decoder.decode(buf, decoder.getWidth()*3, Format.RGB);
                
                
                buf.flip();
                //per NicoLino:
                // se guardi BufferedImage.TYPE_INT_ARGB la descrizione dice che i pixel sono scritti in formato RGBA:
                //----public static final int TYPE_INT_ARGB
                //------Represents an image with 8-bit RGBA color components packed into integer pixels. 6
                //------The image has a DirectColorModel with alpha. The color data in this image is considered not to be premultiplied with alpha. 
                //Qundi mi pare abbastanza facile usare laclasse PNGDecoder2 che ha anche il formato Format.RGBA
                //Per provare ti basta quindi andare a cercare i TODO che ho lasciato io dove ho forzato l'immagine a OPAQUE (prima era forzato a TRANSPARENT).
                //Se funziona anche sui transparent è una figata e basta evitare che scriva i pkm per i layer di overlay (basta mattere un parametro come il TRILOGIS della trasparenza)
                //Se non funziona fanculo....
                //a memoria gli OPAQUE messi sono:  BUFFEREDIMAGERASTER nella funzione doGetSubraster
                //e TRANSPARENT TILEDIMAGE PRODUCER protected DataRaster createDataRaster vedi un po
                
                String filenamepkm = file.getAbsolutePath().replace(".png", "") + ".pkm";
                
                writeToFilePkm(buf, decoder.getWidth(), decoder.getHeight(), filenamepkm);
                

                if(image.getTransparency() == Transparency.TRANSLUCENT){
                	
                	BufferedImage alpha = extractAlphaInline(image);
                	
                    ImageIO.write(alpha, formatSuffix, osalpha);
                    
                    InputStream inputa = new ByteArrayInputStream(osalpha.toByteArray());
                            
                    PNGDecoder decodera = new PNGDecoder(inputa);

                    ByteBuffer bufa = ByteBuffer.allocateDirect(3*decodera.getWidth()*decodera.getHeight());
                    decodera.decode(bufa, decoder.getWidth()*3, Format.RGB);//TODO maybe rgb is wrong since extract alpha creates bgr
                            
                    bufa.flip();
                    
                    String filenamepkmalpha = file.getAbsolutePath().replace(".png", "") + "_alpha.pkm";
                    
                    writeToFilePkm(bufa, decodera.getWidth(), decodera.getHeight(), filenamepkmalpha);
                    
//                    writeAlphaToFile(bufa,decodera.getWidth(), decodera.getHeight(), file.getAbsolutePath());
                	
                }

                
                System.out.println("Written PKM: '"+ file.getParentFile().getParentFile().getName() + "/" + file.getParentFile().getName() + "/" + file.getName() +"'");
            } catch (Exception e) {
                System.out.println("Exception saving '"+ file.getParentFile().getParentFile().getName() + "/" + file.getParentFile().getName() + "/" + file.getName() +"' pkm: " + e.getMessage());
            }finally{
            	if(os!=null){
            		os.close();
            	}
            	if(osalpha!=null){
            		osalpha.close();
            	}
            }
    }

    
    protected static BufferedImage extractAlphaInline(BufferedImage paramBitmap)
    {
      int i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      BufferedImage localimage = new BufferedImage(i, j, BufferedImage.TYPE_INT_RGB);
      for (int k = 0; k < j; k++)
        for (int m = 0; m < i; m++)
        {
        	int colour = paramBitmap.getRGB(m, k);
        	int alpha = (colour>>24) & 0xff;
        	int col = (alpha << 16) | (alpha << 8) | alpha;
        	localimage.setRGB(m, k, col);
        }
      return localimage;
    }
  
    
    private void writeToFilePkm(ByteBuffer buffer, int width, int height, String filePathName) throws FileNotFoundException, IOException {
        ByteBuffer compressedImage;
        final int encodedImageSize = JavaETC1.getEncodedDataSize(width, height);
        compressedImage = ByteBuffer.allocateDirect(encodedImageSize).order(ByteOrder.nativeOrder());
        JavaETC1.encodeImage(buffer, width, height,3, 3 * width, compressedImage);
        ETC1Texture texture = new ETC1Texture(width, height, compressedImage);

        buffer.rewind();
        if (compressedImage != null) {
            File f = new File(filePathName);
            f.createNewFile();
            JavaETC1Util.writeTexture(texture, new FileOutputStream(f));
            System.out.println("Texture PKM created ");
        }
    }

//    private void writeAlphaToFile(ByteBuffer buffer, int width, int height, String filePathName) throws FileNotFoundException, IOException {
//        ByteBuffer compressedImage;
//        final int encodedImageSize = JavaETC1.getEncodedDataSize(width, height);
//        compressedImage = ByteBuffer.allocateDirect(encodedImageSize).order(ByteOrder.nativeOrder());
//        JavaETC1.encodeImage(buffer, width, height,3,3* width, compressedImage);
//        ETC1Texture texture = new ETC1Texture(width, height, compressedImage);
//
//        buffer.rewind();
//        if (compressedImage != null) {
//            File f = new File(filePathName.replace(".png", "") + "_alpha.pkm");
//            f.delete();
//            f.createNewFile();
//            JavaETC1Util.writeTexture(texture, new FileOutputStream(f));
//            System.out.println("Texture PKM created ");
//        }
//    }
    
    
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

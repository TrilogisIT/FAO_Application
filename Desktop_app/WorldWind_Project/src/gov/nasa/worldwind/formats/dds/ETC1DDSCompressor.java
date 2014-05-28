package gov.nasa.worldwind.formats.dds;

import java.awt.image.BufferedImage;

import gov.nasa.worldwind.util.ImageUtil;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWMath;
import gov.nasa.worldwind.formats.dds.DDSCompressor;
import gov.nasa.worldwind.formats.dds.DDSConstants;
import gov.nasa.worldwind.formats.dds.DXT1Compressor;
import gov.nasa.worldwind.formats.dds.DXT3Compressor;
import gov.nasa.worldwind.formats.dds.DXTCompressionAttributes;
import gov.nasa.worldwind.formats.dds.DXTCompressor;

public class ETC1DDSCompressor extends DDSCompressor {
    protected DXTCompressor getDXTCompressor(java.awt.image.BufferedImage image, DXTCompressionAttributes attributes)
    {
        // If the caller specified a DXT format in the attributes, then we return a compressor matching that format.
        // Otherwise, we choose one automatically from the image type. If no choice can be made from the image type,
        // we default to using a DXT3 compressor.

        if (attributes.getDXTFormat() == DDSConstants.D3DFMT_DXT1)
        {
            return new DXT1Compressor();
        }
        else if (attributes.getDXTFormat() == ETCConstants.D3DFMT_ETC1)
        {
            return new ETC1Compressor();
        }
        else if (attributes.getDXTFormat() == DDSConstants.D3DFMT_DXT2
            || attributes.getDXTFormat() == DDSConstants.D3DFMT_DXT3)
        {
            return new DXT3Compressor();
        }
        else if (!image.isAlphaPremultiplied())
        {
            return new DXT1Compressor();
        }
        else
        {
            return new DXT3Compressor();
        }
    }
    
	protected java.awt.image.BufferedImage[] buildMipMaps(java.awt.image.BufferedImage image, DXTCompressionAttributes attributes) {
		// Build the mipmap chain using a premultiplied alpha image format. This is necessary to ensure that
		// transparent colors do not bleed into the opaque colors. For example, without premultiplied alpha the colors
		// in a totally transparent pixel may contribute when one mipmap level is filtered (with either a box or a
		// bilinear filter) to produce the pixels for the next level.
		//
		// The DXT color block extractor typically accessed Bitmap data via a call to getRGB(). This returns
		// a packed 8888 ARGB int, where the color components are known to be not premultiplied, and in the sRGB color
		// space. Therefore computing mipmaps in this way does not affect the rest of the DXT pipeline, unless color
		// data is accessed directly. In this case, such code would be responsible for recognizing the color model
		// (premultiplied) and behaving accordingly.

//		int maxLevel = ImageUtil.getMaxMipmapLevel(image.getWidth(), image.getHeight());

		
		return new BufferedImage[]{ImageUtil.buildMipmap(image, BufferedImage.TYPE_3BYTE_BGR)};
//		return ImageUtil.buildMipmaps(image, BufferedImage.TYPE_3BYTE_BGR, maxLevel);
	}
	
	/**
     * Returns the default compression attributes. The default DXT compression attributes are defined as follows:
     * <table> <tr><th>Attribute</th><th>Value</th></tr> <tr><td>Build Mipmaps</td><td>true</td></tr>
     * <tr><td>Premultiply Alpha</td><td>true</td></tr> <tr><td>DXT Format</td><td>Let DDSCompressor choose optimal
     * format.</td></tr> <tr><td>Enable DXT1 Alpha</td><td>false</td></tr> <tr><td>DXT1 Alpha
     * Threshold</td><td>128</td></tr> <tr><td>Compression Algorithm</td><td>Euclidean Distance</td></tr> </table>
     *
     * @return the default compression attributes.
     */
    public static DXTCompressionAttributes getDefaultCompressionAttributes()
    {
        DXTCompressionAttributes attributes = new DXTCompressionAttributes();
        attributes.setBuildMipmaps(true); // Always build mipmaps.
        attributes.setPremultiplyAlpha(true); // Always create premultiplied alpha format files..
        attributes.setDXTFormat(ETCConstants.D3DFMT_ETC1); // Allow the DDSCompressor to choose the appropriate DXT format.
        return attributes;
    }
    
    

    /**
     * Convenience method to convert the specified <code>image</code> to DDS according to the default attributes. This
     * chooses the DXT compression format best suited for the image type.
     *
     * @param image image to convert to the DDS file format.
     *
     * @return little endian ordered ByteBuffer containing the dds file bytes.
     *
     * @throws IllegalArgumentException if <code>image</code> is null, or if <code>image</code> has non power of two
     *                                  dimensions.
     */
    public static java.nio.ByteBuffer compressImage(java.awt.image.BufferedImage image)
    {
        if (image == null)
        {
            String message = Logging.getMessage("nullValue.ImageIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (!WWMath.isPowerOfTwo(image.getWidth()) || !WWMath.isPowerOfTwo(image.getHeight()))
        {
            String message = Logging.getMessage("generic.InvalidImageSize", image.getWidth(), image.getHeight());
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        ETC1DDSCompressor compressor = new ETC1DDSCompressor();
        DXTCompressionAttributes attributes = getDefaultCompressionAttributes();//TODO 
        
        
        return compressor.compressImage(image, attributes);
    }
	
}

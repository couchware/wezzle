/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.lwjgl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;

/**
 * A utility class to load textures for LWJGL. This source is based
 * on a texture that can be found in the Java Gaming (www.javagaming.org)
 * Wiki. It has been simplified slightly for explicit 2D graphics use.
 * 
 * OpenGL uses a particular image format. Since the images that are 
 * loaded from disk may not match this format this loader introduces
 * a intermediate image which the source image is copied into. In turn,
 * this image is used as source for the OpenGL texture.
 *
 * @author Kevin Glass
 * @author Brian Matzon
 * @author Cameron McKay
 */
 
public class TextureLoader
{

    /**
     * The table of textures that have been loaded in this loader.
     */
    private HashMap<String, Texture> table = new HashMap<String, Texture>();
    
    /** 
     * The colour model including alpha for the GL image.
     */
    private ColorModel glAlphaColorModel;
    
    /** 
     * The colour model for the GL image.
     */
    private ColorModel glColorModel;

    /** 
     * Create a new texture loader based on the game panel.
     *
     * @param gl The GL content in which the textures should be loaded.
     */
    public TextureLoader()
    {
        glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[]{8, 8, 8, 8},
                true,
                false,
                ComponentColorModel.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);

        glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[]{8, 8, 8, 0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);
    }

    /**
     * Create a new texture ID.
     *
     * @return A new texture ID.
     */
    private int createTextureID()
    {
        IntBuffer tmp = createIntBuffer(1);
        GL11.glGenTextures(tmp);
        return tmp.get(0);
    }

    /**
     * Load a texture.
     *
     * @param resourceName The location of the resource to load.
     * @return The loaded texture.
     * @throws IOException Indicates a failure to access the resource.
     */
    public Texture getTexture(String resourceName) throws IOException
    {
        Texture texture = table.get(resourceName);

        if (texture != null)        
            return texture;        

        texture = getTexture(resourceName,
                GL11.GL_TEXTURE_2D, // Target
                GL11.GL_RGBA,       // DST pixel format.
                GL11.GL_LINEAR,     // Min. filter (unused).
                GL11.GL_LINEAR);

        table.put(resourceName, texture);

        return texture;
    }
    
    public Texture getTexture(String resourceName, BufferedImage image) throws IOException
    {
        Texture texture = table.get(resourceName);
        
        if (texture != null)        
            return texture;        

        texture = getTexture(image,
                GL11.GL_TEXTURE_2D, // Target
                GL11.GL_RGBA,       // DST pixel format.
                GL11.GL_LINEAR,     // Min. filter (unused).
                GL11.GL_LINEAR);

        table.put(resourceName, texture);

        return texture;
    }

    /**
     * Load a texture from a PNG file into OpenGL from a image reference on
     * disk.
     *
     * @param resourceName The location of the resource to load.
     * @param target The GL target to load the texture against.
     * @param dstPixelFormat The pixel format of the screen.
     * @param minFilter The minimising filter.
     * @param magFilter The magnification filter.
     * @return The loaded texture.
     * @throws IOException Indicates a failure to access the resource.
     */
    public Texture getTexture(String resourceName,
            int target,
            int dstPixelFormat,
            int minFilter,
            int magFilter) throws IOException
    {
        int srcPixelFormat = 0;

        // Create the texture ID for this texture.
        int textureID = createTextureID();
        Texture texture = new Texture(target, textureID);

        // Bind this texture.
        GL11.glBindTexture(target, textureID);

        PNGImageData imageData = new PNGImageData();    
        InputStream in = TextureLoader.class.getClassLoader().getResourceAsStream(resourceName);
        ByteBuffer textureBuffer = imageData.loadImage(in);
        
        texture.setTextureWidth(imageData.getTextureWidth());
        texture.setTextureHeight(imageData.getTextureHeight());
        
        texture.setWidth(imageData.getWidth());
        texture.setHeight(imageData.getHeight());
        
        boolean hasAlpha = imageData.getDepth() == 32;
        srcPixelFormat = hasAlpha ? GL11.GL_RGBA : GL11.GL_RGB;
        
        //BufferedImage bufferedImage = loadImage(resourceName);
        //texture.setWidth(bufferedImage.getWidth());
        //texture.setHeight(bufferedImage.getHeight());        

//        if (bufferedImage.getColorModel().hasAlpha())
//        {
//            srcPixelFormat = GL11.GL_RGBA;
//        }
//        else
//        {
//            srcPixelFormat = GL11.GL_RGB;
//        }

        // Convert that image into a byte buffer of texture data.
        //ByteBuffer textureBuffer = convertImageData(bufferedImage, texture);

        if (target == GL11.GL_TEXTURE_2D)
        {
            GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
            GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
        }

        // Produce a texture from the byte buffer.
        GL11.glTexImage2D(target,
                0,
                dstPixelFormat,
                get2Fold(imageData.getWidth()),
                get2Fold(imageData.getHeight()),
                0,
                srcPixelFormat,
                GL11.GL_UNSIGNED_BYTE,
                textureBuffer);

        return texture;
    }
    
    /**
     * Load a texture from a buffered image into OpenGL from a image reference on
     * disk.
     *
     * @param image The image to create a texture from.
     * @param target The GL target to load the texture against.
     * @param dstPixelFormat The pixel format of the screen.
     * @param minFilter The minimising filter.
     * @param magFilter The magnification filter.
     * @return The loaded texture.
     * @throws IOException Indicates a failure to access the resource.
     */
    public Texture getTexture(BufferedImage image,
            int target,
            int dstPixelFormat,
            int minFilter,
            int magFilter) throws IOException
    {
        int srcPixelFormat = 0;

        // Create the texture ID for this texture.
        int textureID = createTextureID();
        Texture texture = new Texture(target, textureID);

        // Bind this texture.
        GL11.glBindTexture(target, textureID);       
                
        texture.setWidth(image.getWidth());
        texture.setHeight(image.getHeight());
        
        boolean hasAlpha = image.getColorModel().hasAlpha();
        srcPixelFormat = hasAlpha ? GL11.GL_RGBA : GL11.GL_RGB;
        
        ByteBuffer textureBuffer = convertImageData(image, texture);        

        if (target == GL11.GL_TEXTURE_2D)
        {
            GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
            GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
        }

        // Produce a texture from the byte buffer.
        GL11.glTexImage2D(target,
                0,
                dstPixelFormat,
                get2Fold(image.getWidth()),
                get2Fold(image.getHeight()),
                0,
                srcPixelFormat,
                GL11.GL_UNSIGNED_BYTE,
                textureBuffer);

        return texture;
    }

    /**
     * Get the closest greater power of 2 to the fold number.
     * 
     * @param fold The target number.
     * @return The power of 2.
     */
    private int get2Fold(int fold)
    {
        int val = 2;
        
        while (val < fold)
            val *= 2;        
        
        return val;
    }

    /**
     * Convert the buffered image to a texture.
     *
     * @param bufferedImage The image to convert to a texture.
     * @param texture The texture to store the data into.
     * @return A buffer containing the data.
     */
    private ByteBuffer convertImageData(BufferedImage bufferedImage, Texture texture)
    {
        ByteBuffer imageBuffer;
        WritableRaster raster;
        BufferedImage textureImage;

        int textureWidth = 2;
        int textureHeight = 2;

        // Find the closest power of 2 for the width and height
        // of the produced texture.
        while (textureWidth < bufferedImage.getWidth())
        {
            textureWidth *= 2;
        }
        
        while (textureHeight < bufferedImage.getHeight())
        {
            textureHeight *= 2;
        }

        texture.setTextureHeight(textureHeight);
        texture.setTextureWidth(textureWidth);

        // Create a raster that can be used by OpenGL as a source
        // for a texture.
        if (bufferedImage.getColorModel().hasAlpha())
        {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, textureWidth, 
                    textureHeight, 4, null);
            textureImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable());
        }
        else
        {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, textureWidth, 
                    textureHeight, 3, null);
            textureImage = new BufferedImage(glColorModel, raster, false, new Hashtable());
        }

        // Copy the source image into the produced image.
        Graphics g = textureImage.getGraphics();
        g.setColor(new Color(0f, 0f, 0f, 0f));
        g.fillRect(0, 0, textureWidth, textureHeight);
        g.drawImage(bufferedImage, 0, 0, null);

        // Build a byte buffer from the temporary image 
        // that be used by OpenGL to produce a texture.
        byte[] data = ((DataBufferByte) textureImage.getRaster().getDataBuffer()).getData();

        imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();

        return imageBuffer;
    }

    /** 
     * Load a given resource as a buffered image.
     * 
     * @param path The location of the resource to load.
     * @return The loaded buffered image.
     * @throws IOException Indicates a failure to find a resource.
     */
    private BufferedImage loadImage(String path) throws IOException
    {
        URL url = TextureLoader.class.getClassLoader().getResource(path);

        if (url == null)
        {
            throw new IOException("Cannot find: " + path + ".");
        }

        BufferedImage bufferedImage = ImageIO.read(new BufferedInputStream(
                getClass().getClassLoader().getResourceAsStream(path)));

        return bufferedImage;
    }

    /**
     * Creates an integer buffer to hold specified ints
     * - strictly a utility method
     *
     * @param size how many int to contain
     * @return created IntBuffer
     */
    protected IntBuffer createIntBuffer(int size)
    {
        ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
        temp.order(ByteOrder.nativeOrder());

        return temp.asIntBuffer();
    }    

}

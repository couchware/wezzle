/**
 * Copyright (c) 2007, Slick 2D
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Slick 2D nor the names of its contributors may be 
 *   used to endorse or promote products derived from this software without 
 *   specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ca.couchware.wezzle2d.lwjgl;

import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.CouchLogger;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;

/**
 * A TrueType font implementation for Slick.
 * Adapted for use with Wezzle2D.
 * 
 * @author Cameron McKay
 * @author James Chambers (Jimmy)
 * @author Jeremy Adams (elias4444)
 * @author Kevin Glass (kevglass)
 */
public class TrueTypeFont implements Serializable
{

    /** Array that holds necessary information about the font characters. */
    private CharacterInfo[] charArray = new CharacterInfo[256];      
    
    /** Font size. */
    private int fontSize = 0;
    
    /** Font. height. */
    private int fontHeight = 0;
    
    /** Texture used to cache the font characters. */
    private Texture fontTexture;
    
    /** Default font texture width. */
    final static private int FontTextureWidth = 1024;
    final static private float FlFontTextureWidth = 1024f;
    
    /** Default font texture height. */
    final static private int FontTextureHeight = 512;
    final static private float FlFontTextureHeight= 512f;
    
    /** The texture loader. */
    private TextureLoader textureLoader;
    
    /** A reference to Java's AWT Font that we create our font texture from */
    private Font font;
    
    /** 
     * A text layout with the two characters that have the lowest descent 
     * and highest ascent.
     */
    private TextLayout heightLayout;
    
    /**
     * A text layout with the character having the highest ascent, giving the
     * baseline information.
     */
    private TextLayout ascentLayout;

    /**
     * This is a special internal class that holds our necessary information for
     * the font characters. This includes width, height, and where the character
     * is stored on the font texture.
     */
    private static class CharacterInfo implements Serializable
    {                
        /** Character width. */
        public int width;
        
        /** Character height. */
        public int height;
        
        /** The width of the character on the line. */
        public int lineWidth;
        
        /** The height of the character starting at the baseline. */
        public int ascent;
        
        /** The descent of the character. */
        public int descent;
        
        /** Character's stored x position. */
        public int x;
        
        /** Character's stored y position. */
        public int y;        
        
        /** 
         * The amount of space before the letter.  This is used to remove
         * the space before a line so it is exactly aligned on the x-side.
         */
        public int space;
    }

    /**
     * Creates an OpenGL version of a TrueType font.
     * 
     * @param font
     *            Standard Java AWT font	
     */
    public TrueTypeFont(TextureLoader textureLoader, Font font)
    {
        if (textureLoader == null)
            throw new NullPointerException("Texture loader must not be null.");
        
        if (font == null)
            throw new NullPointerException("Font must not be null.");
            
        this.font = font;
        this.fontSize = font.getSize();
        this.textureLoader = textureLoader;                

        createFont();
    }

    private void determineHeight(Graphics2D gfx)
    {
        // Create the text layout instances for measuring.
        this.heightLayout = createTextLayout( gfx, "Yg", font );
        this.ascentLayout = createTextLayout( gfx, "Y", font );
        this.fontHeight = (int) ascentLayout.getBounds().getHeight();
    }

    /**
     * Create a standard Java2D BufferedImage of the given character.
     * 
     * @param ch
     *            The character to create a BufferedImage for
     * 
     * @return A BufferedImage containing the character
     */
    private BufferedImage getCharImage(char ch, CharacterInfo charInfo)
    {
        // Create a temporary image to extract the character size.
        BufferedImage i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gfx1 = (Graphics2D) i.getGraphics();

        // Turn on anti-aliasing for smooth-looking fonts.
        gfx1.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        gfx1.setFont(font);
        
        // Get a text layout for this.        
        TextLayout widthLayout = createTextLayout(gfx1, String.valueOf(ch), font);
        
        //fontMetrics = gfx1.getFontMetrics();
        
        // Record the space before the character.
        charInfo.space = (int) widthLayout.getBounds().getMinX();
        
        int charWidth = (int) (widthLayout.getBounds().getMaxX() * 1.5);
        if (charWidth <= 0) charWidth = 1;
        charInfo.width = charWidth;
        
        int charHeight = (int) (heightLayout.getBounds().getHeight() * 1.5);
        if (charHeight <= 0) charHeight = fontSize;        
        charInfo.height = charHeight;
        
        int charAscent = (int) (ascentLayout.getBounds().getHeight() * 1.5);
        charInfo.ascent = charAscent;
        charInfo.descent = charHeight - charAscent;        
        
        FontMetrics fm = gfx1.getFontMetrics(font);
        charInfo.lineWidth = fm.stringWidth(String.valueOf(ch));

        // Create another image holding the character we are creating.
        BufferedImage fontImage;
        fontImage = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gfx2 = (Graphics2D) fontImage.getGraphics();
        
        gfx2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);       
        gfx2.setFont(font);
        gfx2.setColor(Color.WHITE);
        
        int charX = 0;
        int charY = 0;
        
        gfx2.drawString(String.valueOf(ch), charX, charY + charAscent);

        return fontImage;
    }

    /**
     * Create and store the font.
     */
    private void createFont()
    {                
        try
        {
            BufferedImage image = new BufferedImage(
                FontTextureWidth, FontTextureHeight,
                BufferedImage.TYPE_INT_ARGB);

            Graphics2D gfx = (Graphics2D) image.getGraphics();
            determineHeight( gfx );

            int rowHeight = 0;
            int positionX = 0;
            int positionY = 0;

            for (int i = 0; i < 256; i++)
            {
                char ch = (char) i;

                CharacterInfo charInfo = new CharacterInfo();
                BufferedImage charImage = getCharImage(ch, charInfo);

                if (positionX + charInfo.width >= FontTextureWidth)
                {
                    positionX = 0;
                    positionY += rowHeight;
                    rowHeight = 0;
                }

                charInfo.x = positionX;
                charInfo.y = positionY;

                if (charInfo.height > rowHeight)
                {
                    rowHeight = charInfo.height;
                }

                // Draw it here
                gfx.drawImage(charImage, positionX, positionY, null);

                positionX += charInfo.width;

                charArray[i] = charInfo;
            }
            
            fontTexture = textureLoader.getTexture(font.toString(), image);
        }        
        catch (IOException e)
        {
            CouchLogger.get().recordException(this.getClass(), e);
        }

    }

    /**
     * Draw a textured quad.
     * 
     * @param drawX
     *            The left x position to draw to
     * @param drawY
     *            The top y position to draw to
     * @param drawX2
     *            The right x position to draw to
     * @param drawY2
     *            The bottom y position to draw to
     * @param srcX
     *            The left source x position to draw from
     * @param srcY
     *            The top source y position to draw from
     * @param srcX2
     *            The right source x position to draw from
     * @param srcY2
     *            The bottom source y position to draw from
     */
    private void drawQuad(float drawX, float drawY, float drawX2, float drawY2,
            float srcX, float srcY, float srcX2, float srcY2)
    {
        float drawWidth = drawX2 - drawX;
        float drawHeight = drawY2 - drawY;
        float textureSrcX = srcX / FlFontTextureWidth;
        float textureSrcY = srcY / FlFontTextureHeight;
        float srcWidth = srcX2 - srcX;
        float srcHeight = srcY2 - srcY;
        float renderWidth = (srcWidth / FlFontTextureWidth);
        float renderHeight = (srcHeight / FlFontTextureHeight);

        GL11.glTexCoord2f(textureSrcX, textureSrcY);
        GL11.glVertex2f(drawX, drawY);
        GL11.glTexCoord2f(textureSrcX, textureSrcY + renderHeight);
        GL11.glVertex2f(drawX, drawY + drawHeight);
        GL11.glTexCoord2f(textureSrcX + renderWidth, textureSrcY + renderHeight);
        GL11.glVertex2f(drawX + drawWidth, drawY + drawHeight);
        GL11.glTexCoord2f(textureSrcX + renderWidth, textureSrcY);
        GL11.glVertex2f(drawX + drawWidth, drawY);
    }

    /**
     * Get the width of a given string.
     * 
     * @param str
     *            The string to get the width of.
     * 
     * @return The width of string.
     */
    public int stringWidth(String str)
    {
        CharacterInfo charInfo;
        
        int totalWidth = 0;                
        int currentChar = 0;
        int space = 0;
        
        if (str.length() > 0)
            space = charArray[str.charAt(0)].space;
        
        for (int i = 0; i < str.length(); i++)
        {
            currentChar = str.charAt(i);
            if (currentChar < 256)
            {
                charInfo = charArray[currentChar];
                totalWidth += charInfo.lineWidth;
            }
        }
        
        return totalWidth - space;
    }

    /**
     * Get the font height.
     * 
     * @return The height of the font
     */
    public int getHeight()
    {
        return fontHeight;
    }       
    
     /**
     * Updates the text layout instance.
     * @param frctx The current font render context.
     */
    private TextLayout createTextLayout(Graphics2D gfx, String text, Font font)
    {             
        // Set the font.
        gfx.setFont(font);  
        gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		gfx.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);        
        
        // Get the render context.
        FontRenderContext frctx = gfx.getFontRenderContext();
        
        // Create new text layout.        
        return new TextLayout(text, font, frctx);                     
    }   

    /**
     * ...
     */
    public void drawString(float x, float y, String str,
            Color color, int startIndex, int endIndex)
    {
        // Set the color.
        GL11.glColor4f((float) color.getRed() / 255f,
                       (float) color.getGreen() / 255f,
                       (float) color.getBlue() / 255f,
                       (float) color.getAlpha() / 255f);
        
        // Bind the texture.
        fontTexture.bind();

        CharacterInfo charInfo;
        int currentChar;
        int totalWidth = 0;
        
        if (str.length() > 0)
            totalWidth = -charArray[str.charAt(0)].space;

        GL11.glBegin(GL11.GL_QUADS);
                
        for (int i = 0; i < str.length(); i++)
        {
            currentChar = str.charAt(i);
            if (currentChar < 256)
            {
                charInfo = charArray[currentChar];

                if ((i >= startIndex) || (i <= endIndex))
                {
                    drawQuad(
                            x + totalWidth, 
                            y - charInfo.ascent,
                            x + totalWidth + charInfo.width, 
                            y - charInfo.ascent + charInfo.height, 
                            charInfo.x, 
                            charInfo.y, 
                            charInfo.x + charInfo.width,
                            charInfo.y + charInfo.height);
                }
                
                totalWidth += charInfo.lineWidth;
                
            } // end if
        } // end for

        GL11.glEnd();
    }
    
     /**
     * Draw a string.
     * 
     * @param x
     *            The x position to draw the string
     * @param y
     *            The y position to draw the string
     * @param str
     *            The string to draw
     * @param color
     *            The color to draw the text
     */
    public void drawString(float x, float y, String str, Color color)
    {
        drawString(x, y, str, color, 0, str.length() - 1);
    }

    /**
     * Draw a string
     * 
     * @param x
     *            The x position to draw the string
     * @param y
     *            The y position to draw the string
     * @param str
     *            The string to draw
     */
    public void drawString(float x, float y, String str)
    {
        drawString(x, y, str, Color.WHITE);
    }
}

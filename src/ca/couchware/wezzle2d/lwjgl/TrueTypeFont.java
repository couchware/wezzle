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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.lwjgl.opengl.GL11;

/**
 * A TrueType font implementation for Slick.
 * Adapted for use with Wezzle2D.
 * 
 * @author Cameorn McKay
 * @author James Chambers (Jimmy)
 * @author Jeremy Adams (elias4444)
 * @author Kevin Glass (kevglass)
 */
public class TrueTypeFont
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
    private float textureWidth = 512.0f;
    
    /** Default font texture height. */
    private float textureHeight = 512.0f;
    
    /** The texture loader. */
    private TextureLoader textureLoader;
    
    /** A reference to Java's AWT Font that we create our font texture from */
    private Font font;
    
    /** The font metrics for our Java AWT font */
    private FontMetrics fontMetrics;    

    /**
     * This is a special internal class that holds our necessary information for
     * the font characters. This includes width, height, and where the character
     * is stored on the font texture.
     */
    private class CharacterInfo
    {
        /** Character's width */
        public int width;
        
        /** Character's height */
        public int height;
        
        /** Character's stored x position */
        public int storedX;
        
        /** Character's stored y position */
        public int storedY;        
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

        createPlainSet();
    }

    /**
     * Create a standard Java2D BufferedImage of the given character.
     * 
     * @param ch
     *            The character to create a BufferedImage for
     * 
     * @return A BufferedImage containing the character
     */
    private BufferedImage getCharImage(char ch)
    {
        // Create a temporary image to extract the character size.
        BufferedImage i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gfx1 = (Graphics2D) i.getGraphics();

        // Turn on anti-aliasing for smooth-looking fonts.
        gfx1.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        gfx1.setFont(font);
        
        fontMetrics = gfx1.getFontMetrics();
        
        int charWidth = fontMetrics.charWidth(ch);
        if (charWidth <= 0) charWidth = 1;
        
        int charHeight = fontMetrics.getHeight();
        if (charHeight <= 0) charHeight = fontSize;        

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
        
        gfx2.drawString(String.valueOf(ch), (charX), (charY) + fontMetrics.getAscent());

        return fontImage;
    }

    /**
     * Create and store the font.
     */
    private void createPlainSet()
    {
        try
        {
            BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gfx = (Graphics2D) image.getGraphics();

            int rowHeight = 0;
            int positionX = 0;
            int positionY = 0;

            for (int i = 0; i < 256; i++)
            {
                char ch = (char) i;
                BufferedImage charImage = getCharImage(ch);

                CharacterInfo newIntObject = new CharacterInfo();

                newIntObject.width = charImage.getWidth();
                newIntObject.height = charImage.getHeight();

                if (positionX + newIntObject.width >= 512)
                {
                    positionX = 0;
                    positionY += rowHeight;
                    rowHeight = 0;
                }

                newIntObject.storedX = positionX;
                newIntObject.storedY = positionY;

                if (newIntObject.height > fontHeight)
                {
                    fontHeight = newIntObject.height;
                }

                if (newIntObject.height > rowHeight)
                {
                    rowHeight = newIntObject.height;
                }

                // Draw it here
                gfx.drawImage(charImage, positionX, positionY, null);

                positionX += newIntObject.width;

                charArray[i] = newIntObject;

                charImage = null;
            }

            fontTexture = textureLoader.getTexture(font.toString(), image);
        }
        catch (IOException e)
        {
            System.err.println("Failed to create font.");
            e.printStackTrace();
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
        float DrawWidth = drawX2 - drawX;
        float DrawHeight = drawY2 - drawY;
        float TextureSrcX = srcX / textureWidth;
        float TextureSrcY = srcY / textureHeight;
        float SrcWidth = srcX2 - srcX;
        float SrcHeight = srcY2 - srcY;
        float RenderWidth = (SrcWidth / textureWidth);
        float RenderHeight = (SrcHeight / textureHeight);

        GL11.glTexCoord2f(TextureSrcX, TextureSrcY);
        GL11.glVertex2f(drawX, drawY);
        GL11.glTexCoord2f(TextureSrcX, TextureSrcY + RenderHeight);
        GL11.glVertex2f(drawX, drawY + DrawHeight);
        GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
        GL11.glVertex2f(drawX + DrawWidth, drawY + DrawHeight);
        GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY);
        GL11.glVertex2f(drawX + DrawWidth, drawY);
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
        CharacterInfo charInfo = null;
        
        int totalWidth = 0;        
        int currentChar = 0;
        
        for (int i = 0; i < str.length(); i++)
        {
            currentChar = str.charAt(i);
            if (currentChar < 256)
            {
                charInfo = charArray[currentChar];
                totalWidth += charInfo.width;
            }
        }
        
        return totalWidth;
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

        CharacterInfo charInfo = null;
        int currentChar;
        int totalWidth = 0;

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
                            x + totalWidth, y,
                            x + totalWidth + charInfo.width, y + charInfo.height, 
                            charInfo.storedX, charInfo.storedY, 
                            charInfo.storedX + charInfo.width,
                            charInfo.storedY + charInfo.height);
                }
                
                totalWidth += charInfo.width;
                
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

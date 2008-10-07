package ca.couchware.wezzle2d.lwjgl;

import ca.couchware.wezzle2d.java2d.*;
import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.manager.LogManager;
import java.awt.Font;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author cdmckay
 */
public class FontStore 
{
    /**
     * The font url.
     */
    final private static String PATH = Game.FONTS_PATH + "/bubbleboy2.ttf";
    
    /**
	 * The single instance of this class
	 */
	final private static FontStore single = new FontStore();

    /**
     * The base font that all font sizes are derived from.
     */
    private Font baseFont;
    
	/**
	 * Get the single instance of this class .
	 * 
	 * @return The single instance of this class
	 */
	public static FontStore get()
	{
		return single;
	}
    
    /**
     * The font map.
     */
    private static final HashMap<Integer, TrueTypeFont> fontMap = 
            new HashMap<Integer, TrueTypeFont>();
    
    public TrueTypeFont getFont(Integer size, TextureLoader textureLoader)
    {       
        // If we already have the font cached, return it.
		if (fontMap.containsKey(size) == true)
		{
			return fontMap.get(size);
		}
        // If the size is one, load it from the ttf file.
        // Note for retarded persons: The font size 1 is only created once.
        // All ensuing invocations will use the cached version.
        else if (size == 1)
        {            
            try
            {
                URL url = this.getClass().getClassLoader().getResource(PATH);
                
                InputStream in = url.openStream();
                
                Font font = Font.createFont(Font.TRUETYPE_FONT, in);
                this.baseFont = font;
                
                TrueTypeFont ttfont = new TrueTypeFont(textureLoader, font);
                fontMap.put(size, ttfont);
                
                in.close();         
                
                return ttfont;
            }
            catch(Exception e)
            {
                LogManager.recordException(e);
            }
        }
        // Otherwise, derive all other fonts from the size 1.        
        else
        {
            LogManager.recordMessage("Font size " + size + " created.", 
                    "Java2DFontStore#getFont");   
            
            // Create the base font if it doesn't exist.
            if (baseFont == null)
                getFont(1, textureLoader);
            
            // Derive new font from it.
            Font font = baseFont.deriveFont((float) size);
            TrueTypeFont ttfont = new TrueTypeFont(textureLoader, font);
            fontMap.put(size, ttfont);
            
            return ttfont;
        }  
        
        throw new IllegalStateException("Font problem.");
    }
}
package ca.couchware.wezzle2d.java2d;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Font;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author cdmckay
 */
public class Java2DFontStore 
{
    /**
     * The font url.
     */
    final private static String PATH = Game.FONTS_PATH + "/bubbleboy2.ttf";
    
    /**
	 * The single instance of this class
	 */
	final private static Java2DFontStore single = new Java2DFontStore();

	/**
	 * Get the single instance of this class .
	 * 
	 * @return The single instance of this class
	 */
	public static Java2DFontStore get()
	{
		return single;
	}
    
    /**
     * The font map.
     */
    private static final HashMap<Integer, Font> fontMap = 
            new HashMap<Integer, Font>();
    
    public Font getFont(Integer size)
    {       
        // If we already have the font cached, return it.
		if (fontMap.containsKey(size) == true)
		{
			return (Font) fontMap.get(size);
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
                fontMap.put(size, font);
                in.close();         
                
                return font;
            }
            catch(Exception e)
            {
                Util.handleException(e);
            }
        }
        // Otherwise, derive all other fonts from the size 1.        
        else
        {
            Util.handleMessage("Font size " + size + " created.", 
                    Thread.currentThread());
            Font base = (Font) getFont(new Integer(1));
            Font font = base.deriveFont((float) size);
            fontMap.put(size, font);
            
            return font;
        }  
        
        throw new IllegalStateException("Font problem.");
    }
}

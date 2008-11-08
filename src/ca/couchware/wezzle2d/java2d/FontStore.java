package ca.couchware.wezzle2d.java2d;

import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.Settings;
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
    final private static String PATH = Settings.FONT_RESOURCES_PATH + "/bubbleboy2.ttf";
    
    /**
	 * The single instance of this class
	 */
	final private static FontStore single = new FontStore();

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
                LogManager.recordException(e);
            }
        }
        // Otherwise, derive all other fonts from the size 1.        
        else
        {
            LogManager.recordMessage("Font size " + size + " created.", 
                    "Java2DFontStore#getFont");
            Font base = (Font) getFont(new Integer(1));
            Font font = base.deriveFont((float) size);
            fontMap.put(size, font);
            
            return font;
        }  
        
        throw new IllegalStateException("Font problem.");
    }
}

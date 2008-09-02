package ca.couchware.wezzle2d.java2d;

import ca.couchware.wezzle2d.manager.LogManager;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.HashMap;

/**
 *
 * @author cdmckay
 */
public class Java2DTextLayoutStore 
{   
    
    /**
	 * The single instance of this class
	 */
	final private static Java2DTextLayoutStore single = new Java2DTextLayoutStore();

	/**
	 * Get the single instance of this class .
	 * 
	 * @return The single instance of this class
	 */
	public static Java2DTextLayoutStore get()
	{
		return single;
	}
    
    /**
     * The number of cache hits.
     */
    int hits = 0;
    
    /**
     * The number of cache misses.
     */
    int misses = 0;
    
    
    /**
     * The font map.
     */
    private static final HashMap<String, HashMap<Font, TextLayout>> textLayoutMap = 
            new HashMap<String, HashMap<Font, TextLayout>>();
    
    public TextLayout getTextLayout(Graphics2D g, String text, Font font, boolean cached)
    {         
//        LogManager.recordMessage("Hits: " + hits + ", Misses: " + misses + ".",
//                    "Java2DTextLayoutStore#getTextLayout");
        
        // If we already have the layout cached, return it.
		if (textLayoutMap.containsKey(text) == true
                && textLayoutMap.get(text).containsKey(font) == true)
		{
            hits++;
			return textLayoutMap.get(text).get(font);
		}   
        // Otherwise, add it to the cache.
        else            
        {
            misses++;
//            LogManager.recordMessage("TextLayout '" + text + "' created.", 
//                    "Java2DTextLayoutStore#getTextLayout");           
//            LogManager.recordMessage("There are now " + textLayoutMap.size() + " cached layouts.",
//                    "Java2DTextLayoutStore#getTextLayout");
                        
            TextLayout textLayout = createTextLayout(g, text, font);
            
            if (cached == true)
            {
                if (textLayoutMap.containsKey(text) == false)
                textLayoutMap.put(text, new HashMap<Font, TextLayout>());
            
                textLayoutMap.get(text).put(font, textLayout);
            }
                        
            return textLayout;
        }                    
    }
    
     /**
     * Updates the text layout instance.
     * @param frctx The current font render context.
     */
    private TextLayout createTextLayout(Graphics2D g, String text, Font font)
    {             
        // Set the font.
        g.setFont(font);  
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);        
        
        // Get the render context.
        FontRenderContext frctx = g.getFontRenderContext();
        
        // Create new text layout.        
        return new TextLayout(text, font, frctx);                     
    }   
}

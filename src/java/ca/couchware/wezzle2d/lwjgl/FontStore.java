package ca.couchware.wezzle2d.lwjgl;

import ca.couchware.wezzle2d.util.CouchLogger;
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
    final private static String PATH = Settings.getFontResourcesPath() + "/Bubbleboy-2.ttf";
   
    /**
     * The base font that all font sizes are derived from.
     */
    private Font baseFont;

    /**
     * The font map.
     */
    private final HashMap<Integer, TrueTypeFont> fontMap =
            new HashMap<Integer, TrueTypeFont>();  
   
    public TrueTypeFont getFont(Integer size, TextureLoader textureLoader)
    {
        // Between 20 and 50, only use even fonts.
        if (size > 20 && size % 2 != 0)
        {            
            size++;
        }
        
        // If we already have the font cached, return it.
        if ( fontMap.containsKey( size ) )
        {
            return fontMap.get( size );
        }
        // If the size is one, load it from the ttf file.
        // Note for retarded persons: The font size 1 is only created once.
        // All ensuing invocations will use the cached version.
        else if ( size == 1 )
        {
            try
            {
                URL url = this.getClass().getClassLoader().getResource( PATH );
                InputStream in = url.openStream();
                Font font = Font.createFont( Font.TRUETYPE_FONT, in );
                this.baseFont = font;

                TrueTypeFont ttfont = new TrueTypeFont( textureLoader, font );
                fontMap.put( size, ttfont );

                in.close();

                return ttfont;
            }
            catch ( Exception e )
            {
                CouchLogger.get().recordException( this.getClass(), e, true /* Fatal */ );
            }
        }
        // Otherwise, derive all other fonts from the size 1.        
        else
        {
            //CouchLogger.get().recordMessage( this.getClass(), "Font size " + size + " created" );

            // Create the base font if it doesn't exist.
            if ( baseFont == null )
            {
                getFont( 1, textureLoader );
            }            

            // Derive new font from it.
            Font font = baseFont.deriveFont( (float) size );
            TrueTypeFont ttfont = new TrueTypeFont( textureLoader, font );
            fontMap.put( size, ttfont );

            //CouchLogger.get().recordMessage( this.getClass(), String.format( "Create new font size: %d", size ) );

            return ttfont;
        }

        throw new IllegalStateException( "Unknown font problem" );
    }

}

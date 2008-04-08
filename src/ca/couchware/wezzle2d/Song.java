
package ca.couchware.wezzle2d;

import java.io.BufferedInputStream;
import java.net.URL;
import javazoom.jl.player.Player;

/**
 * A class to hold a song that will be payed by the music manager.
 * a song consists of a key and a path to the associated mp3.
 * 
 * @author Kevin
 */

public class Song 
{
    /** The song's key */
    private String key;
    
    /** The song's player */
    private Player player;
    
    public Song(String key, String path)
    {
        // The associated key.
        this.key = key;
        
        // Load the reserouce.
        URL file = this.getClass().getClassLoader()
            .getResource(path);
        
        // Check the URL.
        if(file == null)
            throw new RuntimeException("URL ERROR: " + path 
                    + " does not exist.");
        
        // Create the player.
        try
        {
            BufferedInputStream bis = new BufferedInputStream(file.openStream());
            this.player = new Player(bis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //--------------------------------------------------------------------------
    // Getters
    //--------------------------------------------------------------------------
    
    public String getKey()
    {
        return this.key;
    }
    
    public Player getPlayer()
    {
        return this.player;
    }
    
}

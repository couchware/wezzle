package ca.couchware.wezzle2d;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import javazoom.jl.player.Player;

/**
 * A class to handle background music.
 * @author Kevin
 */

public class MusicManager 
{
  
    /** The list of songs */
    private ArrayList songList;
    
    /** The song number we are one */
    private int songNum;
    
    /** The game */
    private Game game;

    // constructor creates the song list.
    public MusicManager() 
    {        
        //initiate the array list and song number.
        this.songList = new ArrayList();
        this.songNum = 0;
       
        // Add some music. Note that the order songs play is the reverse of the
        // order in which they were added. i.e. The last song added is the 
        // first song played.
        this.songList.add(new Song("Prelude", Game.MUSIC_PATH 
                + "/PreludeinCMinorRemix.mp3"));
        this.songList.add(new Song("Tron", Game.MUSIC_PATH 
                + "/IntergalacticTron.mp3"));
        
        
       
    }
    
    /**
     * A method to add a new song to the player.
     * 
     * @param newSong The new song.
     */
    public void addSong(Song newSong)
    {
        this.songList.add(newSong);
    }
    
    
    /**
     * A method to remove a song by it's key value.
     * Note: this method does not set the song to null.
     * 
     * @param key The key of the associated song.
     * @return true if the song was removed, false otherwise.
     */
    public boolean removeSong (final String key)
    {
         // Find and remove the song.
        for(int i = 0; i < songList.size(); i++)
        {
            if(((Song) songList.get(i)).getKey().equals(key))
            {
                songList.remove(i); 
                return true;
            }

        }
        
        return false;
    }
    
    /**
     * Return a reference to the song with the associated key.
     * Note: This method does not remove the song from the list.
     * Note: returns null if the key was not found.
     * 
     * @param key The associated key.
     * @return The song or null if the key was not found.
     */
    public Song getSong(final String key)
    {
         // find and return the song.
        for(int i = 0; i < songList.size(); i++)
        {
            if(((Song) songList.get(i)).getKey().equals(key))
            {
                return (Song) songList.get(i); 
            }
        }
        
        return null;
    }
    
    /**
     * A method to play a specific song identified by it's key.
     * 
     * @param key The key of the associated song.
     */
    public void playSong(final String key)
    {
        // Flag music playing.
        game.musicPlayingInProgress = true;
        
        // Play the song in the background.
        new Thread() 
        {
            @Override
            public void run() 
            {
                try 
                { 
                    // Play the song.
                    for(int i = 0; i < songList.size(); i++)
                    {
                        if(((Song) songList.get(i)).getKey().equals(key))
                        {
                            ((Song) songList.get(i)).getPlayer().play(); 
                            break;
                        }
                        
                    }
                    // Signal song is done.
                    game.musicPlayingInProgress = false;
                }
                catch (Exception e) 
                { 
                    System.out.println(e); 
                }
            }
        }.start();
    }

  
 
    /**
     * A method to cycle through the list of songs. One at a time.
     */
    public void playNext() 
    {
        // Flag music playing in game.
        game.musicPlayingInProgress = true;
        
        // Determine the next song to play.
        this.songNum = (this.songNum + 1) % this.songList.size();
        
        // run in new thread to play in background
        new Thread() 
        {
            @Override
            public void run() 
            {
                try 
                { 
                    // Play the current song.
                    ((Song) songList.get(songNum)).getPlayer().play(); 
                    
                    // Signal song is done.
                    game.musicPlayingInProgress = false;
                }
                catch (Exception e) 
                { 
                    System.out.println(e); 
                }
            }
        }.start();
    }
}

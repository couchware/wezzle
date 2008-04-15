package ca.couchware.wezzle2d.music;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;

/**
 * A class to handle background music.
 * @author Kevin
 */

public class MusicManager 
{
  
    /** 
     * A link to the property manager. 
     */
    private PropertyManager propertyMan;
    
    /** 
     * The list of songs.
     */
    private ArrayList<Song> songList;
    
    /** 
     * The song number we are one 
     */
    private int songNum;
    
    /** 
     * A variable to check if the song is paused.
     */
    private boolean paused = false;
    
    /**
     * Is the music playing?
     */
    private volatile boolean playing;
      
    /**
     * The volume level.
     * range: -80.0 - 6.0206
     */    
    private float volume;
    
    /** 
     * How much to adjust the volume by.
     */
    private static final float VOLUME_STEP = 0.5f;
    
    /**
     * Creates the song list.
     */
    public MusicManager(PropertyManager propertyMan) 
    {        
        // The property manager.
        this.propertyMan = propertyMan;
        
        // Initiate the array list and song number.
        this.songList = new ArrayList<Song>();      
        
        // The music is not playing.
        setPlaying(false);
       
        // Add some music.  This is the order they will play in, but it will
        // not necessarily start on the first song.
        this.songList.add(new Song("Turning The Page", Game.MUSIC_PATH 
                + "/TurningThePage.ogg"));
        this.songList.add(new Song("Taking a Stroll", Game.MUSIC_PATH 
                + "/TakingAStroll.ogg"));
        this.songList.add(new Song("Intergalactic Tron", Game.MUSIC_PATH 
                + "/IntergalacticTron.ogg")); 
        
        // Randomly pick a starting song.
        this.songNum = Util.random.nextInt(songList.size());
        
        // Get the default volume.
        this.volume = propertyMan
                .getFloatProperty(PropertyManager.KEY_MUSIC_VOLUME);
    }
    
    /**
     * A method to add a new song to the player.
     * 
     * @param newSong The new song.
     */
    public void addSong(Song song)
    {
        this.songList.add(song);
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
        for (int i = 0; i < songList.size(); i++)
        {
            if (songList.get(i).getKey().equals(key) == true)
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
        for (int i = 0; i < songList.size(); i++)
        {
            if (songList.get(i).getKey().equals(key) == true)
            {
                return songList.get(i); 
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
        setPlaying(true);
        
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
                        if(songList.get(i).getKey().equals(key) == true)
                        {
                            // Adjust the song number.
                            songNum = i;
                            
                            // Set the volume.
                            songList.get(songNum).setVolume(volume);
                            
                            // Play the song.
                            songList.get(songNum).play(); 
                            break;
                        }
                        
                    }
                    // Signal song is done.
                    setPlaying(false);
                }
                catch (Exception e) 
                { 
                    Util.handleException(e); 
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
        setPlaying(true);
        
        // Determine the next song to play.
        this.songNum = (this.songNum + 1) % this.songList.size();
        
        // Grab that song.
        final Song song = songList.get(songNum);
        
        // Run in new thread to play in background.
        new Thread() 
        {
            @Override
            public void run() 
            {
                try 
                {                     
                    // Set the volume.
                    song.setVolume(getVolume());
                    
                    // Play the current song.
                    song.play(); 
                    
                    // Signal song is done.
                    setPlaying(false);
                }
                catch (Exception e) 
                { 
                    Util.handleException(e); 
                }
            }
        }.start();
    }

    /**
     * Is the music playing?
     * 
     * @return True if it is, false otherwise.
     */
    public synchronized boolean isPlaying()
    {
        return playing;
    }

    /**
     * Sets whether or not the music is playing.
     * 
     * @param playing
     */
    private synchronized void setPlaying(boolean playing)
    {
        this.playing = playing;
    }    
    
    /**
     * Pause the song.
     */
    public void setPaused(boolean paused)
    {
        this.paused = paused;                    
        this.songList.get(songNum).setPaused(paused);                 
    }
        
    /**
     * Return whether or not the song is paused.
     * @return true if paused, false otherwise.
     */
    public boolean isPaused()
    {
        return this.paused;
    }

    /**
     * Gets the current volume.
     * 
     * @return
     */
    public synchronized float getVolume()
    {
        return volume;
    }  
    
    /** 
     * A method to increase the volume of the song.
     */
    public synchronized void increaseVolume()
    {
        // Adjust the volume.
        this.volume += VOLUME_STEP;
        
        // Max volume.
        if (this.volume > 6.0206f)
            this.volume = 6.0206f;
        
        // Adjust the property;
        propertyMan.setProperty(PropertyManager.KEY_MUSIC_VOLUME, 
                Float.toString(this.volume));
        
        // Adjust the current playing song.
        if (isPlaying() == true)
        {
            this.songList.get(this.songNum).setVolume(this.volume);
        }            
    }
    
    /**
     * A method to decrease the volume of the song
     */
    public synchronized void decreaseVolume()
    {
         // Adjust the volume.
        this.volume -= VOLUME_STEP;
        
        // Min volume.
        if (this.volume < -80.0f)
            this.volume = -80.0f;
        
         // Adjust the property;
        propertyMan.setProperty(PropertyManager.KEY_MUSIC_VOLUME, 
                Float.toString(this.volume));
        
        // Adjust the current playing song.
        if (isPlaying() == true)
        {
            this.songList.get(this.songNum).setVolume(volume);
        }            
    }
}

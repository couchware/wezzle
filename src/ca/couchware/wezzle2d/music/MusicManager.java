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
  
    /** The list of songs */
    private ArrayList<Song> songList;
    
    /** The song number we are one */
    private int songNum;
    
    /** A variable to check if the song is paused */
    private boolean paused = false;
    
    /**
     * Is the music playing?
     */
    private volatile boolean musicPlayingInProgress;
      
    /**
     * The volume level.
     * range: -80.0 - 6.0206
     */    
    private float volume;
    
    /** How much to adjust the volume by */
    private static final float volumeAdjustment = 0.5f;
    
    /**
     * Creates the song list.
     */
    public MusicManager() 
    {        
        // Initiate the array list and song number.
        this.songList = new ArrayList<Song>();        
        this.musicPlayingInProgress = false;
       
        // Add some music.  This is the order they will play in, but it will
        // not necessarily start on the first song.
        this.songList.add(new Song("Turning The Page", Game.MUSIC_PATH 
                + "/TurningThePage.mp3"));
        this.songList.add(new Song("Taking a Stroll", Game.MUSIC_PATH 
                + "/TakingAStroll.mp3"));
        this.songList.add(new Song("Intergalactic Tron", Game.MUSIC_PATH 
                + "/IntergalacticTron.mp3")); 
        
        // Randomly pick a starting song.
        this.songNum = Util.random.nextInt(songList.size());
        
        // Get the default volume.
        this.volume = songList.get(0).getVolume();
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
        //game.musicPlayingInProgress = true;
        setMusicPlaying(true);
        
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
                            songList.get(i).play(); 
                            break;
                        }
                        
                    }
                    // Signal song is done.
                    setMusicPlaying(false);
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
        setMusicPlaying(true);
        
        // Determine the next song to play.
        this.songNum = (this.songNum + 1) % this.songList.size();
        
        // Run in new thread to play in background.
        new Thread() 
        {
            @Override
            public void run() 
            {
                try 
                {                     
                    // Set the volume.
                    songList.get(songNum).setVolume(volume);
                    
                    // Play the current song.
                    songList.get(songNum).play(); 
                    
                    // Signal song is done.
                    setMusicPlaying(false);
                }
                catch (Exception e) 
                { 
                    Util.handleException(e); 
                }
            }
        }.start();
    }

    public boolean isMusicPlaying()
    {
        return musicPlayingInProgress;
    }

    public void setMusicPlaying(boolean musicPlayingInProgress)
    {
        this.musicPlayingInProgress = musicPlayingInProgress;
    }    
    
    /**
     * Pause the song.
     */
    public void setPaused(boolean paused)
    {
        this.paused = paused;                    
        this.songList.get(this.songNum).setPaused(paused);        
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
     * A method to increase the volume of the song.
     */
    public void increaseVolume()
    {
        // Adjust the volume.
        this.volume += volumeAdjustment;
        
        // Max volume.
        if (this.volume > 6.0206f)
            this.volume = 6.0206f;
        
        // Adjust the current playing song.
        if (this.musicPlayingInProgress == true)
        {
            this.songList.get(this.songNum).setVolume(this.volume);
            this.songList.get(this.songNum).setChanged();
        }            
    }
    
    /**
     * A method to decrease the volume of the song
     */
    public void decreaseVolume()
    {
         // Adjust the volume.
        this.volume -= volumeAdjustment;
        
        // Min volume.
        if (this.volume < -80.0f)
            this.volume = -80.0f;
        
        // Adjust the current playing song.
        if (this.musicPlayingInProgress == true)
        {
            this.songList.get(this.songNum).setVolume(this.volume);
            this.songList.get(this.songNum).setChanged();
        }            
    }
}

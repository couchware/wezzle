/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.audio;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.audio.AudioTrack;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * A class to handle background music.
 * @author Kevin
 */

public class MusicManager 
{
    /**
     * The minimum volume setting.
     */
    private final float MUSIC_MIN;
    
    /**
     * The maximum volume setting.
     */
    private final float MUSIC_MAX;
    
    /** 
     * How much to adjust the volume by.
     */
    private static final float VOLUME_STEP = 0.5f;
    
    /**
     * A link to the executor that the manager uses to play sounds.
     */
    private Executor executor;
    
    /** 
     * A link to the property manager. 
     */
    private PropertyManager propertyMan;
    
    /** 
     * The list of songs.
     */
    private ArrayList<AudioPlayer> musicList;
    
    /** 
     * The song number we are one 
     */
    private int trackNum;
    
    /** 
     * A variable to check if the song is paused.
     */
    private boolean paused = false;
    
    /**
     * Is the music playing?
     */
    private volatile boolean playing;
    
    /**
     * Is the music turned on?
     */
    private boolean enabled;
      
    /**
     * The volume level.     
     */    
    private float volume;        
    
    /**
     * Creates the song list.
     */
    public MusicManager(Executor executor, PropertyManager propertyMan) 
    {        
        // The executor.
        this.executor = executor;
        
        // The property manager.
        this.propertyMan = propertyMan;
        
        // Grab the minimum and maximum sound value from the property manager.
        MUSIC_MIN = propertyMan.getFloatProperty(
                PropertyManager.KEY_MUSIC_MIN);
        
        MUSIC_MAX = propertyMan.getFloatProperty(
                PropertyManager.KEY_MUSIC_MAX);
                        
        // Initiate the array list and song number.
        this.musicList = new ArrayList<AudioPlayer>();                     
       
        // Add some music.  This is the order they will play in, but it will
        // not necessarily start on the first song.
        this.musicList.add(new AudioPlayer(AudioTrack.MUSIC_TRON1, Game.MUSIC_PATH 
                + "/IntergalacticTron.ogg"));
        this.musicList.add(new AudioPlayer(AudioTrack.MUSIC_TRON2, Game.MUSIC_PATH 
                + "/IntergalacticTron2.ogg"));
        this.musicList.add(new AudioPlayer(AudioTrack.MUSIC_TRON3, Game.MUSIC_PATH 
                + "/IntergalacticTron3.ogg"));        
        
        // Randomly pick a starting song.
        //this.songNum = Util.random.nextInt(songList.size());
        this.trackNum = -1;        
        
         // The music is not currently playing.
        setPlaying(false);
        
        // The music starts out paused.
        setPaused(true);
        
        // Get the default volume.
        this.volume = propertyMan.getFloatProperty(
                PropertyManager.KEY_MUSIC_VOLUME);
        
        // Now see whether or not the music is enabled.
         // Run the music if it's enabled.
        if (propertyMan.getStringProperty(PropertyManager.KEY_MUSIC)
                .equals(PropertyManager.VALUE_ON))
        {     
            setPaused(false);
            playNext();
        }        
    }
    
    /**
     * A method to add a new song to the player.
     * 
     * @param newSong The new song.
     */
    public void add(AudioPlayer song)
    {
        this.musicList.add(song);
    }
        
    /**
     * A method to remove a song by it's key value.
     * Note: this method does not set the song to null.
     * 
     * @param key The key of the associated song.
     * @return true if the song was removed, false otherwise.
     */
    public boolean remove(final AudioTrack type)
    {
        // Find and remove the song.
        for (int i = 0; i < musicList.size(); i++)
        {
            if (musicList.get(i).getTrack() == type)
            {
                musicList.remove(i); 
                return true;
            }           
        }
        
        return false;
    }
    
    /**
     * Return a reference to the song with the associated key.
     * Note: This method does not remove the song from the list.
     * Note: Returns null if the key was not found.
     * 
     * @param key The associated key.
     * @return The song or null if the key was not found.
     */
    public AudioPlayer get(final AudioTrack type)
    {
         // find and return the song.
        for (int i = 0; i < musicList.size(); i++)
        {
            if (musicList.get(i).getTrack() == type)
            {
                return musicList.get(i); 
            }
        }
        
        return null;
    }
    
    /**
     * A method to play a specific song identified by it's key.
     * 
     * @param key The key of the associated song.
     */
    public void playSong(final AudioTrack type)
    {
        // Flag music playing.        
        setPlaying(true);
        
        // Play the song in the background.
        executor.execute(new Runnable()
        {            
            public void run() 
            {
                try 
                { 
                    // Play the song.
                    for (int i = 0; i < musicList.size(); i++)
                    {
                        if (musicList.get(i).getTrack() == type)
                        {
                            // Adjust the track number.
                            trackNum = i;
                            
                            // Set the volume.
                            musicList.get(trackNum).setVolume(volume);
                            
                            // Play the song.
                            musicList.get(trackNum).play(); 
                            break;
                        }
                        
                    }
                    // Signal song is done.
                    setPlaying(false);
                }
                catch (Exception e) 
                { 
                    LogManager.handleException(e); 
                }
            }
        });
    }
   
    /**
     * A method to cycle through the list of songs. One at a time.
     */
    public void playNext() 
    {
        // Flag music playing in game.
        setPlaying(true);
        
        // Determine the next song to play.
        this.trackNum = (this.trackNum + 1) % this.musicList.size();
        
        // Grab that song.
        final AudioPlayer song = musicList.get(trackNum);
        
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
                    LogManager.handleException(e); 
                }
            }
        }.start();
    }

    /**
     * Is the music playing? That is, is a track loaded? It may be paused.
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
        
        if (trackNum != -1)
            this.musicList.get(trackNum).setPaused(paused);                 
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
    public synchronized void increaseVolume()
    {
        // Adjust the volume.
        float vol = volume + VOLUME_STEP;
        
        // Max volume.
        if (vol > MUSIC_MAX)
            vol = MUSIC_MIN;                   
        
        // Set it.
        setVolume(vol);
    }
    
    /**
     * A method to decrease the volume of the song
     */
    public synchronized void decreaseVolume()
    {
        // Adjust the volume.
        float vol = volume - VOLUME_STEP;
        
        // Min volume.
        if (vol < MUSIC_MIN)
            vol = MUSIC_MIN;
                
        // Set it.
        setVolume(vol);          
    }
    
    public float getVolume()
    {
        return volume;
    }

    public void setVolume(float volume)
    {
        // Adjust the property;
        propertyMan.setProperty(PropertyManager.KEY_MUSIC_VOLUME, 
                Float.toString(volume));
        
        // Adjust the current playing song.
        if (isPlaying() == true)
        {
            if (trackNum != -1)
                this.musicList.get(this.trackNum).setVolume(volume);
            else
                throw new IllegalStateException(
                        "Track number is -1 (no track) yet playing flag is set");
        }   
        
        this.volume = volume;
    }
    
    public void updateLogic(Game game)
    {
        if (isPlaying() == false && isPaused() == false)
            playNext();
    }
    
}

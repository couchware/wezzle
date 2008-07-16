/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.audio;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * A class for managing the playing of game sounds.
 * Sounds are played by calling the Play() method and passing in
 * an integer. 
 * 
 * The class also defines the constants BOMB, LINE, BLEEP, CLICK for the
 * sounds that are available.
 * 
 * @author Kevin, Cameron
 */

public class SoundManager 
{
    /**
     * The minimum volume setting.
     */
    private final float SOUND_MIN;
    
    /**
     * The maximum volume setting.
     */
    private final float SOUND_MAX;
    
    /** 
     * How much to adjust the volume by.
     */
    private final float VOLUME_STEP = 0.5f;       
    
    /** 
     * The number of buffers for the effect. 
     */
    private static int NUM_BUFFERS = 4;
      
    /**
     * A link to the executor that the manager uses to play sounds.
     */
    private Executor executor;
    
    /** 
     * A link to the property manager. 
     */
    private PropertyManager propertyMan;
    
    /** 
     * The list of effects.
     */   
    private ArrayList<AudioPlayer[]> soundList;
    
    /**
     * The current buffer.
     */
    private ArrayList<Integer> bufferPointerList;
   
    /** 
     * Determine if the sound is on or off.
     */
    private boolean paused;
      
    /**
     * The volume level.
     * range: -80.0 - 6.0206
     */    
    private float volume;       
    
    /**
     * Creates the effect list.
     */
    public SoundManager(Executor executor, PropertyManager propertyMan) 
    {        
        // The executor.
        this.executor = executor;
        
        // The property manager.
        this.propertyMan = propertyMan;
        
        // Grab the minimum and maximum sound value from the property manager.
        SOUND_MIN = propertyMan.getFloatProperty(
                PropertyManager.KEY_SOUND_MIN);
        
        SOUND_MAX = propertyMan.getFloatProperty(
                PropertyManager.KEY_SOUND_MAX);
        
        // Initiate the array list.
        this.soundList = new ArrayList<AudioPlayer[]>(); 
        this.bufferPointerList = new ArrayList<Integer>();
        
        // Add some Sound effects. MUST USE addsound effect as it 
        // handles buffering.
        this.add(AudioTrack.SOUND_LINE,
                Game.SOUNDS_PATH + "/SoundLine.wav");
        
        this.add(AudioTrack.SOUND_BOMB,
                Game.SOUNDS_PATH + "/SoundExplosion.wav");
        
        this.add(AudioTrack.SOUND_BLEEP,
                Game.SOUNDS_PATH + "/SoundBleep.wav");
        
        this.add(AudioTrack.SOUND_CLICK,
                Game.SOUNDS_PATH + "/SoundClick.wav");
        
        this.add(AudioTrack.SOUND_LEVEL_UP,
                Game.SOUNDS_PATH + "/SoundLevelUp.wav");
        
        this.add(AudioTrack.SOUND_STAR,
                Game.SOUNDS_PATH + "/SoundDing.wav");
        
        this.add(AudioTrack.SOUND_ROCKET,
                Game.SOUNDS_PATH + "/SoundRocket.wav");
             
        // Get the default volume.
        setVolume(propertyMan.getFloatProperty(
                PropertyManager.KEY_SOUND_VOLUME));
        
        // Check if paused or not.
        if (propertyMan.getStringProperty(PropertyManager.KEY_SOUND)
                .equals(PropertyManager.VALUE_ON))
        {
            setPaused(false);
        }
        else
        {
            setPaused(true);
        }
    }
    
    /**
     * A method to add a new effect to the player.
     * 
     * @param effect The new effect.
     */
    public void add(AudioTrack track, String path)
    {
        AudioPlayer sounds[] = new AudioPlayer[NUM_BUFFERS];
        for (int i = 0; i < sounds.length; i++)
            sounds[i] = new AudioPlayer(track, path);
        
        // Add the effect.
        this.soundList.add(sounds);
        
        // Add the corresponding buffer pointer.
        this.bufferPointerList.add(new Integer(0));
    }
        
    /**
     * A method to remove an effect by it's key value.
     * Note: this method does not set the effect to null.
     * 
     * @param key The key of the effect to remove.
     * @return True if the effect was removed, false otherwise.
     */
    public boolean remove(final AudioTrack track)
    {
        // Find and remove the effect.        
        for (int i = 0; i < soundList.size(); i++)
        {
            if (soundList.get(i)[0].getTrack() == track)
            {
                // Remove the effect and its buffer num list.
                soundList.remove(i); 
                bufferPointerList.remove(i);
                return true;
            }           
        }
                        
        return false;
    }
    
    /**
     * Return a reference to the effect with the associated key.
     * Note: This method does not remove the effect from the list.
     * Note: returns null if the key was not found.
     * Note: returns the sound effect of the proper buffer.
     * 
     * @param key The associated key.
     * @return The effect or null if the key was not found.
     */
    public AudioPlayer get(final AudioTrack track)
    {        
        // Find and return the effect.
        for (int i = 0; i < soundList.size(); i++)
        {
            if (soundList.get(i)[0].getTrack() == track)
            {
                // The current buffer.
                int bufferNum = bufferPointerList.get(i);

                // The next buffer
                int nextBufferNum = (bufferNum + 1) % NUM_BUFFERS;

                // Set the next buffer to be used.
                bufferPointerList.set(i, new Integer(nextBufferNum));

                // Return the proper buffered effect.
                return soundList.get(i)[bufferNum]; 
            }
        }
                
        return null;
    }
    
    /**
     * A method to play a specific effect identified by it's key.
     * 
     * @param key The key of the associated effect.
     */
    public void play(final AudioTrack track)
    {
        // If paused, don't play.
        if (isPaused() == true)
            return;
        
        // Get the sound effect to play.
        final AudioPlayer player = get(track);
        
        // Play the effect in the background.
        executor.execute(new Runnable()
        {            
            public void run() 
            {
                try 
                { 
                    // Play the sound.
                    player.setVolume(volume);
                    player.play();
                }
                catch (Exception e) 
                { 
                    Util.handleException(e); 
                }
            }
        });
    }     
        
    public float getVolume()
    {
        return volume;
    }

    public void setVolume(float volume)
    {
        // Adjust the property;
        propertyMan.setProperty(PropertyManager.KEY_SOUND_VOLUME, 
                Float.toString(volume));
        
        this.volume = volume;
    }        
    
    /** 
     * A method to increase the volume of the sound.
     */
    public void increaseVolume()
    {
        // Adjust the volume.
        float vol = this.volume + VOLUME_STEP;
        
        // Max volume.
        if (vol > SOUND_MAX)
            vol = SOUND_MAX;
        
        // Set it.     
        setVolume(vol);
    }
    
    /**
     * A method to decrease the volume of the effect
     */
    public void decreaseVolume()
    {
        // Adjust the volume.
        float vol = this.volume - VOLUME_STEP;
        
        // Min volume.
        if (vol < SOUND_MIN)
            vol = SOUND_MIN;
        
        // Set it.
        setVolume(vol);                
    }

    public boolean isPaused()
    {
        return paused;
    }        
    
    /**
     * Toggle the paused variable
     * @param paused whether or not to pause.
     */
    public void setPaused(boolean paused)
    {
        this.paused = paused;
    }    
    
}




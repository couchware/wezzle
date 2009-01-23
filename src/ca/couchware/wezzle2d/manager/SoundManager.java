/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.audio.SoundPlayer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.util.CouchLogger;
import java.util.ArrayList;
import java.util.List;
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
     * The number of buffers for the effect. 
     */
    private static int NUM_BUFFERS = 4;
      
    /**
     * A link to the executor that the manager uses to play sounds.
     */
    private Executor executor;   
    
    /**
     * The settings manager.
     */
    private SettingsManager settingsMan;
    
    /** 
     * The list of effects.
     */   
    private List<List<SoundPlayer>> soundList;
    
    /**
     * The current buffer.
     */
    private List<Integer> bufferPointerList;
   
    /** 
     * Determine if the sound is on or off.
     */
    private boolean paused;
      
    /**
     * The normalized gain.
     */
    private double normalizedGain = 0.0;
    
    /**
     * Creates the effect list.
     * 
     * @param executor
     * @param propertyMan
     */
    private SoundManager(Executor executor, SettingsManager settingsMan) 
    {        
        // The executor.
        this.executor = executor;  
        this.settingsMan = settingsMan;
        
        // Initiate the array list.
        this.soundList = new ArrayList<List<SoundPlayer>>(); 
        this.bufferPointerList = new ArrayList<Integer>();
        
        // Add some Sound effects. MUST USE addsound effect as it 
        // handles buffering.
        String path = Settings.getSoundResourcesPath();
        
        // Create the sound objects.
        for (Sound sound : Sound.values())
        {
            this.create(sound, path + "/" + sound.toString() + ".wav");
        }
        
//        this.create(Sound.LINE,
//                path + "/SoundLine.wav");
//        
//        this.create(Sound.BOMB,
//                path + "/SoundExplosion.wav");
//        
//        this.create(Sound.BLEEP,
//                path + "/SoundBleep.wav");
//        
//        this.create(Sound.CLICK,
//                path + "/SoundClick.wav");
//        
//        this.create(Sound.LEVEL_UP,
//                path + "/SoundLevelUp.wav");
//        
//        this.create(Sound.STAR,
//                path + "/SoundDing.wav");
//        
//        this.create(Sound.ROCKET,
//                path + "/SoundRocket.wav");
             
        // Get the default volume.
        setNormalizedGain((double) settingsMan.getInt(Key.USER_SOUND_VOLUME) / 100.0);
        
        // Check if on or off.
        if (SettingsManager.get().getBoolean(Key.USER_SOUND) == true)
        {
            setPaused(false);
        }
        else
        {
            setPaused(true);
        }
    }
    
    /**
     * Static constructor.
     * 
     * @param executor
     * @param userProperties
     * @return
     */
    public static SoundManager newInstance(Executor executor, SettingsManager settingsMan)
    {
        return new SoundManager(executor, settingsMan);
    }
    
    /**
     * A method to add a new effect to the player.
     * 
     * @param effect The new effect.
     */
    public void create(Sound track, String path)
    {
        List<SoundPlayer> buffer = new ArrayList<SoundPlayer>(NUM_BUFFERS);
        for (int i = 0; i < NUM_BUFFERS; i++)
            buffer.add(new SoundPlayer(track, path, true));
        
        // Add the effect.
        this.soundList.add(buffer);
        
        // Add the corresponding buffer pointer.
        this.bufferPointerList.add(0);
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
    public SoundPlayer get(final Sound track)
    {        
        // Find and return the effect.
        for (int i = 0; i < soundList.size(); i++)
        {
            if (soundList.get(i).get(0).getTrack() == track)
            {
                // The current buffer.
                int bufferNum = bufferPointerList.get(i);

                // The next buffer
                int nextBufferNum = (bufferNum + 1) % NUM_BUFFERS;

                // Set the next buffer to be used.
                bufferPointerList.set(i, new Integer(nextBufferNum));

                // Return the proper buffered effect.
                return soundList.get(i).get(bufferNum); 
            }
        }
                
        return null;
    }
    
    /**
     * A method to play a specific effect identified by it's key.
     * 
     * @param key The key of the associated effect.
     */
    public void play(final Sound track)
    {
        // If paused, don't play.
        if (this.paused == true)
            return;
        
        // Get the sound effect to play.
        final SoundPlayer player = get(track);
        
        // Play the effect in the background.
        executor.execute(new Runnable()
        {            
            public void run() 
            {
                try 
                { 
                    // Play the sound.       
                    player.setNormalizedGain(normalizedGain);
                    player.play();
                    
                }
                catch (Exception e) 
                { 
                    CouchLogger.get().recordException(this.getClass(), e);
                }
            }
        });
    }     
        
    public double getNormalizedGain()
    {
        return normalizedGain;
    }

    public void setNormalizedGain(double nGain)
    {
        // Make sure it's between 0.0 and 1.0.
        if (nGain < 0.0) nGain = 0.0;
        else if (nGain > 1.0) nGain = 1.0;
        
        // Adjust the property;
        settingsMan.setInt(Key.USER_SOUND_VOLUME, (int) (nGain * 100));
        
        // Remember it.
        this.normalizedGain = nGain;                
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
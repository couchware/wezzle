/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
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

public class SoundManager implements IResettable
{               
      
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

    private final Sound[] appletSounds =
    {
        Sound.BLEEP,
        Sound.CLICK,
        Sound.CLICK_LIGHT,
        Sound.LEVEL_UP,
        Sound.LINE_1,
        Sound.LINE_2,
        Sound.LINE_3,
        Sound.LINE_4,
        Sound.LINE_5,
        Sound.LINE_6,
        Sound.LINE_7,
        Sound.ROCKET
    };

    private final Sound[] desktopSounds = Sound.values();
    
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
        Sound[] sounds = Game.isApplet() ? appletSounds : desktopSounds;
        for (Sound sound : sounds)
        {
            this.create(sound, path + "/" + sound.toString() + ".wav");
        }
                             
        resetState();
    }
    
    /**
     * Static constructor.
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
     * @param effect The new effect.
     */
    public void create(Sound track, String path)
    {
        final int numBuffers = track.getNumberOfBuffers();
        List<SoundPlayer> buffer = new ArrayList<SoundPlayer>(numBuffers);
        for (int i = 0; i < numBuffers; i++)
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
     * @param key The associated key.
     * @return The effect.
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
                int nextBufferNum = (bufferNum + 1) % track.getNumberOfBuffers();

                // Set the next buffer to be used.
                bufferPointerList.set(i, new Integer(nextBufferNum));

                // Return the proper buffered effect.
                return soundList.get(i).get(bufferNum); 
            }
        }
                
        throw new RuntimeException("Could not find sound: " + track);
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
                    CouchLogger.get().recordException(this.getClass(), e, true /* Fatal */);
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

    public void exportSettings()
    {
        // Write the music volume.
        int intGain = (int) (normalizedGain * 100.0);
        settingsMan.setInt(Key.USER_SOUND_VOLUME, intGain);
    }

    public final void importSettings()
    {
        // Read the music volume.
        final double nGain =
                (double) settingsMan.getInt(Key.USER_SOUND_VOLUME) / 100.0;

        setNormalizedGain(nGain);
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

    public final void resetState()
    {        
        setNormalizedGain((double) settingsMan.getInt(Key.USER_SOUND_VOLUME) / 100.0);        
        setPaused( !settingsMan.getBool(Key.USER_SOUND) );
    }
    
}
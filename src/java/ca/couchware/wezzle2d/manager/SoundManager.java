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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import paulscode.sound.SoundSystem;

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
    private SettingsManager settingsMan;
    private final SoundSystem soundSystem;
    
    /** 
     * The list of effects.
     */   
    private Map<Sound, List<SoundPlayer>> soundMap;
    
    /**
     * The current buffer.
     */
    private Map<Sound, Integer> bufferPointerMap;
   
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
        Sound.BOMB,
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
        Sound.ROCKET,
        Sound.STAR
    };

    private final Sound[] desktopSounds = Sound.values();
    
    /**
     * Creates the effect list.
     * 
     * @param executor
     * @param propertyMan
     */
    private SoundManager(SoundSystem soundSystem, SettingsManager settingsMan)
    {
        this.soundSystem = soundSystem;
        this.settingsMan = settingsMan;
        
        // Initiate the array list.
        this.soundMap = new EnumMap<Sound, List<SoundPlayer>>(Sound.class);
        this.bufferPointerMap = new EnumMap<Sound, Integer>(Sound.class);
        
        // Add some Sound effects. MUST USE addsound effect as it 
        // handles buffering.
        final String path = Settings.getSoundResourcesPath();
        final Sound[] sounds = Game.isApplet() ? appletSounds : desktopSounds;
        for (Sound sound : sounds)
        {
            create(sound, path + "/" + sound.toString() + ".wav");
        }
                             
        resetState();
    }
    
    /**
     * Static constructor.
     * @param executor
     * @param userProperties
     * @return
     */
    public static SoundManager newInstance(SoundSystem soundSystem, SettingsManager settingsMan)
    {
        return new SoundManager(soundSystem, settingsMan);
    }
    
    /**
     * A method to add a new effect to the player.
     * @param effect The new effect.
     */
    final private void create(Sound sound, String path)
    {
        final int numBuffers = sound.getNumberOfBuffers();
        final List<SoundPlayer> buffer = new ArrayList<SoundPlayer>(numBuffers);

        for (int i = 0; i < numBuffers; i++)
        {
            buffer.add(new SoundPlayer(soundSystem, path));
        }
        
        soundMap.put(sound, buffer);
        bufferPointerMap.put(sound, 0);
    }
        
    /**
     * Return a reference to the effect with the associated key.
     *
     * @param key The associated key.
     * @return The effect.
     */
    public SoundPlayer getPlayer(final Sound sound)
    {               
        // The current buffer.
        int bufferPointer = bufferPointerMap.get(sound);

        // The next buffer
        int nextBufferPointer = (bufferPointer + 1) % sound.getNumberOfBuffers();

        // Set the next buffer to be used.
        bufferPointerMap.put(sound, nextBufferPointer);

        // Return the proper buffered effect.
        return soundMap.get(sound).get(bufferPointer);
    }
    
    /**
     * A method to play a specific effect identified by it's key.
     * 
     * @param key The key of the associated effect.
     */
    public void play(final Sound sound)
    {
        // If paused, don't play.
        if (this.paused == true)
            return;
        
        // Get the sound effect to play.
        final SoundPlayer player = getPlayer(sound);

        // Play the sound.
        player.setNormalizedGain(normalizedGain);
        player.play();        
    }     

    public void stopAll()
    {
        for (Sound sound : soundMap.keySet())
        {
            for (SoundPlayer player : soundMap.get(sound))
            {
                player.setNormalizedGain(0.0);
                player.close();
            } // end for
        } // end for
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
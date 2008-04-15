package ca.couchware.wezzle2d.sound;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;

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
    // The keys.
    public final static String KEY_BOMB = "bomb";
    public final static String KEY_LINE = "line";
    public final static String KEY_BLEEP = "bleep";
    public final static String KEY_CLICK = "click";
    public final static String KEY_LEVEL_UP = "levelUp";
    
    /** 
     * The number of buffers for the effect. 
     */
    private static int NUM_BUFFERS = 4;
      
    /** 
     * A link to the property manager. 
     */
    private PropertyManager propertyMan;
    
    /** 
     * The list of effects.
     */   
    private ArrayList<SoundEffect[]> effectsList;
    
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
     * How much to adjust the volume by.
     */
    private static final float VOLUME_STEP = 0.5f;
    
    /**
     * Creates the effect list.
     */
    public SoundManager(PropertyManager propertyMan) 
    {        
        // The property manager.
        this.propertyMan= propertyMan;
        
        // Initiate the array list.
        this.effectsList = new ArrayList<SoundEffect[]>(); 
        this.bufferPointerList = new ArrayList<Integer>();
        
        // Add some Sound effects. MUST USE addsound effect as it 
        // handles buffering.
        this.addSoundEffect(SoundManager.KEY_LINE,
               Game.SOUNDS_PATH + "/SoundLine.wav");
        
        this.addSoundEffect(SoundManager.KEY_BOMB,
                Game.SOUNDS_PATH + "/SoundExplosion.wav");
        
        this.addSoundEffect(SoundManager.KEY_BLEEP,
                Game.SOUNDS_PATH + "/SoundBleep.wav");
        
        this.addSoundEffect(SoundManager.KEY_CLICK,
                Game.SOUNDS_PATH + "/SoundClick.wav");
        
        this.addSoundEffect(SoundManager.KEY_LEVEL_UP,
                Game.SOUNDS_PATH + "/SoundLevelUp.wav");
             
        // Get the default volume.
        this.volume = propertyMan.getFloatProperty(PropertyManager.KEY_SOUND_VOLUME);
        
        // Check if paused or not.
        if (propertyMan.getStringProperty(PropertyManager.KEY_SOUND)
                .equals(PropertyManager.VALUE_OFF))
        {
            this.paused = true;
        }
        else
        {
            this.paused = false;
        }
    }
    
    /**
     * A method to add a new effect to the player.
     * 
     * @param effect The new effect.
     */
    public void addSoundEffect(String key, String path)
    {
        SoundEffect effects[] = new SoundEffect[NUM_BUFFERS];
        for (int i = 0; i < effects.length; i++)
            effects[i] = new SoundEffect(key, path);
        
        // Add the effect.
        this.effectsList.add(effects);
        
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
    public boolean removeSoundEffect (final String key)
    {
        // Find and remove the effect.        
        for (int i = 0; i < effectsList.size(); i++)
        {
            if (effectsList.get(i)[0].getKey().equals(key) == true)
            {
                // Remove the effect and its buffer num list.
                effectsList.remove(i); 
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
    public SoundEffect getSoundEffect(final String key)
    {        
        // Find and return the effect.
        for (int i = 0; i < effectsList.size(); i++)
        {
            if (effectsList.get(i)[0].getKey().equals(key) == true)
            {
                // The current buffer.
                int bufferNum = bufferPointerList.get(i);

                // The next buffer
                int nextBufferNum = (bufferNum + 1) % NUM_BUFFERS;

                // Set the next buffer to be used.
                bufferPointerList.set(i, new Integer(nextBufferNum));

                // Return the proper buffered effect.
                return effectsList.get(i)[bufferNum]; 
            }
        }
                
        return null;
    }
    
    /**
     * A method to play a specific effect identified by it's key.
     * 
     * @param key The key of the associated effect.
     */
    public void playSoundEffect(final String key)
    {
        // If paused, don't play.
        if (this.paused == true)
            return;
        
        // Get the sound effect to play.
        final SoundEffect effect = getSoundEffect(key);
        
        // Play the effect in the background.
        new Thread() 
        {
            @Override
            public void run() 
            {
                try 
                { 
                    // Play the effect. MUST USE get soundeffect as
                    // it handles buffering.
                    effect.setVolume(volume);
                    effect.play();
                }
                catch (Exception e) 
                { 
                    Util.handleException(e); 
                }
            }
        }.start();
    }     
    
    /** 
     * A method to increase the volume of the sound.
     */
    public void increaseVolume()
    {
        // Adjust the volume.
        this.volume += VOLUME_STEP;
        
        // Max volume.
        if (this.volume > 6.0206f)
            this.volume = 6.0206f;
        
        // Adjust the property;
        propertyMan.setProperty(PropertyManager.KEY_SOUND_VOLUME, Float.toString(this.volume));        
    }
    
    /**
     * A method to decrease the volume of the effect
     */
    public void decreaseVolume()
    {
        // Adjust the volume.
        this.volume -= VOLUME_STEP;
        
        // Min volume.
        if (this.volume < -80.0f)
            this.volume = -80.0f;
        
        // Adjust the property;
        propertyMan.setProperty(PropertyManager.KEY_MUSIC_VOLUME, 
                Float.toString(this.volume));
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




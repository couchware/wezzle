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
 * @author Kevin
 */


public class SoundManager 
{
    // The keys.
    public final static String KEY_BOMB = "bomb";
    public final static String KEY_LINE = "line";
    public final static String KEY_BLEEP = "bleep";
    public final static String KEY_CLICK = "click";
    public final static String KEY_LEVEL_UP = "level up";
    
    /** The number of buffers for the effect. */
    private static int numBuffers = 4;
    
  
    /** A link to the property manager. */
    private PropertyManager propMan;
    
    /** The list of effects */
    private ArrayList<SoundEffect[]> effectList;
    private ArrayList<Integer> bufferNumList;
   
    /** Determine if the sound is on or off */
    private boolean paused;
      
    /**
     * The volume level.
     * range: -80.0 - 6.0206
     */    
    private float volume;
    
    /** How much to adjust the volume by */
    private static final float volumeAdjustment = 0.5f;
    
    /**
     * Creates the effect list.
     */
    public SoundManager(PropertyManager propMan) 
    {        
        // The property manager.
        this.propMan= propMan;
        
        // Initiate the array list.
        this.effectList = new ArrayList<SoundEffect[]>(); 
        this.bufferNumList = new ArrayList<Integer>();
        
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
        this.volume = propMan.getFloatProperty(PropertyManager.KEY_SOUND_VOLUME);
        
        // Paused or not.
        String check = propMan.getStringProperty(PropertyManager.KEY_SOUND);
        
        if(check.equals(PropertyManager.VALUE_OFF))
            this.paused = true;
        else
            this.paused = false;
    }
    
    /**
     * A method to add a new effect to the player.
     * 
     * @param effect The new effect.
     */
    public void addSoundEffect(String key, String path)
    {
        SoundEffect effects[] = new SoundEffect[numBuffers];
        for(int i = 0; i < effects.length; i++)
            effects[i] = new SoundEffect(key, path);
        
        // Add the effect.
        this.effectList.add(effects);
        
        // Add the corresponding buffer.
        this.bufferNumList.add(new Integer(0));
    }
        
    /**
     * A method to remove an effect by it's key value.
     * Note: this method does not set the effect to null.
     * 
     * @param key The key of the associated effect.
     * @return true if the effect was removed, false otherwise.
     */
    public boolean removeEffect (final String key)
    {
        // Find and remove the effect.
        for (int i = 0; i < effectList.size(); i++)
        {
            if (effectList.get(i)[0].getKey().equals(key) == true)
            {
                // Remove the effect and its buffer num list.
                effectList.remove(i); 
                bufferNumList.remove(i);
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
        synchronized(this)
        {
             // find and return the effect.
            for (int i = 0; i < effectList.size(); i++)
            {
                if (effectList.get(i)[0].getKey().equals(key) == true)
                {
                    // The current buffer.
                    int bufferNum = bufferNumList.get(i);

                    // The next buffer
                    int nextBufNum = (bufferNum + 1) % numBuffers;
                    
                    // Set the next buffer to be used.
                    bufferNumList.set(i, new Integer(nextBufNum));

                    // Return the proper buffered effect.
                    return effectList.get(i)[bufferNum]; 
                }
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
        // If paused, dont play.
        if(this.paused)
            return;
        
        // Play the effect in the background.
        new Thread() 
        {
            @Override
            public void run() 
            {
                try 
                { 
                    synchronized(this)
                    {
                        // Play the effect. MUST USED get soundeffect as
                        // it handles buffering.
                         getSoundEffect(key).play();
                    }
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
        this.volume += volumeAdjustment;
        
        // Max volume.
        if (this.volume > 6.0206f)
            this.volume = 6.0206f;
        
        // Adjust the property;
        propMan.setProperty(PropertyManager.KEY_SOUND_VOLUME, Float.toString(this.volume));        
    }
    
    /**
     * A method to decrease the volume of the effect
     */
    public void decreaseVolume()
    {
         // Adjust the volume.
        this.volume -= volumeAdjustment;
        
        // Min volume.
        if (this.volume < -80.0f)
            this.volume = -80.0f;
        
         // Adjust the property;
        propMan.setProperty(PropertyManager.KEY_MUSIC_VOLUME, Float.toString(this.volume));
        
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




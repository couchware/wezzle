/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.audio;

import ca.couchware.wezzle2d.util.CouchLogger;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * A class to hold an audio file.  This is used by both the sound and music
 * managers. 
 * 
 * @author Kevin, Cameron
 */

public class SoundPlayer
{    
    private Audio clip;
    //private FloatControl gainControl;
    private double normalizedGain;          

    /**
     * The constructor.
     * 
     * @param key
     * @param path     
     */
    public SoundPlayer(String path)
    {               
        InputStream stream = SoundPlayer.class.getClassLoader().getResourceAsStream(path);
        BufferedInputStream bin = new BufferedInputStream(stream);
        open(bin);
    }               
    
    public void play()
    {       
        // Play clip from the start.
       clip.playAsSoundEffect(1.0f, (float) this.getNormalizedGain(), false);
    }
       
    /**
     * A method to load/rest an audio file.      
     */
    private void open(BufferedInputStream stream)
    {                                   
        // Create the Audio Stream.
        try
        {
            
            clip = AudioLoader.getAudio("WAV", stream);
   
        }
        catch (Exception e)
        {
            CouchLogger.get().recordException(this.getClass(), e, true /* Fatal */);
        }
 
    }

    public void close()
    {
        //clip.drain();
        clip.stop();
    }
           
    /**
     * Sets gain (i.e. volume) value.     
     * 
     * Linear scale 0.0 - 1.0.
     * Threshold Coefficent : 1/2 to avoid saturation.
     * 
     * @param fGain The gain to set.     
     */
    public void setNormalizedGain(double nGain)
    {        
        //double minGainDB = 0.0f;//gainControl.getMinimum();
        //double ampGainDB = ((10.0f / 20.0f) * 1.0f) - 0.0f;//gainControl.getMaximum()) - gainControl.getMinimum();
        //double cste = Math.log(10.0) / 20;
        //double valueDB = minGainDB + (1 / cste) * Math.log(1 + (Math.exp(cste * ampGainDB) - 1) * nGain);
        //gainControl.setValue((float) valueDB);
        
        // Remember the normalized gain.
        this.normalizedGain = nGain;
    }
    
    /**
     * Gets the gain (i.e. volume) value.
     * 
     * @return
     */
    public double getNormalizedGain()
    {
        return normalizedGain;
    }
    
}

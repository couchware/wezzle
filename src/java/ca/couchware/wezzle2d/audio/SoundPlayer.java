/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.audio;

import ca.couchware.wezzle2d.util.CouchLogger;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * A class to hold an audio file.  This is used by both the sound and music
 * managers. 
 * 
 * @author Kevin, Cameron
 */

public class SoundPlayer
{    
    private Clip clip;         
    private FloatControl gainControl;
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
        open(stream);
    }               
    
    public void play()
    {       
        // Play clip from the start.
        clip.setFramePosition(0);
        clip.loop(0);
    }
       
    /**
     * A method to load/rest an audio file.      
     */
    private void open(InputStream stream)
    {                                   
        // Create the Audio Stream.
        AudioInputStream in = null;
        try
        {
            in = AudioSystem.getAudioInputStream(new BufferedInputStream(stream));

            // The base audio format.
            AudioFormat baseFormat = in.getFormat();

            // The decoded input stream.
            AudioInputStream decodedIn = AudioSystem.getAudioInputStream(baseFormat, in);

            clip = AudioSystem.getClip();
            clip.open(decodedIn);
            
            this.gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        }
        catch (Exception e)
        {
            CouchLogger.get().recordException(this.getClass(), e, true /* Fatal */);
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                CouchLogger.get().recordException(this.getClass(), e, true /* Fatal */);
            }
        } // end try
    }

    public void close()
    {
        clip.drain();
        clip.stop();
        clip.close();
    }

    public void quit()
    {
        clip.stop();
        clip.close();
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
        double minGainDB = gainControl.getMinimum();
        double ampGainDB = ((10.0f / 20.0f) * gainControl.getMaximum()) - gainControl.getMinimum();
        double cste = Math.log(10.0) / 20;
        double valueDB = minGainDB + (1 / cste) * Math.log(1 + (Math.exp(cste * ampGainDB) - 1) * nGain);
        gainControl.setValue((float) valueDB);        
        
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

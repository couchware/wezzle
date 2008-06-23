package ca.couchware.wezzle2d.sound;

import ca.couchware.wezzle2d.util.Util;
import java.net.URL;
import java.io.*;
import javax.sound.sampled.*;

/**
 * A class to hold a Effect that will be payed by the music manager.
 * a Effect consists of a key and a path to the associated mp3.
 * 
 * Based on code from http://www.javalobby.org/java/forums/t18465.html
 * 
 * @author Kevin
 */

public class SoundEffect 
{
    /** 
     * The key identifer.
     */
    private int key;
     
    /** 
     * A line of sound.
     */
    SourceDataLine line;
    
    /** 
     * The decoded audio format 
     */
    AudioFormat format;
    
    /** 
     * The audio input stream from the file.
     */
    AudioInputStream in;

    /** 
     * The url for the file. 
     */
    URL url;
             
    /** 
     * The volume control.
     */
    FloatControl volumeControl = null;

    /** 
     * The volume.
     */
    private float currentVolume;
    
    /**
     * A variable indicating whether or not the effect is currently playing.
     */
    private boolean playing;

    /**
     * The constructor.
     * 
     * @param key
     * @param path
     */
    public SoundEffect(int key, String path)
    {
        // The associated key.
        this.key = key;
        
        // Load the reserouce.
        url = this.getClass().getClassLoader()
            .getResource(path);
        
        // Check the URL.
        if (url == null)
            throw new RuntimeException("Url Error: " + path 
                    + " does not exist.");
        
        // The effect is not currently playing.
        setPlaying(false);
        
        // Load the effect.
        loadEffect();                  
    }
    
    /**
     * Play the Effect
     * 
     * The method creates an audio stream and plays through it a line of
     * data at a time.
     * 
     * Note: The first line is already initialized when we get to this function.
     * 
     */
    public void play()
    {        
        // Don't play if we're already playing.
        synchronized (this)
        {
            if (playing == true)            
                return;
            else
                playing = true;
        }
                
        try
        {            
            if (line != null) 
            {
				line.open(format);
				byte[] data = new byte[4096];
                
				// Start
				line.start();
                
				int nBytesRead;

                while ((nBytesRead = in.read(data, 0, data.length)) != -1) 
                {	
                    volumeControl = (FloatControl) line.getControl(
                            FloatControl.Type.MASTER_GAIN);
                    
                    volumeControl.setValue(getVolume());

                    if (!line.isRunning()) 
                    {
                        line.start();
                    }

                    line.write(data, 0, nBytesRead);
                }
                
				// The effect is done, close it.                
				line.drain();
				line.stop();
				line.close();
				in.close();                
                
                // The effect is done, have to reload it so we can play again.
                loadEffect();
                
                // We're done playing.
                setPlaying(false);
			}
        }
        catch(Exception e)
        {
           Util.handleException(e);   
        }
    }        

    /**
     * A method to reset the Effect.
     */
    private void loadEffect()
    {
        // Make sure everything is null.
        line = null;
        in = null;
        
        // Load the Effect.
        try
        {
            // Create the audio stream.
            in = AudioSystem.getAudioInputStream(url.openStream());

            // The base audio format.
            format = in.getFormat();
            
            // Set up a line of audio data from our decoded stream.
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);                        
        }
        catch(Exception e)
        {
            Util.handleException(e);
        }
    }
       
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
   
    /**
     * A method to set the volume.
     * 
     * @param volume The new volume value.
     */
    public synchronized void setVolume(float volume)
    {
        this.currentVolume = volume;     
    }
    
    /**
     * A method to get the volume of the effect.
     * 
     * @return the volume.
     */
    public synchronized float getVolume()
    {
       // Return the volume
        return this.currentVolume;
    }
   
    /**
     * The the sound effect key.
     * 
     * @return
     */
    public int getKey()
    {
        return this.key;
    }

    /**
     * Is the effect playing?
     * 
     * @return True if it is playing, false otherwise.
     */    
    public synchronized boolean isPlaying()
    {        
        return playing;
    }

    /**
     * Sets whether or not the effect is playing.
     * 
     * @param playing
     */
    private synchronized void setPlaying(boolean playing)
    {
        this.playing = playing;
    }    
    
}

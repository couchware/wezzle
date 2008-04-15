
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
    /** The Effect's key */
    private String key;
     
    /** A line of media */
    SourceDataLine line;
    
    /** The decoded audio format */
    AudioFormat format;
    
    /** The input stream from the file */
    AudioInputStream in;

    /** The url for the file */
     URL file;
             
    /** the volume control */
    FloatControl volume = null;

    /** the volume */
    private float currentVolume;

    /**
     * The constructor.
     * 
     * @param key
     * @param path
     */
    public SoundEffect(String key, String path)
    {
        // The associated key.
        this.key = key;
        
        // Load the reserouce.
        file = this.getClass().getClassLoader()
            .getResource(path);
        
        // Check the URL.
        if (file == null)
            throw new RuntimeException("Url Error: " + path 
                    + " does not exist.");
        
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
        try
        {
            
            if(line != null) {
				line.open(format);
				byte[] data = new byte[4096];
				// Start
				line.start();
                
				int nBytesRead;
                
                    while ((nBytesRead = in.read(data, 0, data.length)) != -1) 
                    {	
                        volume = (FloatControl) line.getControl
                                (FloatControl.Type.MASTER_GAIN);
                        volume.setValue(this.currentVolume);
                       
                        if(!line.isRunning()) 
                        {
                            line.start();
                        }
		
                        line.write(data, 0, nBytesRead);
                    }
                
				// The Effect is done, close it.
				line.drain();
				line.stop();
				line.close();
				in.close();
                
                // The Effect is done, have to reload it so we can play again.
                loadEffect();
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
    public void loadEffect()
    {
        // Make sure everything is null.
        line = null;
        in = null;
        
        // Load the Effect.
        try
        {
            // Create the Audio Stream.
            in = AudioSystem.getAudioInputStream(file.openStream());

            // The base audio format.
            format = in.getFormat();
            
            //Set up a line of audio data from our decoded stream.
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            
            
        }
        catch(Exception e)
        {
            Util.handleException(e);
        }
    }
       
    //--------------------------------------------------------------------------
    // Getters and setters.
    //--------------------------------------------------------------------------
   
    /**
     * A method to increase the volume.
     * @param volume The new volume value.
     */
    public void setVolume(float volume)
    {
        this.currentVolume = volume;     
    }
    
    /**
     * A method to get the volume of the Effect.
     * @return the volume.
     */
    public float getVolume()
    {
       // Return the volume
        return this.currentVolume;
    }
   
    public String getKey()
    {
        return this.key;
    }
    
}

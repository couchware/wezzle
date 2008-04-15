
package ca.couchware.wezzle2d.music;
import ca.couchware.wezzle2d.util.Util;
import java.net.URL;
import java.io.*;
import javax.sound.sampled.*;


/**
 * A class to hold a song that will be payed by the music manager.
 * a song consists of a key and a path to the associated mp3.
 * 
 * Based on code from http://www.javalobby.org/java/forums/t18465.html
 * 
 * @author Kevin
 */

public class Song 
{
    /** The song's key */
    private String key;
    
    /** A line of media */
    SourceDataLine line;
    
    /** The decoded audio format */
    AudioFormat decodedFormat;
    
    /** The input stream from the file */
    AudioInputStream in;
    
    /** The decoded input stream */
    AudioInputStream decodedIn;
    
    /** The url for the file */
    URL url;
             
    /** The lock */
    Object lock = new Object();
    
    /** some paused variable */
    volatile boolean paused = false;
    
    /** the volume control */
    FloatControl volume = null;
    
    /** The current volume */
    private float currentVolume;

    /**
     * The constructor.
     * 
     * @param key
     * @param path
     */
    public Song(String key, String path)
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
        
        loadSong();
         
        // Set the current volumne.
        this.currentVolume = 0.0f;                    
    }
    
    /**
     * Play the song
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
            if(line != null) 
            {
				line.open(decodedFormat);
				byte[] data = new byte[4096];
				
                // Start
				line.start();
                
				int nBytesRead;
                
                synchronized (lock)
                {
                    while ((nBytesRead = decodedIn.read(data, 0, data.length)) != -1) 
                    {	
                        // The volume control, must apply to every line of data.
                        volume = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                        volume.setValue(this.currentVolume);
                        
                        while (paused) 
                        {
                            if (line.isRunning()) 
                            {
                                line.stop();
                            }
                            try 
                            {
                                lock.wait();
                            }
                            catch(InterruptedException e) 
                            {
                               Util.handleException(e);
                            }
                        }
	
                        if(!line.isRunning()) 
                        {
                            line.start();
                        }
		
                        line.write(data, 0, nBytesRead);
                    }
                }
                
				// The song is done, close it.
				line.drain();
				line.stop();
				line.close();
				decodedIn.close();
                
                // The song is done, have to reload it so we can play again.
                loadSong();
			}
        }
        catch(Exception e)
        {
           Util.handleException(e);   
        }
    }        

    /**
     * A method to reset the song.
     */
    public void loadSong()
    {
        // Make sure everything is null.
        line = null;
        decodedFormat = null;
        in = null;
        decodedIn = null;
        
        // Load the song.
        try
        {
            // Create the Audio Stream.
            in = AudioSystem.getAudioInputStream(url.openStream());

            // The base audio format.
            AudioFormat baseFormat = in.getFormat();
            
            // decode the format so we can play it.
            decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED, // Encoding to use
                baseFormat.getSampleRate(),	  // sample rate (same as base format)
                16,				  // sample size in bits (thx to Javazoom)
                baseFormat.getChannels(),	  // # of Channels
                baseFormat.getChannels()*2,	  // Frame Size
                baseFormat.getSampleRate(),	  // Frame Rate
                false				  // Big Endian
            );

            // The decoded input stream.
            decodedIn = AudioSystem.getAudioInputStream(decodedFormat, in);

            //Set up a line of audio data from our decoded stream.
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
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
     * Is the current song paused?
     * 
     * @return
     */
    public boolean isPaused()
    {
        return paused;
    } 
    
    /**
     * Pauses the current song playing.
     * 
     * @param paused
     */
    public void setPaused(boolean paused)
    {
        if (paused == true)
            this.paused = true;
        else
        {
            synchronized (lock) 
            {
                this.paused = false;
                lock.notifyAll();
            }
        } // end if                   
    }
    
    /**
     * A method to increase the volume.
     * @param volume The new volume value.
     */
    public void setVolume(float volume)
    {  
        this.currentVolume = volume;
    }
    
    /**
     * A method to get the volume of the song.
     * @return the volume.
     */
    public float getVolume()
    {
        return this.currentVolume;
    }
   
    public String getKey()
    {
        return this.key;
    }
    
}

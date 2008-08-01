/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.audio;

import ca.couchware.wezzle2d.LogManager;
import ca.couchware.wezzle2d.util.Util;
import java.net.URL;
import java.io.*;
import javax.sound.sampled.*;


/**
 * A class to hold an audio file.  This is used by both the sound and music
 * managers.
 * 
 * Based on code from http://www.javalobby.org/java/forums/t18465.html
 * 
 * @author Kevin, Cameron
 */

public class AudioPlayer 
{        
    /** 
     * The numeric identifier for the audio file.
     */
    private AudioTrack track;
    
    /**
     * The audio file extension.
     */
    private String ext;
    
    /** 
     * A line of music data.
     */
    SourceDataLine line;        
    
    /** 
     * The decoded audio format.
     */
    AudioFormat decodedFormat;
    
    /** 
     * The input stream from the file.
     */
    AudioInputStream in;
    
    /** 
     * The decoded input stream.
     */
    AudioInputStream decodedIn;
    
    /** 
     * The url for the file.
     */
    URL url;   
    
    /** 
     * Is the music paused?
     */
    volatile boolean paused = false;
    
    /** 
     * The volume control.
     */
    FloatControl volume = null;
    
    /** 
     * The current volume.
     */
    private float currentVolume;

    /**
     * The constructor.
     * 
     * @param key
     * @param path
     */
    public AudioPlayer(AudioTrack track, String path)
    {
        // The associated key.
        this.track = track;
        
        // Load the reserouce.
        url = this.getClass().getClassLoader()
            .getResource(path);
        
        // Check the URL.
        if (url == null)
            throw new RuntimeException("Url Error: " + path 
                    + " does not exist.");
        
        // Determine the extension.
        ext = Util.getFileExtension(path);
        
        // Load the audio.
        load();
         
        // Set the current volumne.
        this.currentVolume = 0.0f;                    
    }
    
    /**
     * Play the audio.
     * 
     * The method creates an audio stream and plays through it a line of
     * data at a time.
     * 
     * Note: The first line is already initialized when we get to 
     * this method.
     * 
     */
    public void play()
    {
        try
        {
            if (line != null) 
            {
				line.open(decodedFormat);
				byte[] data = new byte[4096];
				
                // Start
				line.start();
                
				int nBytesRead;
                
                synchronized (this)
                {
                    while ((nBytesRead = decodedIn.read(data, 0, data.length)) != -1) 
                    {	
                        // The volume control, must apply to every line of data.
                        volume = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                        volume.setValue(this.currentVolume);
                        
                        while (paused) 
                        {
                            if (line.isRunning())                             
                                line.stop();
                            
                            try 
                            {
                                this.wait();
                            }
                            catch (InterruptedException e) 
                            {
                               LogManager.handleException(e);
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
                load();
			}
        }
        catch(Exception e)
        {
           LogManager.handleException(e);   
        }
    }        

    /**
     * A method to load/rest an audio file.      
     */
    public void load()
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
            
            // If it's an .ogg file, treat it differently.
            if (ext.equals("ogg") == true)
            {
                // decode the format so we can play it.
                decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, // Encoding to use               
                    baseFormat.getSampleRate(),      // sample rate (same as base format)
                    16,                              // sample size in bits (thanks to Javazoom)
                    baseFormat.getChannels(),        // # of Channels
                    baseFormat.getChannels() * 2,	 // Frame Size
                    baseFormat.getSampleRate(),      // Frame Rate
                    false                            // Big Endian
                );                
            }
            // Otherwise, do this.
            else
            {
                // The format is already decoded.
                decodedFormat = baseFormat;                                
            }
            
            // The decoded input stream.
            decodedIn = AudioSystem.getAudioInputStream(decodedFormat, in);

            //Set up a line of audio data from our decoded stream.
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
            line = (SourceDataLine) AudioSystem.getLine(info);                           
        }
        catch (Exception e)
        {
            LogManager.handleException(e);
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
            synchronized (this) 
            {
                this.paused = false;
                this.notifyAll();
            }
        } // end if                   
    }
    
    /**
     * A method to increase the volume.
     * 
     * @param volume The new volume value.
     */
    public void setVolume(float volume)
    {  
        this.currentVolume = volume;
    }
    
    /**
     * A method to get the volume of the song.
     * 
     * @return the volume.
     */
    public float getVolume()
    {
        return currentVolume;
    }
   
    /**
     * Get the track enum.
     *  
     * @return
     */
    public AudioTrack getTrack()
    {
        return track;
    }
    
}

/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.audio;

import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.util.AtomicDouble;
import ca.couchware.wezzle2d.util.Util;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 * An extension to the JavaZoom BasicPlayer class that incorporates more
 * advanced features such as fading in and out and looping.
 * 
 * @author cdmckay
 */
public class MusicPlayer
{     
    /**
     * The scheduled executor.  It is used to fade the volume.
     */
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);        
    
    /**
     * The basic player that this wraps.
     */
    private BasicPlayer player;
    
    /**
     * The player lock.
     */
    private Object playerLock = new Object();        
    
    /**
     * The future reference for the fader.
     */
    private Future fadeFuture;
    
    /**
     * The future reference lock.
     */
    private Object fadeFutureLock = new Object();
    
    /**
     * The future reference for the stop at gain thing.
     */
    private Future stopFuture;
    
     /**
     * The future reference lock.
     */
    private Object stopFutureLock = new Object();
    
    /**
     * The current normalizedGain.
     */
    private AtomicDouble normalizedGain = new AtomicDouble();
    
    /**
     * Should we loop this player?
     */
    private AtomicBoolean loop = new AtomicBoolean(false);
    
    /**
     * Has the player finished playing the track?
     */
    private AtomicBoolean finished = new AtomicBoolean(false);
    
    /**
     * The constructor.
     */
    private MusicPlayer()
    {         
        // Create a new basic player.
        this.player = new BasicPlayer();                        
        this.player.addBasicPlayerListener(new MusicPlayerListener());
    }    
    
    /**
     * Create a new music player instance.
     * 
     * @return
     */
    public static MusicPlayer newInstance()
    {
        return new MusicPlayer();
    }
    
    public void open(URL url) throws BasicPlayerException
    {
        synchronized (playerLock)
        { 
            player.open(url); 
        }
    }
    
    public void play() throws BasicPlayerException
    {
        if (finished.get() == true)
        {
            LogManager.recordWarning("Attempted to play a track that was finished.");
            return;
        }
        
        synchronized (playerLock)
        { 
            player.play();
        }
    }
    
    public void stop() throws BasicPlayerException
    {
        synchronized (playerLock)
        { 
            player.stop();
        }
        
        // Clear the finished status.
        finished.set(false);
    }
    
    public void pause() throws BasicPlayerException
    {
        synchronized (playerLock)
        { 
            player.pause();
        }
    }
    
    public void resume() throws BasicPlayerException
    {
        synchronized (playerLock)
        { 
            player.resume();
        }
    }
       
    public void setNormalizedGain(double nGain) throws BasicPlayerException
    {
        // Make sure gain is between 0.0 and 1.0.
        if (nGain < 0.0) nGain = 0.0;
        else if (nGain > 1.0) nGain = 1.0;
        
        // Invoke the super.
        synchronized (playerLock)
        { 
            player.setGain(nGain); 
        }
        
        // Set the normalized gain.
        this.normalizedGain.set(nGain);
    }              
    
    /**
     * Slowly fade the volume to the passed volume level.
     * 
     * @param targetGain The target gain to fade to, from 0.0 to 1.0.    
     */
    public void fadeToGain(final double nGain)
    {                
        Runnable r = new Runnable()
        {
            private double targetNormalizedGain = nGain;
            
            public void run()
            {
                double n = normalizedGain.get();
                double delta = 0.05;                

                if (Util.equalsDouble(n, targetNormalizedGain, 0.02))
                {
                    //LogManager.recordMessage("Fade completed.");                    
                    
                    // Cancel this runnable.          
                    synchronized (fadeFutureLock)
                    {
                        if (fadeFuture != null)
                            fadeFuture.cancel(false);
                    }                   
                    
                    // End execution.
                    return;
                }
                
                if (n > targetNormalizedGain)
                {
                    delta *= -1;                                        
                }          
                
                try
                {           
                    //LogManager.recordMessage("Fade gain set to " + (n + delta));
                    setNormalizedGain(n + delta);
                }
                catch (BasicPlayerException e)
                {
                    LogManager.recordException(e);
                }
            }            
        };
                
        synchronized (fadeFutureLock)
        {
            // Cancel existing fader.
            if (this.fadeFuture != null)
                this.fadeFuture.cancel(false);
        
            // Start a new fader.
            this.fadeFuture = executor.scheduleWithFixedDelay(r, 0, 100, TimeUnit.MILLISECONDS);    
        } // end sync            
    }

    /**
     * Is the player looping?
     * 
     * @return
     */
    public boolean getLoop()
    {
        return loop.get();
    }
    
    /**
     * Set the loop status of the player.  True for looping, false for not.
     * 
     * @param loop
     */
    public void setLoop(boolean loop)
    {
        this.loop.set(loop);
    }   
    
    /**
     * Is the player at the end of the track?
     */
    public boolean isFinished()
    {
        return finished.get();
    }   
    
    public void stopAtGain(final double nGain)
    {
        Runnable r = new Runnable()
        {
            final private double targetGain = nGain;
            
            public void run()
            {
                LogManager.recordMessage("Checking...");
                
                if (Util.equalsDouble(targetGain, normalizedGain.get(), 0.02))
                {                                        
                    synchronized (player)
                    {                        
                        try
                        {
                            player.stop();
                            
                            // Cancel this runnable.          
                            synchronized (stopFutureLock)
                            {
                                if (stopFuture != null)
                                    stopFuture.cancel(false);
                            }  
                        }
                        catch (BasicPlayerException e)
                        {
                            LogManager.recordException(e);
                        }
                    } // end sync
                } // end if
            }
        };
        
        synchronized (stopFutureLock)
        {
            // Cancel existing stopper.
            if (this.stopFuture != null)
                this.stopFuture.cancel(false);
        
            // Start a new stopper.
            this.stopFuture = executor.scheduleWithFixedDelay(r, 0, 500, TimeUnit.MILLISECONDS);    
        } // end sync  
    }
            
    /**
     * A private class that listens for certain events in order to implement
     * looping.
     */
    private class MusicPlayerListener implements BasicPlayerListener
    {
        public void opened(Object stream, Map properties)
        {
            // Intentionally left blank.
        }

        public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties)
        {
            // Intentionally left blank.
        }

        public void stateUpdated(BasicPlayerEvent event)
        {
            // Wait for EOM.  If looping is enabled, then loop.
            switch (event.getCode())
            {
                case BasicPlayerEvent.EOM:
                    try
                    {           
                        synchronized (playerLock)
                        {
                            player.stop();
                            if (loop.get() == true)
                            {                     
                                player.play();
                                setNormalizedGain(normalizedGain.get());                            
                            }    
                            else
                            {
                                finished.set(true);
                            }
                        } // end sync                                                                
                    }
                    catch (BasicPlayerException e)
                    {
                        LogManager.recordException(e);
                    }
                    break;
            } // end switch
        }

        public void setController(BasicController controller)
        {
            // Intentionally left blank.
        }
    }         

}

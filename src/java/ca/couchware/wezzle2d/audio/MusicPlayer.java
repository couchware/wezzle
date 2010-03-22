/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.audio;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.AtomicDouble;
import ca.couchware.wezzle2d.util.NumUtil;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemJPCT;

/**
 * An extension to the JavaZoom BasicPlayer class that incorporates more
 * advanced features such as fading in and out and looping.
 * 
 * @author cdmckay
 */
public class MusicPlayer
{
    private static final double MIN_GAIN = 0.0;
    private static final double MAX_GAIN = 1.0;
    private static final double EPSILON = 0.001;
    private static final double FADE_DELTA = 0.02;
    private static final int FADE_PERIOD = 50;
    private static final int STOP_PERIOD = 250;
    public static SoundSystemJPCT player = Game.getSoundSystem();
    private String key;
    private Thread fadeThread;
    private Thread stopThread;    
    private AtomicBoolean cancelFade = new AtomicBoolean(false);
    private AtomicBoolean cancelStop = new AtomicBoolean(false);
    final private Object playerLock = new Object();
    private AtomicDouble normalizedGain = new AtomicDouble();
    private AtomicBoolean loop = new AtomicBoolean(false);
    
    /* The fade direction is 1 for up, -1 for down */
    final private static int DIR_UP = 1;
    final private static int DIR_DOWN = -1;
    private AtomicInteger fadeDirection = new AtomicInteger(0);

    /**
     * The constructor.
     */
    private MusicPlayer(String path, String key)
    {        
        this.key = key;
        open(path);
    }

    /**
     * Create a new music player instance.
     * 
     * @return
     */
    public static MusicPlayer newInstance(String path, String key)
    {
        return new MusicPlayer(path, key);
    }

    private void open(String path)
    {
        player.newStreamingSource(true, key, path, false, SoundSystemConfig.ATTENUATION_NONE);
    }

    public void play()
    {
        synchronized (playerLock)
        {
            try
            {

                player.play(key);

            } catch (Exception ex)
            {
                CouchLogger.get().recordException(getClass(), ex);
            }
        }
    }

    public void stop()
    {
        try
        {
            if (fadeThread != null && fadeThread.isAlive())
            {
                cancelFade.set(true);
                fadeThread.join();
            }

            if (stopThread != null && stopThread.isAlive())
            {
                cancelStop.set(true);
                stopThread.join();
            }

            synchronized (playerLock)
            {
                player.stop(key);
            }
        } catch (Exception ex)
        {
            CouchLogger.get().recordException(getClass(), ex);
        }
    }

    public void pause()
    {
        synchronized (playerLock)
        {
            try
            {
                player.pause(key);
            } catch (Exception ex)
            {
                CouchLogger.get().recordException(getClass(), ex);
            }
        }
    }

    public void resume()
    {
        synchronized (playerLock)
        {
            try
            {
                player.play(key);
            } catch (Exception ex)
            {
                CouchLogger.get().recordException(getClass(), ex);
            }
        }
    }

    public void rewind()
    {
       player.rewind(key);       
    }

    public void setNormalizedGain(double nGain)
    {
        synchronized (playerLock)
        {
            try
            {
                nGain = Math.max(nGain, MIN_GAIN);
                nGain = Math.min(nGain, MAX_GAIN);
                player.setVolume(key, (float) nGain);
            } catch (Exception ex)
            {
                CouchLogger.get().recordException(getClass(), ex);
            }
        }

        this.normalizedGain.set(nGain);
    }

    /**
     * Slowly fade the volume to a specified gain.
     * 
     * @param targetGain The target gain to fade to, from 0.0 to 1.0.    
     */
    public void fadeToGain(final double nGain)
    {
        if (fadeThread != null && fadeThread.isAlive())
        {
            try
            {
                cancelFade.set(true);
                fadeThread.join();
            } catch (InterruptedException ex)
            {
                CouchLogger.get().recordException(getClass(), ex);
            }
        }

        fadeThread = new Thread()
        {
            final private double targetNormalizedGain = nGain;

            @Override
            public void run()
            {
                cancelFade.set(false);
                
                if (nGain > normalizedGain.get())
                    fadeDirection.set(DIR_UP);
                else
                    fadeDirection.set(DIR_DOWN);

                while (true)
                {
                    final int dir = fadeDirection.get();
                    final double n = normalizedGain.get();
                    final double delta = FADE_DELTA * dir;

                    final boolean atUpper = dir == DIR_UP && n >= targetNormalizedGain;
                    final boolean atLower = dir == DIR_DOWN && n <= targetNormalizedGain;

                    if (cancelFade.get() || atUpper || atLower)
                    {
                        // End execution.
                        break;
                    }                    

                    final double g = n + delta;
                    setNormalizedGain(g);

                    try
                    {
                        Thread.sleep(FADE_PERIOD);
                    } catch (InterruptedException ex)
                    {
                        break;
                    }
                } // end while
            } // end thread
        };

        fadeThread.start();
    }

    /**
     * Is the player looping?
     * 
     * @return
     */
    public boolean isLooping()
    {
        return loop.get();
    }

    /**
     * Set the loop status of the player.  True for looping, false for not.
     * 
     * @param loop
     */
    public void setLooping(boolean loop)
    {
        this.loop.set(loop);
        player.setLooping(key, loop);
    }

    /**
     * Is the player at the end of the track?
     */
    public synchronized boolean isFinished()
    {
        return !player.playing(key);
    }

    /**
     * This method will call stop() once the music player
     * reaches the specified gain.  The order will expire
     * once the gain is reached, or if the track is finished
     * as decided by isFinished().
     *
     * @see stop
     * @see isFinished
     * @param nGain     
     */
    public void stopAtGain(final double nGain)
    {
        stopThread = new Thread()
        {
            final private double targetNormalizedGain = nGain;

            @Override
            public void run()
            {
                cancelStop.set(false);

                if (nGain > normalizedGain.get())
                    fadeDirection.set(DIR_UP);
                else
                    fadeDirection.set(DIR_DOWN);

                while (true)
                {
                    final int dir = fadeDirection.get();
                    final double n = normalizedGain.get();
                    final boolean atUpper = dir == DIR_UP && n >= targetNormalizedGain;
                    final boolean atLower = dir == DIR_DOWN && n <= targetNormalizedGain;

                    if (atUpper || atLower || cancelStop.get() || isFinished())
                    {
                        synchronized (playerLock)
                        {
                            try
                            {
                                player.stop(key);
                                break;
                            } catch (Exception e)
                            {
                                CouchLogger.get().recordException(getClass(), e, true /* Fatal */);
                            }
                        } // end sync
                    }

                    try
                    {
                        Thread.sleep(STOP_PERIOD);
                    } catch (InterruptedException ex)
                    {
                        break;
                    }
                } // end while
            } // end thread
        };

        stopThread.start();
    }
}

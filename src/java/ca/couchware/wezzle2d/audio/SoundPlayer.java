/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.audio;

import java.util.UUID;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

/**
 * A class to hold an audio file.  This is used by both the sound and music
 * managers. 
 * 
 * @author Kevin, Cameron
 */
public class SoundPlayer
{
    private double normalizedGain;
    private final SoundSystem soundSystem;
    private String key = UUID.randomUUID().toString();

    public SoundPlayer(SoundSystem soundSystem, String path)
    {
        if (soundSystem == null)
        {
            throw new IllegalArgumentException("Sound system cannot be null");
        }

        if (path == null)
        {
            throw new IllegalArgumentException("Path cannot be null");
        }

        this.soundSystem = soundSystem;
        open(path);
    }

    private void open(String path)
    {
        soundSystem.newSource(false, key, path, false, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 1);
    }

    public void play()
    {
        soundSystem.play(key);
    }

    /**
     * A method to load a WAV file. This method assumes .wav format.
     * @param stream - A markable inputstream.
     */
    public void close()
    {
        if (soundSystem.playing(key))
            soundSystem.stop(key);
    }

    /**
     * Sets gain (i.e. volume) value.     
     * Linear scale 0.0 - 1.0.
     * 
     * @param fGain The gain to set.     
     */
    public void setNormalizedGain(double nGain)
    {
        soundSystem.setVolume(key, (float) nGain);
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

/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.audio;

import ca.couchware.wezzle2d.Game;
import java.util.UUID;
import paulscode.sound.SoundSystemJPCT;

/**
 * A class to hold an audio file.  This is used by both the sound and music
 * managers. 
 * 
 * @author Kevin, Cameron
 */
public class SoundPlayer
{

    private double normalizedGain;
    private SoundSystemJPCT player = Game.getSoundSystem();
    private String key = UUID.randomUUID().toString();

    public SoundPlayer(String path)
    {
        player.newSource(false, key, path, false);
    }

    public void play()
    {
        player.play(key);
    }

    /**
     * A method to load a WAV file. This method assumes .wav format.
     * @param stream - A markable inputstream.
     */
    public void close()
    {
        if(player.playing(key))
            player.stop(key);
    }

    /**
     * Sets gain (i.e. volume) value.     
     * Linear scale 0.0 - 1.0.
     * 
     * @param fGain The gain to set.     
     */
    public void setNormalizedGain(double nGain)
    {
        player.setVolume(key, (float) nGain);
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

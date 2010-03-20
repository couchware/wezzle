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
    private double normalizedGain;

    public SoundPlayer(String path)
    {
        InputStream stream = SoundPlayer.class.getClassLoader().getResourceAsStream(path);
        BufferedInputStream bin = new BufferedInputStream(stream);
        open(bin);
    }

    // Plays as a sound effect only.
    public void play()
    {
        clip.playAsSoundEffect(1.0f, (float) this.getNormalizedGain(), false);
    }

    /**
     * A method to load a WAV file. This method assumes .wav format.
     * @param stream - A markable inputstream.
     */
    private void open(BufferedInputStream stream)
    {
        try
        {
            clip = AudioLoader.getAudio("WAV", stream);
        } catch (Exception e)
        {
            CouchLogger.get().recordException(this.getClass(), e, true /* Fatal */);
        }
    }

    public void close()
    {
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

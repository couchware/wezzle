/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.audio;

/**
 * Contains all the possible sounds.  Note that the number of buffers must
 * not exceed 32.
 * @author cdmckay
 */
public enum Sound 
{
    BOMB(3),
    LINE_1(3),
    LINE_2(2),
    LINE_3(1),
    LINE_4(1),
    LINE_5(1),
    LINE_6(1),
    LINE_7(1),
    BLEEP(1),
    CLICK(1),
    CLICK_LIGHT(1),
    LEVEL_UP(1),
    STAR(1),
    ROCKET(3),
    ACHIEVEMENT(3);

    /** The number of buffers this clip needs. */
    final private int numberOfBuffers;

    Sound(int numberOfBuffers)
    {
        if (numberOfBuffers < 1)
            throw new IllegalArgumentException("At least 1 buffer is required.");

        this.numberOfBuffers = numberOfBuffers;
    }

    public int getNumberOfBuffers()
    { return numberOfBuffers; }
}

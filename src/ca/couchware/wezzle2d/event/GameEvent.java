/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import ca.couchware.wezzle2d.difficulty.GameDifficulty;
import java.util.EventObject;

/**
 * The event object for game events. 
 * @author cdmckay
 */
public class GameEvent extends EventObject
{
    final private GameDifficulty difficulty;
    final private int level;    
    final private int totalScore;
    
    public GameEvent(Object source,
            GameDifficulty difficulty, int level, int totalScore)
    {
        super(source);

        if (difficulty == null)
            throw new IllegalArgumentException("Difficulty cannot be null");

        if (level < 1)
            throw new IllegalArgumentException("Level must be greater than 0");

        if (totalScore < 0)
            throw new IllegalArgumentException("Level must be greater than or equal to 0");

        this.difficulty = difficulty;
        this.level = level;
        this.totalScore = totalScore;
    }

    public GameDifficulty getDifficulty()
    {
        return difficulty;
    }

    public int getLevel()
    {
        return level;
    }

    public int getTotalScore()
    {
        return totalScore;
    }
        
}

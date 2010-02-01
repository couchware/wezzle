/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.event.*;
import ca.couchware.wezzle2d.manager.ListenerManager.GameType;
import java.util.EnumMap;

/**
 * A class to handle various game trivia.
 * 
 * @author Kevin, Cameron
 */

public class StatManager implements IResettable, ISaveable, IGameListener, ILineListener, IMoveListener
{	
	/**
	 * The current move count.
	 */
	private int moveCount;
        
    /**
     * The current line count.
     */
    private int lineCount;
        
    /**
     * The current cycle line count.
     */
    private int cycleLineCount;
    
    /**
     * The current chain count.
     */
    private int chainCount;
    
    /**
     * The starting level.
     */
    private int startLevel;
    
    /**
     * The current line chain count.  This is different from the normal chain
     * count in that it only counts lines made, and not items activated.
     * 
     * For example, if the player got a line with a rocket, which blew away
     * a line, and then the the resulting refactor maade another line, then 
     * the "line" chain count would only be 2 while the chain count would be
     * 3.
     */
    private int lineChainCount;
    
   /**
     * The hash map keys for storing the score manager state.
     */
    private static enum Keys
    {
        MOVE_COUNT,
        LINE_COUNT,
        CYCLE_LINE_COUNT,
        CHAIN_COUNT,
        LINE_CHAIN_COUNT,
        START_LEVEL
    }
    
    /**
     * The hash map used to save the score manager's state.
     */
    private EnumMap<Keys, Object> managerState;
	
	/**
	 * The constructor.
	 * Private to ensure singleton.
	 */
	private StatManager()
	{
        // Create the save state.
        managerState = new EnumMap<Keys, Object>(Keys.class);

        // Reset the counters.
        this.resetMoveCount();                
        this.resetLineCount();
        this.resetCycleLineCount();
        this.resetChainCount();    
        this.resetLineChainCount();
	}
        
    /**
     * Create a new stat manager instance.
     * @return
     */    
    public static StatManager newInstance()
    {
        return new StatManager();
    }
        
	/**
	 * @return the currentMoves
	 */
	public int getMoveCount()
	{
		return moveCount;
	}
    
    /**
	 * @param currentMoves the currentMoves to set.
	 */
	private void setMoveCount(int currentMoveCount)
	{
		this.moveCount = currentMoveCount;
	}
    
    /**
     * A method to increment the move count.
     */
    public void incrementMoveCount()
    {
        this.moveCount++;
    }
    
    /**
     * A method to reset the move count.
     */
    public void resetMoveCount()
    {
        setMoveCount(0);
    }

    public int getLineCount()
    {
        return lineCount;
    }

    private void setLineCount(int lineCount)
    {
        this.lineCount = lineCount;
    }

    public void incrementLineCount()
    {
        this.lineCount++;
    }
    
    public void incrementLineCount(int delta)
    {
        this.lineCount += delta;
    }
    
    public void resetLineCount()
    {
        setLineCount(0);
    }

    public int getChainCount()
    {
        return chainCount;
    }

    public void setChainCount(int chainCount)
    {
        this.chainCount = chainCount;
    }
    
    public void incrementChainCount()
    {
        this.chainCount++;
    }
    
    public void resetChainCount()
    {
        setChainCount(0);
    }

    public int getLineChainCount()
    {
        return lineChainCount;
    }

    public void setLineChainCount(int lineChainCount)
    {
        this.lineChainCount = lineChainCount;
    }
    
    public void incrementLineChainCount()
    {
        this.lineChainCount++;
    }
    
    public void resetLineChainCount()
    {
        this.lineChainCount = 0;
    }

    public int getCycleLineCount()
    {
        return cycleLineCount;
    }

    public void setCycleLineCount(int cycleLineCount)
    {
        if(cycleLineCount < 0)
        {
           throw new IllegalArgumentException("Cycle line count must be non-negative");
        }
        this.cycleLineCount = cycleLineCount;
    }        
    
    public void incrementCycleLineCount()
    {
        this.cycleLineCount++;
    }
    
    public void incrementCycleLineCount(int delta)
    {
        this.cycleLineCount += delta;
    }
    
    public void resetCycleLineCount()
    {
        setCycleLineCount(0);
    }

    public int getStartLevel()
    {
        return startLevel;
    }

    public void setStartLevel(int startLevel)
    {
        if(startLevel <= 0)
        {
           throw new IllegalArgumentException("Start level must be greater than 0");
        }
        this.startLevel = startLevel;
    }        
    
    /**
     * Determine the average number of lines that have occured per move.
     * 
     * @return The lines per move average.
     */
    public double getLinesPerMove()
    {
         // Calculate lines per move.
        double linesPerMove;
        if (getMoveCount() == 0)
        {
            linesPerMove = 0.0;
        }
        else
        {
            linesPerMove = (double) getLineCount() / (double) getMoveCount();
            linesPerMove *= 100.0;
            linesPerMove = ((double) (int) linesPerMove) / 100.0;
        }
        
        return linesPerMove;
    }
    
    /**
     * Overridden methods
     */
    
    /**
     * handle a line event.
     * @param e The line event.
     */    
    public void lineConsumed(LineEvent e, GameType gameType)
    {
        this.setLineCount(this.getLineCount() + e.getLineCount());
    }
    
    /**
     * Handle a move event.
     * 
     * @param e The move event.
     */    
    public void moveCommitted(MoveEvent e, GameType gameType)
    {
        this.setMoveCount(this.getMoveCount() + e.getMoveCount());
    }
    
    public void moveCompleted(MoveEvent event)
    {
        // Intentionally left blank.
    }

    /**
     * Save the state.
     */
    public void saveState()
    {
        managerState.put(Keys.MOVE_COUNT, this.moveCount);
        managerState.put(Keys.LINE_COUNT, this.lineCount);
        managerState.put(Keys.CYCLE_LINE_COUNT, this.cycleLineCount);
        managerState.put(Keys.CHAIN_COUNT, this.chainCount);
        managerState.put(Keys.LINE_CHAIN_COUNT, this.lineChainCount);
        managerState.put(Keys.START_LEVEL, this.startLevel);
    }

    /**
     * Load the state.
     */
    public void loadState()
    {                
        // See if there is a save state.
        if (managerState.isEmpty() == true)
        {
            CouchLogger.get().recordWarning(this.getClass(), "No save state exists");
            return;
        }
        
        this.moveCount = (Integer) managerState.get(Keys.MOVE_COUNT); 
        this.lineCount = (Integer) managerState.get(Keys.LINE_COUNT); 
        this.cycleLineCount = (Integer) managerState.get(Keys.CYCLE_LINE_COUNT); 
        this.chainCount = (Integer) managerState.get(Keys.CHAIN_COUNT); 
        this.lineChainCount = (Integer) managerState.get(Keys.LINE_CHAIN_COUNT); 
        this.startLevel = (Integer) managerState.get(Keys.START_LEVEL); 
    }

    public void resetState()
    {
        this.moveCount = 0;
        this.lineCount = 0;
        this.cycleLineCount = 0;
        this.chainCount = 0;
        this.lineChainCount = 0;
        this.startLevel = 1;
    }

    public void gameStarted(GameEvent event)
    {
        this.startLevel = event.getLevel();
    }

    public void gameReset(GameEvent event)
    {
        this.startLevel = event.getLevel();
    }

    public void gameOver(GameEvent event)
    {
        // Intentionally left blank.
    }
    
}
/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.event.*;
import ca.couchware.wezzle2d.manager.ListenerManager.GameType;
import java.util.EnumMap;

/**
 * A class to handle various game trivia.
 * 
 * @author Kevin, Cameron
 */

public class StatManager implements IManager, IMoveListener, ILineListener
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
     * The hash map keys for storing the score manager state.
     */
    private static enum Keys
    {
        MOVE_COUNT,
        LINE_COUNT,
        CYCLE_LINE_COUNT,
        CHAIN_COUNT
    }
    
    /**
     * The hash map used to save the score manager's state.
     */
    private EnumMap<Keys, Object> managerState;
	
	/**
	 * The constructor.
	 *  private to ensure singleton.
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
	}
        
        
        // Public APi.
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
    private void incrementMoveCount()
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

	private void incrementLineCount()
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

    public int getCycleLineCount()
    {
        return cycleLineCount;
    }

    public void setCycleLineCount(int cycleLineCount)
    {
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
        managerState.put(Keys.MOVE_COUNT, moveCount);
        managerState.put(Keys.LINE_COUNT, lineCount);
        managerState.put(Keys.CYCLE_LINE_COUNT, cycleLineCount);
        managerState.put(Keys.CHAIN_COUNT, chainCount);
    }

    /**
     * Load the state.
     */
    public void loadState()
    {                
        // See if there is a save state.
        if (managerState.isEmpty() == true)
        {
            LogManager.recordWarning("No save state exists.", "MoveManager#load");
            return;
        }
        
        moveCount = (Integer) managerState.get(Keys.MOVE_COUNT); 
        lineCount = (Integer) managerState.get(Keys.LINE_COUNT); 
        cycleLineCount = (Integer) managerState.get(Keys.CYCLE_LINE_COUNT); 
        chainCount = (Integer) managerState.get(Keys.CHAIN_COUNT); 
    }

    public void resetState()
    {
        moveCount = 0;
        lineCount = 0;
        cycleLineCount = 0;
        chainCount = 0;
    }    
    
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d;

/**
 * A class to handle the move counter.
 * @author Kevin
 */

public class MoveManager 
{	

	
	/**
	 * The current move.
	 */
	private int currentMoveCount;
	
	
	/**
	 * The constructor.
	 * 
	 */
	public MoveManager()
	{
		this.currentMoveCount = 0;
	}

	/**
	 * @return the currentMoves
	 */
	public int getMoveCount()
	{
		return currentMoveCount;
	}
    
    /**
     * A method to increment the move count.
     */
    public void incrementMoveCount()
    {
        this.currentMoveCount++;
    }
    
    /**
     * A method to reset the move count.
     */
    public void resetMoveCount()
    {
        this.currentMoveCount = 0;
    }

	/**
	 * @param currentMoves the currentMoves to set.
	 */
	public void setCurrentMoveCount(int currentMoveCount)
	{
		this.currentMoveCount = currentMoveCount;
	}		
}
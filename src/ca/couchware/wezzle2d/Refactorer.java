/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.event.IListenerComponent;
import ca.couchware.wezzle2d.event.LineEvent;
import java.util.EnumMap;
import java.util.List;

/**
 * A class for handling the board refactoring.  This is a singleton class.
 * @author cdmckay
 */
public class Refactorer 
{
    
    /** 
     * The single instance of this class to ever exist. 
     */
	private static final Refactorer single = new Refactorer();
    
    /**
     * The refator speeds.
     */
    public static enum RefactorSpeed
    {
        SLOWER,
        SLOW,
        NORMAL, 
        DROP,
        SHIFT    
    }
    
    /**
     * The default slow speed.
     */
    final public static int DEFAULT_SLOWER_SPEED = 1;
    
    /**
     * The default slow speed.
     */
    final public static int DEFAULT_SLOW_SPEED = 2;
    
    /**
     * The default normal speed.
     */
    final public static int DEFAULT_NORMAL_SPEED = 3;
    
    /**
     * The default drop speed.
     */
    final public static int DEFAULT_DROP_SPEED = 4;
    
    /**
     * The default shift speed.
     */
    final public static int DEFAULT_SHIFT_SPEED = 4;
    
     /**
     * If true, refactor will be activated next loop.
     */
    private boolean activateRefactor = false;
    
    /**
     * If true, the board is currently being refactored downwards.
     */
    private boolean refactorVerticalInProgress = false;
    
    /**
     * If true, the board is currently being refactored leftward.
     */
    private boolean refactorHorizontalInProgress = false;
    
    /**
     * The refactor has finished this loop.
     */
    private boolean finishedRefactor = false;
    
    /**
     * The current refactor animations.
     */
    private List<IAnimation> refactorAnimationList;
    
    /**
     * The refactor type.
     */
    private RefactorSpeed speed;
    
    /**
     * The speed map.
     */
    private EnumMap<RefactorSpeed, Integer> speedMap = 
            new EnumMap<RefactorSpeed, Integer>(RefactorSpeed.class);
    
    private Refactorer()
    {
        // Set the refactor speeds to their defaults.
        speedMap.put(RefactorSpeed.SLOWER, DEFAULT_SLOWER_SPEED);
        speedMap.put(RefactorSpeed.SLOW, DEFAULT_SLOW_SPEED);
        speedMap.put(RefactorSpeed.NORMAL, DEFAULT_NORMAL_SPEED);
        speedMap.put(RefactorSpeed.DROP, DEFAULT_DROP_SPEED);
        speedMap.put(RefactorSpeed.SHIFT, DEFAULT_SHIFT_SPEED);       
        this.speed = RefactorSpeed.NORMAL;
    }
    
    /**
	 * Retrieve the single instance of this class.
	 * 
	 * @return The single instance of this class.
	 */
	public static Refactorer get()
	{
		return single;
	}   
    
    public RefactorSpeed getRefactorSpeed()
    {
        return speed;
    }

    public Refactorer setRefactorSpeed(RefactorSpeed speed)
    {
        assert speed != null;        
        this.speed = speed;
        return this;
    }        
    
     /**
     * Start a refactor with the given speed.
     * 
     * @param speed
     */
    public Refactorer startRefactor()
    {        
        // Set the refactor flag.
        this.activateRefactor = true;                   
        return this;
    }
    
    /**
     * Clear the refactor flag.
     */
    public void clearRefactor()
    {
       // Set the refactor flag.
       this.activateRefactor = false;
    }
    
    /**
     * Has the refactorered just finished running?  This value will be
     * true only for the loop where it has run.
     */
    public boolean isFinished()
    {
        return finishedRefactor;
    }
    
     /**
     * Checks whether a refactor is, or is about to be, in progress.
     */
    public boolean isRefactoring()
    {
        return this.activateRefactor 
                || this.refactorVerticalInProgress 
                || this.refactorHorizontalInProgress;
    }
    
    public void updateLogic(Game game)
    {
        assert game != null;
        
        // Reset the finished flag.
        this.finishedRefactor = false;
        
        // See if we need to activate the refactor.
        if (activateRefactor == true)
        {            
            // Hide piece.
            game.pieceMan.getPieceGrid().setVisible(false);

            // Start down refactor.                           
            this.refactorAnimationList = game.boardMan.startVerticalShift(speedMap.get(speed));              

            // Add to the animation manager.
            // No need to worry about removing them, that'll happen
            // automatically when they are done.
            game.animationMan.addAll(refactorAnimationList);

            // Set the refactor in progress flag.
            this.refactorVerticalInProgress = true;

            // Clear flag.
            clearRefactor();
        }
        
        // See if we're down refactoring.
        if (this.refactorVerticalInProgress == true)
        {
            handleVerticalRefactor(game);
        } 
        
        // See if we're left refactoring.
        if (this.refactorHorizontalInProgress == true)
        {
            handleHorizontalRefactor(game);
        }
        
    }
    
    /**
     * Handle the vertical refactoring of the board.
     * 
     * @param game
     */
    private void handleVerticalRefactor(Game game)
    {
        assert game != null;
        
        boolean done = true;
        for (IAnimation a : refactorAnimationList)
        {
            if (a.isFinished() == false)
            {
                done = false;
            }
        }

        if (done == true)
        {		
            // Clear the animation list.
            refactorAnimationList = null;

            // Clear down flag.
            refactorVerticalInProgress = false;

            // Synchronize board.
            game.boardMan.synchronize();							

            // Start left refactor.
            refactorAnimationList = game.boardMan.startHorizontalShift(speedMap.get(speed));
            
            // Add to the animation manager.
            // No need to worry about removing them, that'll happen
            // automatically when they are done.
            game.animationMan.addAll(refactorAnimationList);

            // Set the refactor in progress flag.
            refactorHorizontalInProgress = true;
            
        } // end if
    }
    
    /**
     * Handle the horizontal refactoring of the board.
     * 
     * @param game
     */
    private void handleHorizontalRefactor(Game game)
    {        
        assert game != null;
        
        boolean done = true;
        for (IAnimation a : refactorAnimationList)
        {
            if (a.isFinished() == false)
            {
                done = false;
            }
        }

        if (done == true)
        {
            // Clear the animation list.
            refactorAnimationList = null;

            // Clear left flag.
            refactorHorizontalInProgress = false;

            // Synchronize board.
            game.boardMan.synchronize();
            
            // Set the finished flag.
            this.finishedRefactor = true;
        }

        // Notify piece manager.
        game.pieceMan.notifyRefactored();     
        
        // Reset speed to normal.
        setRefactorSpeed(RefactorSpeed.NORMAL);
        
    }
    
}

/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.animation.IAnimation;
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
     * The refator types.
     */
    public static enum RefactorType
    {
        NORMAL, 
        DROP
    }
    
    /**
     * The default refactor speed.
     */
    final public static int DEFAULT_REFACTOR_SPEED = 180;
    
    /**
     * The default drop speed.
     */
    final public static int DEFAULT_DROP_SPEED = 250;
    
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
    private RefactorType refactorType;
    
    /**
     * The speed of the upcoming refactor.
     */
    private int refactorSpeed;
    
    /**
     * The speed of the upcoming drop.
     */
    private int dropSpeed;
    
    private Refactorer()
    {
        // Set the refactor speeds to their defaults.
        this.dropSpeed = DEFAULT_DROP_SPEED;
        this.refactorSpeed = DEFAULT_REFACTOR_SPEED;
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
    
    public int getDropSpeed()
    {
        return dropSpeed;
    }

    public void setDropSpeed(int dropSpeed)
    {
        this.dropSpeed = dropSpeed;
    }
    
    public void resetDropSpeed()
    {
        this.dropSpeed = DEFAULT_DROP_SPEED;
    }

    public int getRefactorSpeed()
    {
        return refactorSpeed;
    }

    public void setRefactorSpeed(int refactorSpeed)
    {
        this.refactorSpeed = refactorSpeed;
    }
    
    /**
     * Resets the refactor speed to it's default value.
     */
    public void resetRefactorSpeed()
    {
        this.refactorSpeed = DEFAULT_REFACTOR_SPEED;
    }

    public RefactorType getRefactorType()
    {
        return refactorType;
    }

    public void setRefactorType(RefactorType refactorType)
    {
        this.refactorType = refactorType;
    }        
    
     /**
     * Start a refactor with the given speed.
     * 
     * @param speed
     */
    public void startRefactor(RefactorType type)
    {
        // Set the refactor flag.
        this.activateRefactor = true;   
        
        // Set the type.
        this.refactorType = type;
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
        // Reset the finished flag.
        this.finishedRefactor = false;
        
        // See if we need to activate the refactor.
        if (activateRefactor == true)
        {            
            // Hide piece.
            game.pieceMan.getPieceGrid().setVisible(false);

            // Start down refactor.                
            switch (refactorType)
            {
                case NORMAL:
                    refactorAnimationList = 
                            game.boardMan.startVerticalShift(refactorSpeed);
                    break;

                case DROP:
                    refactorAnimationList =
                            game.boardMan.startVerticalShift(dropSpeed);
                    break;                    

                default: throw new AssertionError();
            }

            // Add to the animation manager.
            // No need to worry about removing them, that'll happen
            // automatically when they are done.
            game.animationMan.addAll(refactorAnimationList);

            refactorVerticalInProgress = true;

            // Clear flag.
            clearRefactor();
        }
        
        // See if we're down refactoring.
        if (refactorVerticalInProgress == true)
        {
            handleVerticalRefactor(game);
        } 
        
        // See if we're left refactoring.
        if (refactorHorizontalInProgress == true)
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

            switch (refactorType)
            {
                case NORMAL:
                    refactorAnimationList = 
                            game.boardMan.startHorizontalShift(refactorSpeed);
                    break;

                case DROP:
                    refactorAnimationList = 
                            game.boardMan.startHorizontalShift(dropSpeed);
                    break; 

                default: throw new AssertionError();
            }

            // Add to the animation manager.
            // No need to worry about removing them, that'll happen
            // automatically when they are done.
            game.animationMan.addAll(refactorAnimationList);

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
    }
    
}

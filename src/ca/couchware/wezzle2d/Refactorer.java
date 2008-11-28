/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import java.util.List;

/**
 * A class for handling the board refactoring.  This is a singleton class.
 * @author cdmckay
 */
public class Refactorer 
{
    
    /** A shortcut to the settings manager to simplify some code. */
    private static final SettingsManager settingsMan = SettingsManager.get();
    
    /** The single instance of this class to ever exist. */
	private static final Refactorer single = new Refactorer();        
    
    /** The refator speeds. */
    public static enum RefactorSpeed
    {
        /** The slower refactor speed, used in tutorials. */
        SLOWER(settingsMan.getInt(Key.REFACTOR_SPEED_X_SLOWER),
            settingsMan.getInt(Key.REFACTOR_SPEED_Y_SLOWER)),
            
        /** The slow refactor speed, used in tutorials. */
        SLOW(settingsMan.getInt(Key.REFACTOR_SPEED_X_SLOW),
            settingsMan.getInt(Key.REFACTOR_SPEED_Y_SLOW)),
        
        /** The normal refactor speed, used during normal operation. */
        NORMAL(settingsMan.getInt(Key.REFACTOR_SPEED_X_NORMAL),
            settingsMan.getInt(Key.REFACTOR_SPEED_Y_NORMAL)), 
        
        /** The shift refactor speed, used during gravity shifts. */
        SHIFT(settingsMan.getInt(Key.REFACTOR_SPEED_X_SHIFT),
            settingsMan.getInt(Key.REFACTOR_SPEED_Y_SHIFT));    
        
        /** The slide speed associated with the key. */
        private int horizontal;
        
        /** The drop speed associated with the key. */
        private int vertical;
                                
        RefactorSpeed(int horizontal, int vertical)
        { 
            assert horizontal != 0;
            assert vertical   != 0;
            this.horizontal = horizontal; 
            this.vertical   = vertical; 
        }
        
        public int getHorizontalSpeed()
        { return horizontal; }
        
        public int getVerticalSpeed()
        { return vertical; }
    }       
        
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
    
    private Refactorer()
    {
        // Set the refactor speeds to their defaults.           
        this.speed = RefactorSpeed.NORMAL;
    }
    
    /**
	 * Retrieve the single instance of this class.
	 * 
	 * @return The single instance of this class.
	 */
	static Refactorer get()
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
        
        LogManager.recordMessage("Speed set to " + speed.toString());
        
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
            this.refactorAnimationList = 
                    game.boardMan.startVerticalShift(speed.getVerticalSpeed());              

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
            refactorAnimationList = 
                    game.boardMan.startHorizontalShift(speed.getHorizontalSpeed());
            
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
    }
    
}

/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.manager.IResettable;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;

import java.util.List;

/**
 * A class for handling the board refactoring.  This is a singleton class.
 * @author cdmckay
 */
public class Refactorer implements IResettable
{
    /** A shortcut to the settings manager to simplify some code. */
    private static final SettingsManager settingsMan = SettingsManager.get();
    
    /** The refator speeds. */
    public static enum RefactorSpeed
    {
        /** The slower refactor speed, used in tutorials. */
        SLOWER( settingsMan.getInt( Key.REFACTOR_SPEED_X_SLOWER ),
            settingsMan.getInt( Key.REFACTOR_SPEED_Y_SLOWER ) ),

        /** The slow refactor speed, used in tutorials. */
        SLOW( settingsMan.getInt( Key.REFACTOR_SPEED_X_SLOW ),
            settingsMan.getInt( Key.REFACTOR_SPEED_Y_SLOW ) ),

        /** The normal refactor speed, used during normal operation. */
        NORMAL( settingsMan.getInt( Key.REFACTOR_SPEED_X_NORMAL ),
            settingsMan.getInt( Key.REFACTOR_SPEED_Y_NORMAL ) ),

        /** The shift refactor speed, used during gravity shifts. */
        SHIFT( settingsMan.getInt( Key.REFACTOR_SPEED_X_SHIFT ),
            settingsMan.getInt( Key.REFACTOR_SPEED_Y_SHIFT ) );

        /** The slide speed associated with the key. */
        private int horizontal;

        /** The drop speed associated with the key. */
        private int vertical;

        RefactorSpeed(int horizontal, int vertical)
        {
            if ( horizontal == 0 || vertical == 0 )
            {
                throw new IllegalArgumentException( "Horizontal and vertical must" +
                        " both be non-zero." );
            }
            this.horizontal = horizontal;
            this.vertical = vertical;
        }

        public int getHorizontalSpeed()
        {
            return horizontal;
        }

        public int getVerticalSpeed()
        {
            return vertical;
        }

    }
    /**
     * If true, refactor will be activated next loop.
     */
    private boolean activateRefactor;

    /**
     * If true, the board is currently being refactored downwards.
     */
    private boolean refactorVerticalInProgress;

    /**
     * If true, the board is currently being refactored leftward.
     */
    private boolean refactorHorizontalInProgress;

    /**
     * The refactor has finished this loop.
     */
    private boolean finishedRefactor;

    /**
     * The current refactor animations.
     */
    private List<IAnimation> refactorAnimationList;
    
    /**
     * The refactor type.
     */
    private RefactorSpeed speed;

    public Refactorer(Game game)
    {
        // Set the refactor speeds to their defaults.           
        this.speed = game.getGameDifficulty().getRefactorSpeed();

        // Reset the state.
        this.resetState();
    }

    public void resetState()
    {
        this.activateRefactor = false;
        this.refactorVerticalInProgress = false;
        this.refactorHorizontalInProgress = false;
        this.finishedRefactor = false;
    }

    /**
     * Retrieve the single instance of this class.
     *
     * @return The single instance of this class.
     */
//	static Refactorer get()
//	{
//		return SINGLE;
//	}
    public Refactorer setRefactorSpeed(RefactorSpeed speed)
    {
        if ( speed == null )
        {
            throw new IllegalArgumentException( "speed cannot be null." );
        }

        CouchLogger.get().recordMessage( this.getClass(), String.format( "Speed set to %s.", speed.
                toString() ) );

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

    public void updateLogic(Game game, ManagerHub hub)
    {
        // Make sure game is not null.
        if ( game == null || hub == null )
        {
            throw new IllegalArgumentException( "Game and hub must not be null" );
        }

        // Reset the finished flag.
        this.finishedRefactor = false;

        // See if we need to activate the refactor.
        if ( activateRefactor == true )
        {
            // Hide piece.
            hub.pieceMan.hidePieceGrid();

            // Start down refactor.                           
            this.refactorAnimationList =
                    hub.boardMan.startVerticalShift( speed.getVerticalSpeed() );

            // Add to the animation manager.
            // No need to worry about removing them, that'll happen
            // automatically when they are done.
            hub.animationMan.addAll( refactorAnimationList );

            // Set the refactor in progress flag.
            this.refactorVerticalInProgress = true;

            // Clear flag.
            clearRefactor();
        }

        // See if we're down refactoring.
        if ( this.refactorVerticalInProgress == true )
        {
            handleVerticalRefactor( hub );
        }

        // See if we're left refactoring.
        if ( this.refactorHorizontalInProgress == true )
        {
            handleHorizontalRefactor( hub );
        }

    }

    /**
     * Handle the vertical refactoring of the board.
     * 
     * @param game
     */
    private void handleVerticalRefactor(ManagerHub hub)
    {
        // Sanity check.
        if ( hub == null )
        {
            throw new IllegalArgumentException( "Hub cannot be null" );
        }

        boolean done = true;
        for ( IAnimation a : refactorAnimationList )
        {
            if ( !a.isFinished() )
            {
                done = false;
                break;
            }
        }

        if ( done == true )
        {
            // Clear the animation list.
            refactorAnimationList = null;

            // Clear down flag.
            refactorVerticalInProgress = false;

            // Synchronize board.
            hub.boardMan.synchronize();

            // Start left refactor.
            refactorAnimationList =
                    hub.boardMan.startHorizontalShift( speed.getHorizontalSpeed() );

            // Add to the animation manager.
            // No need to worry about removing them, that'll happen
            // automatically when they are done.
            hub.animationMan.addAll( refactorAnimationList );

            // Set the refactor in progress flag.
            refactorHorizontalInProgress = true;
        }
    }

    /**
     * Handle the horizontal refactoring of the board.
     * 
     * @param game
     */
    private void handleHorizontalRefactor(ManagerHub hub)
    {
        // Sanity check.
        if ( hub == null )
        {
            throw new IllegalArgumentException( "Hub cannot be null" );
        }

        boolean done = true;
        for ( IAnimation a : refactorAnimationList )
        {
            if ( !a.isFinished() )
            {
                done = false;
                break;
            }
        }

        if ( done == true )
        {
            // Clear the animation list.
            refactorAnimationList = null;

            // Clear left flag.
            refactorHorizontalInProgress = false;

            // Synchronize board.
            hub.boardMan.synchronize();

            // Set the finished flag.
            this.finishedRefactor = true;
        }

        // Notify piece manager.
        hub.pieceMan.notifyRefactored();
    }

}

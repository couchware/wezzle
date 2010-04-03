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
    final private static SettingsManager settingsMan = SettingsManager.get();

    /** The refator speeds. */
    public static enum RefactorSpeed
    {
        /** The slower refactor speed, used in tutorials. */
        SLOWER( settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_X ),
            settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_Y ),
            settingsMan.getInt( Key.REFACTOR_SLOWER_GRAVITY ),
            settingsMan.getInt( Key.REFACTOR_SLOWER_ACCELERATION )),

        /** The slow refactor speed, used in tutorials. */
        SLOW( settingsMan.getInt( Key.REFACTOR_SLOW_SPEED_X ),
            settingsMan.getInt( Key.REFACTOR_SLOW_SPEED_Y ),
            settingsMan.getInt( Key.REFACTOR_SLOW_GRAVITY ),
            settingsMan.getInt( Key.REFACTOR_SLOW_ACCELERATION )),

        /** The normal refactor speed, used during normal operation. */
        NORMAL( settingsMan.getInt( Key.REFACTOR_NORMAL_SPEED_X ),
            settingsMan.getInt( Key.REFACTOR_NORMAL_SPEED_Y ),
            settingsMan.getInt( Key.REFACTOR_NORMAL_GRAVITY ),
            settingsMan.getInt( Key.REFACTOR_NORMAL_ACCELERATION )),

        /** The fast refactor speed, used during hard mode. */
        FAST( settingsMan.getInt( Key.REFACTOR_FAST_SPEED_X ),
            settingsMan.getInt( Key.REFACTOR_FAST_SPEED_Y ),
            settingsMan.getInt( Key.REFACTOR_FAST_GRAVITY ),
            settingsMan.getInt( Key.REFACTOR_FAST_ACCELERATION )),

        /** The shift refactor speed, used during gravity shifts. */
        SHIFT( settingsMan.getInt( Key.REFACTOR_SHIFT_SPEED_X ),
            settingsMan.getInt( Key.REFACTOR_SHIFT_SPEED_Y ),
            settingsMan.getInt( Key.REFACTOR_SHIFT_GRAVITY ),
            settingsMan.getInt( Key.REFACTOR_SHIFT_ACCELERATION ));
        
        final private int horizontalSpeed;
        final private int verticalSpeed;
        final private int gravity;
        final private int acceleration;

        RefactorSpeed(int horizontal, int vertical, int gravity, int acceleration)
        {
            if ( horizontal == 0 )            
                throw new IllegalArgumentException( "Horzontal speed must > 0" );

            if ( vertical == 0 )
                throw new IllegalArgumentException( "Vertical speed must > 0" );

            if ( gravity < 0 )
                throw new IllegalArgumentException( "Gravity must be >= 0" );

            if ( acceleration < 0 )
                throw new IllegalArgumentException( "Acceleration must be >= 0" );

            this.horizontalSpeed = horizontal;
            this.verticalSpeed = vertical;
            this.gravity = gravity;
            this.acceleration = acceleration;
        }

        public int getHorizontalSpeed()
        {
            return horizontalSpeed;
        }

        public int getVerticalSpeed()
        {
            return verticalSpeed;
        }

        public int getGravity()
        {
            return gravity;
        }

        public int getAcceleration()
        {
            return acceleration;
        }

    }   

    /**
     * A reference to the game object.  
     * Used to get the game difficulty strategy.
     */
    final private Game game;

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
        if (game == null)
            throw new IllegalArgumentException("Game cannot be null");

        this.game = game;
        this.resetState();
    }

    final public void resetState()
    {
        // Set the refactor speeds to their defaults.
        this.speed = this.game
                .getDifficulty()
                .getStrategy()
                .getRefactorSpeed();

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
    public void setRefactorSpeed(RefactorSpeed speed)
    {
        if ( speed == null )
        {
            throw new IllegalArgumentException( "Speed cannot be null" );
        }

        CouchLogger.get().recordMessage( this.getClass(),
                String.format( "Speed set to %s", speed.toString() ) );

        this.speed = speed;        
    }

    public RefactorSpeed getRefactorSpeed()
    {
        return this.speed;
    }

    /**
     * Start a refactor with the given speed.
     * 
     * @param speed
     */
    public void startRefactor()
    {
        // Set the refactor flag.
        this.activateRefactor = true;        
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
        if (game == null)
            throw new IllegalArgumentException( "Game must not be null" );

        if (hub == null)
            throw new IllegalArgumentException( "Hub must not be null" );

        // Reset the finished flag.
        this.finishedRefactor = false;

        // See if we need to activate the refactor.
        if ( activateRefactor )
        {
            // Hide piece.
            //hub.pieceMan.hidePieceGrid();

            // Start down refactor.                           
            this.refactorAnimationList =
                    hub.boardMan.startVerticalShift( speed.getVerticalSpeed(), speed.getGravity() );

            // Add to the animation manager.
            // No need to worry about removing them, that'll happen
            // automatically when they are done.
            hub.gameAnimationMan.addAll( refactorAnimationList );

            // Set the refactor in progress flag.
            this.refactorVerticalInProgress = true;

            // Clear flag.
            clearRefactor();
        }

        // See if we're down refactoring.
        if ( this.refactorVerticalInProgress )
        {
            handleVerticalRefactor( hub );
        }

        // See if we're left refactoring.
        if ( this.refactorHorizontalInProgress )
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

        if ( done )
        {
            // Clear the animation list.
            refactorAnimationList = null;

            // Clear down flag.
            refactorVerticalInProgress = false;

            // Synchronize board.
            hub.boardMan.synchronize();

            // Start left refactor.
            refactorAnimationList =
                    hub.boardMan.startHorizontalShift( speed.getHorizontalSpeed(), speed.getAcceleration() );

            // Add to the animation manager.
            // No need to worry about removing them, that'll happen
            // automatically when they are done.
            hub.gameAnimationMan.addAll( refactorAnimationList );

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

        if ( done )
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

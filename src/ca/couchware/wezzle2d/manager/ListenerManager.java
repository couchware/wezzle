/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that holds the entire game state. It holds
 * listener lists for each stat in the game. When a stat changes
 * all the listeners in the list are updated.
 * 
 * Implements the singleton pattern and the observer pattern.
 * 
 * @author kgrad
 */
public class ListenerManager implements IListenerComponent
{
    
    // The single listener manager.
    private final static ListenerManager single = new ListenerManager();    
    
    // The listener lists.
    private List<IScoreListener> scoreListenerList;
    private List<ILevelListener> levelListenerList;
    private List<IMoveListener>  moveListenerList;
    private List<ILineListener>  lineListenerList;    
        
    /**
     * private constructor to ensure only a single entity ever exists.
     */
    private ListenerManager()
    {
        scoreListenerList = new ArrayList<IScoreListener>();
        levelListenerList = new ArrayList<ILevelListener>();
        moveListenerList  = new ArrayList<IMoveListener>();
        lineListenerList  = new ArrayList<ILineListener>();
    }
    
    /**
     * Get the one instance of this manager.
     * 
     * @return The only instance of the listener manager in the whole wide world.
     */
    public static ListenerManager get()
    {
        return single;
    }    
    
    /**
     * Register a score listener.
     * 
     * @param listener The listener to register.
     */    
    public void registerScoreListener(IScoreListener listener)
    {
        // If we try to add a second listener, blow up.
        if (scoreListenerList.contains(listener))
        {
            throw new IllegalArgumentException("Listener already registered!");        
        }
        
        scoreListenerList.add(listener);
    }
    
    /**
     * Register a level listener.
     * @param listener The listener to register.
     */    
    public void registerLevelListener(ILevelListener listener)
    {
        // If we try to add a second listener.
        if (levelListenerList.contains(listener))        
        {
            throw new IllegalStateException("Listener already registered!");        
        }
        
        levelListenerList.add(listener);
    }
    
    /**
     * Register the listener.
     * @param listener The listener to register.
     */    
    public void registerMoveListener(IMoveListener listener)
    {
        // If we try to add a second listener.
        if (moveListenerList.contains(listener))        
        {
            throw new IllegalStateException("Listener already registered!");
        }
        
        moveListenerList.add(listener);
    }
    
    /**
     * Register the listener.
     * @param listener The listener to register.
     */    
    public void registerLineListener(ILineListener listener)
    {
          // If we try to add a second listener.
        if (lineListenerList.contains(listener))
        {
            throw new IllegalStateException("Listener already registered!");
        }
        
        lineListenerList.add(listener);
    }
    
    /**
     * Notify all score listeners.
     * @param e The event.
     */    
    public void notifyScoreListener(ScoreEvent e, IListenerComponent.GameType gameType)
    {
        for (IScoreListener listener : scoreListenerList)
        {
            listener.handleScoreEvent(e, gameType);
        }
    }
    
    /**
     * Notify all level listeners.
     * @param e The event.
     */    
    public void notifyLevelListener(LevelEvent e)
    {
        for (ILevelListener listener : levelListenerList)
        {
            listener.handleLevelEvent(e);
        }
    }
    
    /**
     * Notify all move listeners.
     * @param e The event.
     */    
    public void notifyMoveListener(MoveEvent e, IListenerComponent.GameType gameType)
    {
        for (IMoveListener listener : moveListenerList)
        {
            listener.handleMoveEvent(e, gameType);
        }
    }
    
    /**
     * Notify all line listeners.
     * @param e The event.
     */    
    public void notifyLineListener(LineEvent e, IListenerComponent.GameType gameType)
    {
        for (ILineListener listener : lineListenerList)
        {
            listener.handleLineEvent(e, gameType);
        }
    }

}

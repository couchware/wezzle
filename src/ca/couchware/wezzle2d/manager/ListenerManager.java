/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.event.*;
import java.util.WeakHashMap;

/**
 * A class that holds the entire game state. It holds
 * listener lists for each stat in the game. When a stat changes
 * all the listeners in the list are updated.
 * 
 * implements the singleton pattern and the observer pattern.
 * 
 * Uses weakhashmaps to handle potential memory leaks.
 * 
 * @author kgrad
 */
public class ListenerManager implements IListenerComponent
{
    // The single listener manager.
    private final static ListenerManager listenerMan = new ListenerManager();
    
    
    // The listener lists.
    private WeakHashMap<IScoreListener, String> scoreListenerList;
    private WeakHashMap<ILevelListener, String> levelListenerList;
    private WeakHashMap<IMoveListener, String> moveListenerList;
    private WeakHashMap<ILineListener, String> lineListenerList;
    
    
    
    /**
     * private constructor to ensure only a single entity ever exists.
     */
    private ListenerManager()
    {
        scoreListenerList = new WeakHashMap<IScoreListener, String>();
        levelListenerList = new WeakHashMap<ILevelListener, String>();
        moveListenerList = new WeakHashMap<IMoveListener, String>();
        lineListenerList = new WeakHashMap<ILineListener, String>();
    }
    
    /**
     * Get the one instance of this manager.
     * @return listenerMan.
     */
    public static ListenerManager get()
    {
        return listenerMan;
    }    
    
    /**
     * Register a score listener.
     * @param listener The listener to register.
     */    
    public void registerScoreListener(IScoreListener listener)
    {
        // If we try to add a second listener, blow up.
        if (scoreListenerList.containsKey(listener))        
            throw new IllegalArgumentException("Listener already registered!");        
        
        scoreListenerList.put(listener, "Score");
    }
    
    /**
     * Register a level listener.
     * @param listener The listener to register.
     */    
    public void registerLevelListener(ILevelListener listener)
    {
          // If we try to add a second listener.
        if (levelListenerList.containsKey(listener))        
            throw new IllegalStateException("Listener already registered!");        
        
        levelListenerList.put(listener, "Level");
    }
    
    /**
     * Register the listener.
     * @param listener The listener to register.
     */    
    public void registerMoveListener(IMoveListener listener)
    {
          // If we try to add a second listener.
        if (moveListenerList.containsKey(listener))        
            throw new IllegalStateException("Listener already registered!");        
        
        moveListenerList.put(listener, "Move");
    }
    
    /**
     * Register the listener.
     * @param listener The listener to register.
     */    
    public void registerLineListener(ILineListener listener)
    {
          // If we try to add a second listener.
        if (lineListenerList.containsKey(listener))
        {
            throw new IllegalStateException("Adding a second move listener");
        }
        
        lineListenerList.put(listener, "Line");
    }
    
    /**
     * Notify all score listeners.
     * @param e The event.
     */    
    public void notifyScoreListener(ScoreEvent e, IListenerComponent.GameType gameType)
    {
        for (IScoreListener listener : scoreListenerList.keySet())
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
        for (ILevelListener listener : levelListenerList.keySet())
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
        for (IMoveListener listener : moveListenerList.keySet())
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
        for (ILineListener listener : lineListenerList.keySet())
        {
            listener.handleLineEvent(e, gameType);
        }
    }

}

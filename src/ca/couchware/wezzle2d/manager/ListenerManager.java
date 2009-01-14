/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.event.CollisionEvent;
import ca.couchware.wezzle2d.event.GameEvent;
import ca.couchware.wezzle2d.event.ICollisionListener;
import ca.couchware.wezzle2d.event.IGameListener;
import ca.couchware.wezzle2d.event.ILevelListener;
import ca.couchware.wezzle2d.event.ILineListener;
import ca.couchware.wezzle2d.event.IListener;
import ca.couchware.wezzle2d.event.IMoveListener;
import ca.couchware.wezzle2d.event.IPieceListener;
import ca.couchware.wezzle2d.event.IScoreListener;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.event.LineEvent;
import ca.couchware.wezzle2d.event.MoveEvent;
import ca.couchware.wezzle2d.event.PieceEvent;
import ca.couchware.wezzle2d.event.ScoreEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A class that holds the entire game state. It holds
 * listener lists for each stat in the game. When a stat changes
 * all the listeners in the list are updated.
 * 
 * Implements the singleton pattern and the observer pattern.
 *  
 * @author cmckay
 * @author kgrad
 */
public class ListenerManager
{
    
    /** The different game types. */
    public enum GameType
    {
        /** A normal game. */
        GAME,        
        
        /** A tuorial game. */
        TUTORIAL
    }  
    
    /** The different classes of listener. */
    public enum Listener
    {
        COLLISION,
        GAME,
        LEVEL,
        LINE,
        MOVE,
        PIECE,
        SCORE        
    }
    
    /** The single listener manager. */
    private final static ListenerManager SINGLE = new ListenerManager();            
    
    /** The score listener list. */
    private Map<Listener, List<IListener>> listenerMap;
            
    /**
     * Private constructor to ensure only a single instance ever exists.
     */
    private ListenerManager()
    {
        // Make the listener map.
        listenerMap = new EnumMap<Listener, List<IListener>>(Listener.class);
        
        // Make all the listener lists.
        for (Listener t : Listener.values())
        {
            listenerMap.put(t, new ArrayList<IListener>());
        }
    }
    
    /**
     * Get the one instance of this manager.
     * 
     * @return The only instance of the listener manager in the whole wide world.
     */
    public static ListenerManager get()
    {
        return SINGLE;
    }            
    
    public void registerListener(Listener listenerType, IListener listener)
    {
        if (listenerMap.get(listenerType).contains(listener) == true)
        {
            throw new IllegalArgumentException("Listener already registered!");
        }
        
        listenerMap.get(listenerType).add(listener);
    }       
    
    /**
     * Notify all score listeners.
     * 
     * @param e The event.
     */    
    public void notifyScoreIncreased(ScoreEvent e)
    {
        List<IListener> list = listenerMap.get(Listener.SCORE);
        
        for (IListener listener : list)
        {
            ((IScoreListener) listener).scoreIncreased(e);
        }
    }
    
    /**
     * Notify all score listeners.
     * 
     * @param e The event.
     */    
    public void notifyScoreChanged(ScoreEvent e)
    {
        List<IListener> list = listenerMap.get(Listener.SCORE);
        
        for (IListener listener : list)
        {
            ((IScoreListener) listener).scoreChanged(e);
        }
    }
    
    public void notifyTargetScoreChanged(ScoreEvent e)
    {
        List<IListener> list = listenerMap.get(Listener.SCORE);
        
        for (IListener listener : list)
        {
            ((IScoreListener) listener).targetScoreChanged(e);
        }
    }
    
    /**
     * Notify all level listeners.
     * 
     * @param e The event.
     */    
    public void notifyLevelChanged(LevelEvent e)
    {
        List<IListener> list = listenerMap.get(Listener.LEVEL);
        
        for (IListener listener : list)
        {
            ((ILevelListener) listener).levelChanged(e);
        }
    }
    
    /**
     * Notify all move listeners.
     * 
     * @param e The event.
     */    
    public void notifyMoveCommitted(MoveEvent e, GameType gameType)
    {
        List<IListener> list = listenerMap.get(Listener.MOVE);
        
        for (IListener listener : list)
        {
            ((IMoveListener) listener).moveCommitted(e, gameType);
        }
    }
    
    /**
     * Notify all move listeners.
     * 
     * @param e The event.
     */    
    public void notifyMoveCompleted(MoveEvent e)
    {
        List<IListener> list = listenerMap.get(Listener.MOVE);
        
        for (IListener listener : list)
        {
            ((IMoveListener) listener).moveCompleted(e);
        }
    }
    
    /**
     * Notify all line listeners.
     * 
     * @param e The event.
     */    
    public void notifyLineConsumed(LineEvent e, GameType gameType)
    {
        List<IListener> list = listenerMap.get(Listener.LINE);
        
        for (IListener listener : list)
        {
            ((ILineListener) listener).lineConsumed(e, gameType);
        }
    }      

    public void notifyGameStarted(GameEvent e)
    {
        List<IListener> list = listenerMap.get(Listener.GAME);
        
        for (IListener listener : list)
        {
            ((IGameListener) listener).gameStarted(e);
        }
    }

    public void notifyGameReset(GameEvent e)
    {
        List<IListener> list = listenerMap.get(Listener.GAME);
        
        for (IListener listener : list)
        {
            ((IGameListener) listener).gameReset(e);
        }
    }

    public void notifyGameOver(GameEvent e)
    {
        List<IListener> list = listenerMap.get(Listener.GAME);
        
        for (IListener listener : list)
        {
            ((IGameListener) listener).gameOver(e);
        }
    }   
    
    public void notifyCollisionOccured(CollisionEvent e)
    {
        List<IListener> list = listenerMap.get(Listener.COLLISION);
         
        for (IListener listener : list)
        {
            ((ICollisionListener) listener).collisionOccured(e);
        }
         
    }
    
    public void notifyPieceAdded(PieceEvent e)
    {
        List<IListener> list = listenerMap.get(Listener.PIECE);
         
        for (IListener listener : list)
        {
            ((IPieceListener) listener).pieceAdded(e);
        }
    }
       
}

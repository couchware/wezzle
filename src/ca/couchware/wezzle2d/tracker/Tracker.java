/*
 * Wezzle
 * Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tracker;

import ca.couchware.wezzle2d.event.CollisionEvent;
import ca.couchware.wezzle2d.manager.ListenerManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Watches all the moves the player takes and keeps a record.  Used mostly
 * by the achievement system.
 * @author Cameron McKay
 */
public class Tracker
{

    /** The listener manager. */
    final private ListenerManager listenerMan;

    /** The game history. */
    final private List<Move> history = new ArrayList<Move>();

    /** The current move being tracked. */
    private List<Chain> chainList;

    /** The current chain being tracked. */
    private List<TileGroup> tileGroupList;

    /**
     * The private constructor.
     */
    private Tracker(ListenerManager listenerMan)
    {
        // Store reference to listener manager.
        this.listenerMan = listenerMan;

        // Add a chain list to hold the first move.
        this.chainList = new ArrayList<Chain>();

        // Add a tile group list to hold the first chain.
        this.tileGroupList = new ArrayList<TileGroup>();
    }

    /**
     * Create a new Tracker instance.
     * @return A new Tracker instance.
     */
    public static Tracker newInstance(ListenerManager listenerMan)
    {
        return new Tracker(listenerMan);
    }   

    public void record(List<? extends TileGroup> tileGroupList)
    {
        if (tileGroupList == null)
            throw new NullPointerException("TileGroup cannot be null!");

        if (!tileGroupList.isEmpty())
            this.tileGroupList.addAll(tileGroupList);
    }

    /**
     * Complete the current chain.  Automatically starts a new chain.
     * @return The chain that was just completed.
     */
    public Chain finishChain()
    {
        // Add the current move to the history.
        Chain chain = Chain.newInstance(tileGroupList);
        this.chainList.add(chain);

        // Notify all listeners that a collision might've occured.
        this.listenerMan.notifyCollisionOccured(new CollisionEvent(this, chain));

        // Create a new chain list for the next move.
        this.tileGroupList = new ArrayList<TileGroup>();

        // Return the move.
        return chain;
    }

    /**
     * Complete the current move.  Automatically starts a new move.
     * @return The move that was just completed.
     */
    public Move finishMove()
    {
        // Add the current move to the history.
        Move move = Move.newInstance(chainList);
        this.history.add(move);

        // Create a new chain list for the next move.
        this.chainList = new ArrayList<Chain>();

        // Return the move.
        return move;
    }

}

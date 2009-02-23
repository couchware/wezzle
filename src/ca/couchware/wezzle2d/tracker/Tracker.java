/*
 * Wezzle
 * Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Watches all the moves the player takes and keeps a record.  Used mostly
 * by the achievement system.
 * @author Cameron McKay
 */
public class Tracker
{
    /** The game history. */
    List<Move> history = new ArrayList<Move>();

    /** The current move being tracked. */
    private List<Chain> chainList;

    /**
     * The private constructor.
     */
    private Tracker()
    {
        // Add a chain list to hold the first move.
        this.chainList = new ArrayList<Chain>();
    }

    /**
     * Create a new Tracker instance.
     * @return A new Tracker instance.
     */
    public static Tracker newInstance()
    {
        return new Tracker();
    }

    /**
     * Add a new chain to the current move.
     * @param chain
     */
    public void track(Chain chain)
    {
        if (chain == null)
            throw new NullPointerException("Chain cannot be null!");

        this.chainList.add(chain);
    }

    /**
     * Complete the current move.  Automatically starts a new move.
     * @return The move that was just completed.
     */
    public Move completeMove()
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

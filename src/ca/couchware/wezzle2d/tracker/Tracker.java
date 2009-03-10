/*
 * Wezzle
 * Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tracker;

import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.Rule.NumeratorSubType;
import ca.couchware.wezzle2d.event.CollisionEvent;
import ca.couchware.wezzle2d.manager.ListenerManager;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * Get the last n moves from the history.
     * NOTE: Order is reversed. Last move is first.
     *
     * @param n The number of moves you would like to see.
     * @return An unmodifiable list of the last n moves in reverse order.
     */
    public List<Move> getHistory(int n)
    {
        if (n < 0)
            throw new IllegalArgumentException("n must be a positive integer");

        // Limit the size.
        if (n > history.size()) n = history.size();

        // Reverse the list.
        List<Move> historyClone = new ArrayList<Move>(history);
        Collections.reverse(historyClone);

        // Get the sublist.
        return Collections.unmodifiableList(history.subList(0, n));
    }

    /**
     * Get a mapping of all the counts for the numerator values in the list of
     * moves.
     *
     * This will go through the moves counting the number of rockets, stars,
     * gravity tiles, bombs, 2x, 3x, 4x, items, multipliers, collisions.
     *
     * @param moves The list of moves.
     * @return a mapping of numerator values to counts.
     */
    public Map<Rule.NumeratorSubType, Integer> getCounts(List<Move> moves)
    {
        // get a set of all tiles distinct.
        Set<Tile> tiles = new HashSet<Tile>();
        int lines = 0;
        int score = 0;

        for( Move m : moves)
        {
            lines += m.getNumLines();
            tiles.addAll(m.getTileSet());
            score += m.getScore();
        }


        //count items;
        int rocket = 0, gravity = 0, bomb = 0, star = 0, x2 = 0, x3 = 0, x4 = 0;

        for(Tile t : tiles)
        {
            TileType type = t.getType();

            switch(type)
            {
                case NORMAL:
                    break;

                case ROCKET:
                    rocket++;
                    break;

                case BOMB:
                    bomb++;
                    break;

                case STAR:
                    star++;
                    break;

                case GRAVITY:
                    gravity++;
                    break;

                case X2:
                    x2++;
                    break;

                case X3:
                    x3++;
                    break;

                case X4:
                    x4++;
                    break;

                default:
                    throw new RuntimeException("Unknown tile type.");
            }

        }

        // Create a mapping of SubType -> Value.
        Map<Rule.NumeratorSubType, Integer> countMap =
             new HashMap<Rule.NumeratorSubType, Integer>();

        countMap.put(NumeratorSubType.BOMB, bomb);
        countMap.put(NumeratorSubType.GRAVITY, gravity);
        countMap.put(NumeratorSubType.ITEMS, (rocket+bomb+gravity+star));
        countMap.put(NumeratorSubType.LINES, lines);
        countMap.put(NumeratorSubType.MULTIPLIERS, (x2+x3+x4));
        countMap.put(NumeratorSubType.ROCKET, rocket);
        countMap.put(NumeratorSubType.SCORE, score);
        countMap.put(NumeratorSubType.STAR, star);
        countMap.put(NumeratorSubType.X2, x2);
        countMap.put(NumeratorSubType.X3, x3);
        countMap.put(NumeratorSubType.X4, x4);
        
        return countMap;
    }

}

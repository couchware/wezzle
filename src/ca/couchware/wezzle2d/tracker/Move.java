/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tracker;

import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An immutable class that holds the information about a single move. A
 * move is a collection of cascades, which are collections of lines, which is
 * a collection of tiles.
 * 
 * @author kgrad
 */
public class Move
{
    
    /** The list of chains. */
    private List<Chain> chainList;

    /** The total score of this move */
    private int score;
    
    /**
     * Constructor private to ensure immutability.
     * @param cascades The cascades.
     */
    private Move(Collection<Chain> chainList, int score)
    {
        this.chainList = new ArrayList<Chain>(chainList);
        this.score = score;
    }
    
    /**
     * Static factory returns an immutible move object.
     * @param cascades  The cascades in the move.
     * @return The immutable move.
     */
    public static Move newInstance(Collection<Chain> chainList, int score)
    {
       return new Move(chainList, score);
    }


    public int getScore()
    {
        return this.score;
    }


        
    // -------------------------------------------------------------------------
    // Public Members
    // -------------------------------------------------------------------------
    
    /**
     * Get the cascades. To ensure immutability returns an unmodifiable list.
     * @return The list of cascades.
     */
    public List<Chain> getChainList()
    {
        return Collections.unmodifiableList(this.chainList);
    }

    /**
     * Get a list of all the tile types in a move.
     * @return An unmodifiable list of tile types.
     */
    public Set<Tile> getTileSet()
    {
        // We do this by first getting the chains, 
        // then getting all the lines in each chain,
        // then getting the tiles in the lines,
        // then  finally getting the tile types from the tiles.
        // The algorithm has a complexity of O(c*l*t) which in practice
        // will not be THAT large.
        Set<Tile> tileSet = new HashSet<Tile>();
        List<Chain> chains = this.getChainList();

        for ( Chain c : chains )
        {

            List<TileGroup> grpList = c.getTileGroupList();

            for(TileGroup tg : grpList)
            {
                tileSet.addAll(tg.getTiles());
            }
           
        }

        return Collections.unmodifiableSet(tileSet);
    }

 

    /**
     * Get the number of lines in this move.
     * @return
     */
    public int getNumLines()
    {
        int lineCount = 0;

        List<Chain> chains = this.getChainList();

        for(Chain c : chains)
        {
            lineCount += c.getLineList().size();
        }

        return lineCount;
    }
        
    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        int count = 1;
        
        for (Chain chain : chainList)
        {
            buffer.append(String.format("(Chain %d)\n%s", count, chain.toString()));
            count++;
        }
        
        return buffer.toString();
    }
   
}
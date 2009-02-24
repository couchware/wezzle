/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    
    /**
     * Constructor private to ensure immutability.
     * @param cascades The cascades.
     */
    private Move(Collection<Chain> chainList)
    {
        this.chainList = new ArrayList<Chain>(chainList);
    }
    
    /**
     * Static factory returns an immutible move object.
     * @param cascades  The cascades in the move.
     * @return The immutable move.
     */
    public static Move newInstance(Collection<Chain> chainList)
    {
       return new Move(chainList);
    }    
        
    // -------------------------------------------------------------------------
    // Public Members
    // -------------------------------------------------------------------------
    
    /**
     * Get the cascades. To ensure immutability returns an unmodifiable list.
     * @return The list of cascades.
     */
    public List getChainList()
    {
        return Collections.unmodifiableList(this.chainList);
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
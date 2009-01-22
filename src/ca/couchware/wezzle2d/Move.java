/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

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
    
    /**
     * The list of lines.
     */
    private List<Chain> move;
    
    /**
     * Constructor private to ensure immutability.
     * @param cascades The cascades.
     */
    private Move(List<Chain> cascades)
    {
        this.move = cascades;
    }
    
    /**
     * Static factory returns an immutible move object.
     * @param cascades  The cascades in the move.
     * @return The immutable move.
     */
    public static Move newInstance(List<Chain> cascades)
    {
       return new Move(cascades);
    }
    
        
    /**
     * Public interface.
     */
    
    /**
     * Get the cascades. To ensure immutability returns an unmodifiable list.
     * @return The list of cascades.
     */
    public List getCascades()
    {
        return Collections.unmodifiableList(this.move);
    }
    
    
    public String toString()
    {
        String out = "";
        int count = 1;
        
        for(Chain i : move)
        {
            out += "Chain " + count + ":\n" + i.toString();
            count++;
        }
        
        return out;
    }
}
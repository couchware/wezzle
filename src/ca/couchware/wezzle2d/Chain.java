/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import java.util.Collections;
import java.util.List;

/**
 * An immutable class that holds the information about a single cascade. A 
 * cascade is a collection of lines, which is a collection of tiles.
 * 
 * @author kgrad
 */
public class Chain
{
    
    /**
     * The list of lines.
     */
    private List<Line> chain;
    
    /**
     * Constructor private to ensure immutability.
     * @param lines The lines.
     */
    private Chain(List<Line> lines)
    {
        this.chain = lines;
    }
    
    /**
     * Static factory returns an immutible cascade object.
     * @param lines  The lines in the cascade.
     * @return The immutable cascade.
     */
    public static Chain newInstance(List<Line> lines)
    {
       return new Chain(lines);
    }
    
        
    /**
     * Public interface.
     */
    
    /**
     * Get the lines. To ensure immutability returns an unmodifiable list.
     * @return The list of lines.
     */
    public List getLines()
    {
        return Collections.unmodifiableList(this.chain);
    }
    
    public String toString()
    {
        String out = "";
        int count = 1;
        for(Line i : chain)
        {
            out += "Line " + count + ": [ " + i.toString() + " ]\n";
        }
        
        count++;
        
        return out;
        
    }
    
}
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
    
    /** The list of lines. */
    private List<Line> lineList;
    
    /**
     * Constructor private to ensure immutability.
     * @param lines The lines.
     */
    private Chain(List<Line> lineList)
    {
        this.lineList = lineList;
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
    
    // -------------------------------------------------------------------------
    // Public Members
    // -------------------------------------------------------------------------
    
    /**
     * Get the lines. To ensure immutability returns an unmodifiable list.
     * @return The list of lines.
     */
    public List getLineList()
    {
        return Collections.unmodifiableList(this.lineList);
    }
    
    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        int count = 1;

        for (Line line : lineList)
        {
            buffer.append(String.format("  (Line %d) [ %s ]\n", count, line.toString()));
            count++;
        }
                        
        return buffer.toString();
    }
    
}
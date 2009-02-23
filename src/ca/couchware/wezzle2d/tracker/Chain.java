/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tracker;

import java.util.ArrayList;
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
    private List<? extends TileGroup> tileGroupList;
    
    /**
     * Constructor private to ensure immutability.
     * @param lines The lines.
     */
    private Chain(List<? extends TileGroup> tileGroupList)
    {
        this.tileGroupList = new ArrayList<TileGroup>(tileGroupList);
    }
    
    /**
     * Create a new a chain instance.
     * @param lines  The lines in the cascade.
     * @return The immutable cascade.
     */
    public static Chain newInstance(List<? extends TileGroup> tileGroupList)
    {
       return new Chain(tileGroupList);
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
        return Collections.unmodifiableList(this.tileGroupList);
    }
    
    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        int count = 1;

        for (TileGroup grp : tileGroupList)
        {
            buffer.append(String.format("  (Line %d) [ %s ]\n", count, grp.toString()));
            count++;
        }
                        
        return buffer.toString();
    }

    public int size()
    {
        return this.tileGroupList.size();
    }
    
}
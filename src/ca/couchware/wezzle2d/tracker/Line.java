/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tracker;

import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.util.StringUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An immutable class that holds a line of tiles.
 * @author kgrad
 */
public class Line implements TileGroup
{
    
    /** The list of tiles. */
    private List<Tile> tileList;
    
    /**
     * Constructor private to ensure immutability.
     * @param line The tiles.
     */
    private Line(Collection<Tile> tileList)
    {
        this.tileList = new ArrayList<Tile>(tileList);
    }
    
    /**
     * Static factory returns an immutible line object.
     * @param tiles  The tiles in the line.
     * @return The immutable line.
     */
    public static Line newInstance(Collection<Tile> tiles)
    {
       return new Line(tiles);
    }
    
    // -------------------------------------------------------------------------
    // Public Members
    // -------------------------------------------------------------------------
    
    /**
     * Get the tiles. To ensure immutability returns an unmodifiable list.
     * @return The list of tiles.
     */
    public List getTileList()
    {
        return Collections.unmodifiableList(this.tileList);
    }
    
    @Override
    public String toString()
    {
        return StringUtil.join(tileList, ", ");
    }
}

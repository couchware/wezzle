/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.tile.Tile;
import java.util.Collections;
import java.util.List;

/**
 * An immutable class that holds a line of tiles.
 * @author kgrad
 */
public class Line 
{
    
    /**
     * The list of tiles.
     */
    private List<Tile> line;
    
    /**
     * Constructor private to ensure immutability.
     * @param line The tiles.
     */
    private Line(List<Tile> tiles)
    {
        this.line = tiles;
    }
    
    /**
     * Static factory returns an immutible line object.
     * @param tiles  The tiles in the line.
     * @return The immutable line.
     */
    public static Line newInstance(List<Tile> tiles)
    {
       return new Line(tiles);
    }
    
    /**
     * Public interface.
     */
    
    /**
     * Get the tiles. To ensure immutability returns an unmodifiable list.
     * @return The list of tiles.
     */
    public List getTiles()
    {
        return Collections.unmodifiableList(this.line);
    }
    
    public String toString()
    {
        String out = "";
        for (Tile i : line)
        {
            out += i.toString() + ", ";
        }
        // delete the last comma
        out = out.substring(0, out.length()-2);
        return out;
    }
}

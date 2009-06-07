/*
 * Wezzle
 * Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tracker;

import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.util.StringUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class for holding the tiles affected by a tile effect like a rocket
 * or a bomb explosion.
 * @author Cameron McKay
 */
public class TileEffect implements TileGroup
{
    /** The tile that caused the effect. */
    private Tile causeTile;

    /** The list of tiles. */
    private List<Tile> tileList;

    /**
     * Constructor private to ensure immutability.
     * @param line The tiles.
     */
    private TileEffect(Tile causeTile, Collection<Tile> tileList)
    {
        this.causeTile = causeTile;
        this.tileList = new ArrayList<Tile>(tileList);
    }

    /**
     * Static factory returns an immutible line object.
     * @param tiles  The tiles in the line.
     * @return The immutable line.
     */
    public static TileEffect newInstance(Tile causeTile, Collection<Tile> tileList)
    {
       return new TileEffect(causeTile, tileList);
    }

    // -------------------------------------------------------------------------
    // Public Members
    // -------------------------------------------------------------------------

    /**
     * Get the tiles. To ensure immutability returns an unmodifiable list.
     * @return The list of tiles.
     */
    public List<Tile> getTileList()
    {
        return Collections.unmodifiableList(this.tileList);
    }

    public Tile getCauseTile()
    {
        return this.causeTile;
    }

    @Override
    public String toString()
    {
        return this.causeTile.getType().toString() + " => "
                + (tileList.isEmpty() ? "*Nothing*" : StringUtil.join(tileList, ", "));
    }


    public Set<Tile> getTiles()
    {
       Set<Tile> tiles = new HashSet<Tile>();
       for(Tile t : tileList)
       {
           tiles.add(t);
       }

       return tiles;
    }
}

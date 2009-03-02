/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tracker;

import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.Node;
import java.util.ArrayList;
import java.util.Collection;
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

    /** The chain tree. */
    private Node<Tile> chainTree;
    
    /**
     * Constructor private to ensure immutability.
     * @param lines The lines.
     */
    private Chain(Collection<? extends TileGroup> tileGroupList)
    {
        this.tileGroupList = new ArrayList<TileGroup>(tileGroupList);
        this.buildTree();
    }
    
    /**
     * Create a new a chain instance.
     * @param lines  The lines in the cascade.
     * @return The immutable cascade.
     */
    public static Chain newInstance(Collection<? extends TileGroup> tileGroupList)
    {
       return new Chain(tileGroupList);
    }

    // -------------------------------------------------------------------------
    // Private Members
    // -------------------------------------------------------------------------

    private void buildTree()
    {
        // Find all the lines and turn them into one continuous list.
        this.chainTree = new Node<Tile>(null);
        Node<Tile> root = this.chainTree;

        for ( TileGroup group : this.tileGroupList )
        {
            if (group instanceof Line)
            {
                Line line = (Line) group;
                root.addChildren(line.getTileList());
                continue;
            }

            if (group instanceof TileEffect)
            {
                TileEffect effect = (TileEffect) group;
                Node<Tile> node = root.find(effect.getCauseTile());
                assert node != null;
                node.addChildren(effect.getTileList());
            }
        } // end for

        //CouchLogger.get().recordMessage(this.getClass(), root.toString());
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

    /**
     * Get the chain tree.
     * @return
     */
    public Node<Tile> getTree()
    {
        return chainTree;
    }

    /**
     * Get the size of the chain.
     * @return
     */
    public int size()
    {
        return tileGroupList.size();
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
    
}
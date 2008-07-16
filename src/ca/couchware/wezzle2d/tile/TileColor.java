/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.util.Util;

/**
 * The list of possible tile colours.
 * 
 * @author cdmckay
 */
public enum TileColor
{
    BLUE, 
    GREEN, 
    PURPLE, 
    RED, 
    YELLOW, 
    BLACK, 
    BROWN, 
    WHITE;
            
    @Override
    public String toString()
	{
		String s = super.toString();
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }  
    
    public static TileColor getRandomColor(int max)
	{
        TileColor[] colors = values();
        
        assert(max > 0);
        assert(max <= colors.length);
        
		return colors[Util.random.nextInt(max)];
	}	
} 



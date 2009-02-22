/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
        Set<TileColor> emptySet = Collections.emptySet();
        return getRandomColor(max, emptySet);
    }
    
    public static TileColor getRandomColor(int max, Set<TileColor> filterSet)
	{


        TileColor[] colors = values();
        List<TileColor> colorList = new ArrayList<TileColor>(Arrays.asList(colors));
        
        if ( max <= 0 || max > colorList.size())
               throw new IllegalArgumentException("invalid value for max.");
                       
        
        colorList.removeAll(EnumSet.range(colors[max], colors[colors.length - 1]));
        colorList.removeAll(filterSet);
        
		return colorList.get(Util.random.nextInt(colorList.size()));
	}	
} 



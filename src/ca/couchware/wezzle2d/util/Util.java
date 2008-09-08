/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

import java.util.Random;

/**
 * A utilty static class providing various convenience methods.
 * 
 * @author cdmckay
 */
public class Util
{	    	
	
	/**
	 * The random number generator.
	 */
	public static Random random = new Random(System.currentTimeMillis());		
	
    /**
     * A method for squaring integers.
     * 
     * @int x The integer to square.
     */
    public static int sq(int x)
    {
        return x * x;
    }
    
    /**
     * A method for squaring doubles.
     * 
     * @int x The double to square.
     */
    public static double sq(double x)
    {
        return x * x;
    }
    
	/**
	 * A method for transposing square 2D arrays in-place.
	 */
	public static void transpose2d(Object[][] array)
	{
		assert array != null;
		
		for (int j = 0; j < array[0].length; j++)		
			for (int i = j + 0; i < array.length; i++)			
				swap2d(array, i, j, j, i);						
	}
	
	public static int pseudoTranspose(int i, int columns, int rows)
	{
		return ((columns * i) % (columns * rows)) 
			+ (i / rows); 
	}

	/**
	 * A method for swapping matrix cells in-place.
	 * @param array The target array.
	 * @param c1 The 1st swap column.
	 * @param r1 The 1st swap row.
	 * @param c2 The 2nd swap column.
	 * @param r2 The 2nd swap row.
	 */
	public static void swap2d(Object[][] array, int c1, int r1, int c2, int r2)
	{
		assert array != null;
		
		Object swap = null;
		swap = array[c1][r1];
		array[c1][r1] = array[c2][r2];
		array[c2][r2] = swap;
	}
    
    public static int scaleInt(int fromLower, int fromUpper,
            int toLower, int toUpper, int fromNumber)
    {
        assert(fromUpper > fromLower);
        assert(toUpper > toLower);
        
        if (fromNumber < fromLower)
            return toLower;
        else if (fromNumber > fromUpper)
            return toUpper;
        
        int fromRange = fromUpper - fromLower;
        double fromFraction = (double) (fromNumber - fromLower)
                / (double) fromRange;
        
        int toRange = toUpper - toLower;
        
        return toLower + (int) (((double) toRange) * fromFraction);
    }
    
    public static double scaleDouble(double fromLower, double fromUpper,
            double toLower, double toUpper, double fromNumber)
    {
        assert(fromUpper > fromLower);
        assert(toUpper > toLower);
        
        if (fromNumber < fromLower)
            return toLower;
        else if (fromNumber > fromUpper)
            return toUpper;
        
        double fromRange = fromUpper - fromLower;
        double fromFraction = (double) (fromNumber - fromLower)
                / (double) fromRange;
        
        double toRange = toUpper - toLower;
        
        return toLower + (((double) toRange) * fromFraction);
    }
    
    /**
     * Counts the number of line breaks (i.e. '\n' characters) in the passed
     * string.  Throws exception on null strings.
     * 
     * @return The number of line breaks.
     */
    public static int countNewLines(String text)
    {
        if (text == null)
            throw new IllegalStateException(
                    "Attempted to count newlines on a null string.");                
        
        int n = 0;
        
        for (int i = 0; i < text.length(); i++)
            if (text.charAt(i) == '\n')
                n++;
        
        return n;
    }
    
    /**
     * Extracts the file extension from a file with the name.ext format.
     * 
     * @param path
     * @return The .ext part of the path.
     */
    public static String getFileExtension(final String path)
    {
        String fileName = new java.io.File(path).getName();
        
        String ext = "";
        
        if (fileName.lastIndexOf(".") != -1)        
            ext = fileName.substring(fileName.lastIndexOf(".") + 1);        
        
        return ext;
    }       
    
}
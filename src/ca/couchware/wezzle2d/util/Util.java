/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

import java.util.Random;
import java.util.Set;

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
     * @param x The integer to square.
     */
    public static int sq(int x)
    {
        return x * x;
    }
    
    /**
     * A method for squaring doubles.
     * 
     * @param x The double to square.
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
    
    /**
     * Scales a value.
     *  
     * @param fromLower
     * @param fromUpper
     * @param toLower
     * @param toUpper
     * @param fromNumber
     * @return
     */
    public static int scaleInt(int fromLower, int fromUpper,
            int toLower, int toUpper, int fromNumber)
    {        
        assert fromUpper >= fromLower;
        assert toUpper   >= toLower;
        
        if (fromNumber < fromLower)
        {
            return toLower;
        }
        else if (fromNumber > fromUpper)
        {
            return toUpper;
        }
        
        int fromRange = fromUpper - fromLower;
        if (fromRange == 0) return 0;
        
        double fromFraction = (double) (fromNumber - fromLower)
                / (double) fromRange;
        
        int toRange = toUpper - toLower;
        if (toRange == 0) return 0;
        
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
     * Compare two doubles within a given epsilon.
     * 
     * @param a
     * @param b
     * @param eps
     */
    public static boolean equalsDouble(double a, double b, double eps)
    {
        // See if they're exactly equal first.
        if (a == b) return true;
        
        // If the difference is less than epsilon, treat as equal.
        return Math.abs(a - b) < eps;
    }
    
    /** The default epsilon value. */
    final static double EPSILON = 0.0000001;

    /** 
     * Compare two doubles, using default epsilon
     * 
     * @param a
     * @param b
     */
    public static boolean equalsDouble(double a, double b)
    {
        return equalsDouble(a, b, EPSILON);
    }
      
    public static <T> T getRandomElement(Set<T> set)
    {
        int rnd = random.nextInt(set.size());
        
        int count = 0;
        for (T t : set)
        {
            if (count == rnd) return t;
            count++;
        }
        
        throw new RuntimeException("This should not happen.");
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
    
    public static String padString(String str, char ch, int length)
    {
        StringBuffer buffer = new StringBuffer(str);
        int strLength  = buffer.length();
        
        if (length > 0 && length > strLength)
        {
            for (int i = strLength; i < length; i++)
            {
                buffer.append(ch);                
            } // end for
        } // end if
        
        return buffer.toString();
    }
    
    /**
     * Converts a string passed in with the format "THE_SWIFT_RED_FOX" to
     * the format "The.Swift.Red.Fox".  Used mainly for the settings file.
     * 
     * @param str
     * @return
     */
    public static String toDotFormat(String str)
    {
        StringBuffer buffer = new StringBuffer();
        
        for (int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            
            if (i == 0) 
                buffer.append(Character.toUpperCase(ch));
            else if (ch == '_') 
            {                               
                buffer.append('.');
            }  
            else
            {
                if (buffer.charAt(i - 1) == '.')
                    buffer.append(Character.toUpperCase(ch));                          
                else
                    buffer.append(Character.toLowerCase(ch));                          
            }
        }
        
        return buffer.toString();
    }
    
     /**
     * Converts a string passed in with the format "The.Swift.Red.Fox" to
     * the format "THE_SWIFT_RED_FOX".  Used mainly for the settings file.
     * 
     * @param str
     * @return
     */
    public static String toUnderScoreFormat(String str)
    {
        return str.replace('.', '_').toUpperCase();
    }
    
}
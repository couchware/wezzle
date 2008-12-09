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
    public static boolean equals(double a, double b, double eps)
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
    public static boolean equals(double a, double b)
    {
        return equals(a, b, EPSILON);
    }
    
    /**
     * Determines the minimum of the passed integers.
     * 
     * @param numbers
     * @return
     */
    public static int minimumInt(int ... numbers)
    {
        if (numbers.length == 0) 
            throw new IllegalArgumentException("Requires at least 1 integer.");
        
        int minimum = numbers[0];
        for (int i = 0; i < numbers.length; i++)
        {
            if (numbers[i] < minimum)
                minimum = numbers[i];
        }
        
        return minimum;
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
     * Counts the number of line breaks (i.e. '\n' characters) in the passed
     * string.  Throws exception on null strings.
     * 
     * @return The number of line breaks.
     */
    public static int countLineBreaks(String text)
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
                buffer.setCharAt(i - 1, Character.toUpperCase(buffer.charAt(i - 1)));
                buffer.append('.');
            }                            
        }
        
        return buffer.toString();
    }
    
}
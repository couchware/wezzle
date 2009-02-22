/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

/**
 * A collection of utility methods for use with numbers.
 * 
 * @author cdmckay
 */
public class NumUtil 
{

    /**
     * A method for squaring integers.
     * 
     * @param x The integer to square.
     */
    public static int sqInt(int x)
    {
        return x * x;
    }
    
    /**
     * A method for squaring doubles.
     * 
     * @param x The double to square.
     */
    public static double sqDouble(double x)
    {
        return x * x;
    }
    
    /**
     * Sums an array of integers and returns it.
     * 
     * @param array
     * @return
     */
    public static int sumIntArray(int[] array)
    {
        int sum = 0;
        for (int i : array) sum += i;
        return sum;
    }
    
    /**
     * Sums an array of doubles and returns it.
     * 
     * @param array
     * @return
     */
    public static double sumDoubleArray(double[] array)
    {
        double sum = 0;
        for (double d : array) sum += d;
        return sum;
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
        if(fromUpper < fromLower)
            throw new IllegalArgumentException("fromUpper < fromLower.");
        if(toUpper < toLower)
            throw new IllegalArgumentException("toUpper < toLower");
       
        
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
        if(fromUpper < fromLower)
            throw new IllegalArgumentException("fromUpper < fromLower.");
        if(toUpper < toLower)
            throw new IllegalArgumentException("toUpper < toLower");
        
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
    
}

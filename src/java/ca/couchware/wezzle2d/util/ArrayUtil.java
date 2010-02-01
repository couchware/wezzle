/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.util;

import java.util.Collection;

/**
 * A utilty static class providing various convenience methods.
 * 
 * @author cdmckay
 */
public class ArrayUtil
{    
    /**
     * A method for transposing square 2D arrays in-place.
     */
    public static void transpose2d(Object[][] array)
    {
        if ( array == null )
        {
            throw new IllegalArgumentException( "Array must not be null" );
        }

        for ( int j = 0; j < array[0].length; j++ )
        {
            for ( int i = j + 0; i < array.length; i++ )
            {
                swap2d( array, i, j, j, i );
            }
        }
    }

    /**
     * Rotates an index.  I think.
     * 
     * @param i
     * @param columns
     * @param rows
     * @return
     */
    public static int pseudoTranspose(int i, int columns, int rows)
    {
        return ((columns * i) % (columns * rows)) + (i / rows);
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
        if ( array == null )
        {
            throw new IllegalArgumentException( "Array must not be null" );
        }

        Object swap = array[c1][r1];
        array[c1][r1] = array[c2][r2];
        array[c2][r2] = swap;
    }

    /**
     * Gets a random element from a collection.
     * 
     * @param <T>
     * @param set
     * @return
     */
    public static <T> T getRandomElement(Collection<T> collection)
    {
        int rnd = NumUtil.random.nextInt( collection.size() );

        int count = 0;
        for ( T t : collection )
        {
            if ( count == rnd )
            {
                return t;
            }
            count++;
        }

        throw new RuntimeException( "This should not happen" );
    }

}

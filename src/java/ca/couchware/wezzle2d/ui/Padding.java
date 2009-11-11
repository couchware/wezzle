/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

/**
 * A class for describing the amount of padding that some abstract UI element
 * has.
 * 
 * @author cdmckay
 */
public class Padding 
{
    
    final public static Padding NONE = new Padding(0, 0, 0, 0);
    
    final private int left;
    final private int right;
    final private int top;
    final private int bottom;
    
    private Padding(int left, int right, int top, int bottom)
    {
        this.left   = left;
        this.right  = right;
        this.top    = top;
        this.bottom = bottom;
    }
            
    public static Padding newInstance(int all)
    {
        return new Padding(all, all, all, all);
    }
    
    public static Padding newInstance(int left, int right)
    {
        return new Padding(left, right, 0, 0);
    }
    
    public static Padding newInstance(int left, int right, int top, int bottom)
    {
        return new Padding(left, right, top, bottom);
    }

    public int getBottom()
    {
        return bottom;
    }

    public int getLeft()
    {
        return left;
    }

    public int getRight()
    {
        return right;
    }

    public int getTop()
    {
        return top;
    }
    
}

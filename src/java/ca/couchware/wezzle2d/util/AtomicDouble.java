/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A convenience class that implements an atomic double.
 * 
 * @author cdmckay
 */
public class AtomicDouble 
{
    /**
     * The atomic long that encodes the double.
     */
    private AtomicLong value = new AtomicLong();
    
    /**
     * The constructor.
     */
    public AtomicDouble(double val)
    {
        // Convert the double to a long an store it.
        set(val);
    }
    
    public AtomicDouble()
    {
        // Set it to the default of 0.0.
        set(0.0);
    }
    
    public double get()
    {
        return Double.longBitsToDouble(this.value.get());
    }
    
    public void set(double val)
    {
       this.value.set(Double.doubleToLongBits(val));
    }
}

/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

/**
 * An interface for implementing the Builder design pattern.  Used to
 * make constructors that have many arguments more clear.
 * 
 * From Effective Java, Item 2.
 * 
 * @author cdmckay
 */
public interface IBuilder<T>
{
    public T build();
}

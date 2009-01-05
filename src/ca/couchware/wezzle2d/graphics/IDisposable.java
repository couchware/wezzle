/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.graphics;

/**
 * An interface for describing components that can (or must) be disposed.
 * 
 * @author cdmckay
 */
public interface IDisposable 
{
    /**
     * Dispose the component.
     */
    public void dispose();
}

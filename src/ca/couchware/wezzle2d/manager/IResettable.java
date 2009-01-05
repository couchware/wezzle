/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

/**
 * An interface for describing components capable of having a resettable
 * state.
 * 
 * @author cdmckay
 */
public interface IResettable 
{
    /**
     * Resets the state of the manager to it's initial state.
     */   
    public void resetState();
}

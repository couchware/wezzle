/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

/**
 * An interface for creating entities that can run fragments of code when
 * clicked.
 * 
 * @author cdmckay
 */
public interface IClickable 
{
    
    /**
     * The method that is run when the entity is clicked.
     */
    public void onClick();
    
    /**
     * A method for getting a runnable that is run when the entitiy is clicked.
     */
    public Runnable getClickAction();
    
    /**
     * A method for setting a runnable that is run when the entity is clicked.
     */
    public void setClickAction(Runnable r);
    
}

/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

/**
 *
 * @author cdmckay
 */
public abstract class MenuOption
{
    
    /**
     * The menu that this option is attached to.
     */
    private Menu parentMenu;
    
    /**
     * The menu that this option opens.  If null, then this menu option
     * opens no menu (but may still have a function).
     */
    private Menu targetMenu;      
    
    
    
}

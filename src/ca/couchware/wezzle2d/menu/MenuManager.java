/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

/**
 * A manager for loading and transitioning between the game menus.
 * 
 * @author cdmckay
 */
public class MenuManager
{
    
    /**
     * The different menus that can be loaded by the menu manager.
     */
    public static enum Type
    {
        MAIN,
        PLAY_NOW,
        TUTORIAL,
        OPTIONS,
        UPGRADE,
        ACHIEVEMENTS,
        HIGH_SCORES,
        EXIT
    }
    
    /**
     * The current menu type.  Defaults to main menu.
     */
    private Type type = Type.MAIN;
    
    
}

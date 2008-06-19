/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.ui.button.*;
import ca.couchware.wezzle2d.ui.group.Group;
import ca.couchware.wezzle2d.util.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The menu manager is used to manage the menues in Wezzle.  In particular, it
 * helps to link Buttons to Groups.
 * 
 * @author cdmckay
 */
public class GroupManager 
{
    /**
     * The side class.
     */
    final public static int SIDE = 0;
    
    /**
     * The game over class.
     */
    final public static int GAME_OVER = 1;
        
    /**
     * The list of groups currently being shown.
     */
    protected LinkedList<Entry> entryList;
          
    /**
     * A reference to the layer manager.
     */
    protected LayerManager layerMan;
    
    /**
     * A reference to the piece manager.
     */
    protected PieceManager pieceMan;
    
    /**
     * The constructor.
     */
    public GroupManager(LayerManager layerMan, PieceManager pieceMan)
    {
        entryList = new LinkedList<Entry>();        
        
        this.layerMan = layerMan;
        this.pieceMan = pieceMan;
    }     
    
    public void showGroup(BooleanButton button, Group showGroup, 
            int className)
    {
        // Hide all the current groups.
        for (Entry e : entryList)                    
            e.getGroup().setVisible(false);
       
        // Add the group on top.
        entryList.addFirst(new Entry(showGroup, button, className)); 
        
        // Make the group visible.
        showGroup.setVisible(true);
        showGroup.setActivated(true);
        
        // Make the button activated.
        if (button != null)
            button.setActivated(true);
        
        layerMan.hide(Game.LAYER_TILE);
        layerMan.hide(Game.LAYER_EFFECT);
    }
    
    public void hideGroup(int className)
    {
        // Go through the entry list, removing all entries with the
        // passed class name.
        for (Iterator<Entry> it = entryList.iterator(); it.hasNext(); )            
        {
            // The entry we are looking at.
            Entry e = it.next();
            
            if (e.getClassName() == className)
            {
                e.getGroup().setVisible(false);
                e.getGroup().setActivated(false);
                e.getGroup().resetButtons();
                
                if (e.getButton() != null)
                    e.getButton().setActivated(false);
                
                it.remove();
            }
        }
                    
        // Make the top of the list visible.
        if (entryList.isEmpty() == true)
        {
            pieceMan.clearMouseButtons();
            layerMan.show(Game.LAYER_TILE);
            layerMan.show(Game.LAYER_EFFECT);
        }
        else
            entryList.getFirst().getGroup().setVisible(true);
    }        
    
    public boolean isActivated()
    {
        // If the group list is not empty, then at least one group must be 
        // showing.
        return !entryList.isEmpty();
    }  

    /**
     * This is an inner class represented an entry in 
     * the the group linked list.
     */
    protected class Entry
    {               
        /**
         * The group associated with this entry.
         */
        final protected Group group;
        
        /**
         * The button that was used to open this group.
         */
        final protected BooleanButton button;
        
        /**
         * The class of the group.  This is used to hide or show many groups
         * at once.
         */        
        final protected int className;  
        
        /**
         * The constructor.
         */
        public Entry(Group group, BooleanButton button, int className)
        {
            // Set the references.
            this.group = group;
            this.button = button;
            this.className = className;
        }

        public BooleanButton getButton()
        {
            return button;
        }

        public int getClassName()
        {
            return className;
        }

        public Group getGroup()
        {
            return group;
        }                
    }
    
}

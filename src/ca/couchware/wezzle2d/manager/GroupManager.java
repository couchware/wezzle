/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.group.IGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The menu manager is used to manage the menues in Wezzle.  In particular, it
 * helps to link Buttons to Groups.
 * 
 * @author cdmckay
 */
public class GroupManager implements IResettable
{
    
    public enum Class
    {
        GAME_OVER,
        PAUSE,
        OPTIONS,
        HIGH_SCORE
    }
    
    public enum Layer
    {
        BOTTOM,
        MIDDLE,
        TOP
    }                
    
    /**
     * This  linked list holds all groups that have called the register()
     * method.  This list is useful for performing commands on all the groups,
     * such as running updateLogic().
     */
    protected List<IGroup> groupList;       
    
    /**
     * The list of groups currently being shown.
     */
    protected List<Entry> entryList;    
          
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
    private GroupManager(LayerManager layerMan, PieceManager pieceMan)
    {
        groupList = new ArrayList<IGroup>();
        entryList = new LinkedList<Entry>();        
        
        this.layerMan = layerMan;
        this.pieceMan = pieceMan;                
    }             

    public void resetState()
    {
        hideAllGroups();
    }
    
    // Public API.
    public static GroupManager newInstance(LayerManager layerMan, PieceManager pieceMan)
    {
        return new GroupManager(layerMan, pieceMan);
    }
    
    public void showGroup(IButton button, IGroup showGroup, 
            Class groupClass, Layer layer)
    {
        // Remove all groups that aren't part of the passed class.
        // Hide all existing members of the passed clas.
        for (Iterator<Entry> it = entryList.iterator(); it.hasNext(); )            
        {
            // The entry we are looking at.
            Entry e = it.next();                   
            
            if (e.getLayer() == layer)
            {
                if (e.getGroupClass() == groupClass)
                    e.getGroup().setVisible(false);
                else
                {
                    deactivateEntry(e);
                    it.remove();
                }
            }
            else
            {
                e.getGroup().setVisible(false);
            }                
        }
       
        // Add the group on top.
        entryList.add(0, new Entry(showGroup, button, groupClass, layer)); 
        
        // Make the group visible.
        showGroup.setVisible(true);
        showGroup.setActivated(true);
        
        // Make the button activated.
        if (button != null)
            button.setActivated(true);
        
        layerMan.hide(LayerManager.Layer.TILE);
        layerMan.hide(LayerManager.Layer.EFFECT);
        pieceMan.hidePieceGrid();
        
        CouchLogger.get().recordMessage(this.getClass(), "Groups open: " + entryList.size());
    }    
    
    /**
     * This method makes an entry's group invisible, deactivates it, 
     * resets it's buttons, and deactivates the button that called it.
     * 
     * @param entry
     */
    protected void deactivateEntry(Entry entry)
    {
        entry.getGroup().setVisible(false);
        entry.getGroup().setActivated(false);
        entry.getGroup().clearChanged();

        if (entry.getButton() != null)
            entry.getButton().setActivated(false);
    }
    
    public void hideGroup(Class cls, Layer layer)
    {
        // Go through the entry list, removing all entries with the
        // passed class name.
        for (Iterator<Entry> it = entryList.iterator(); it.hasNext(); )            
        {
            // The entry we are looking at.
            Entry e = it.next();
            
            if (e.getLayer() == layer && e.getGroupClass() == cls)
            {
                // Deactivate the entry.
                deactivateEntry(e);     
                
                // Remove it.
                it.remove();
            }
        }
                    
        // Make the top of the list visible.
        if (entryList.isEmpty() == true)
        {
            pieceMan.clearMouseButtonSet();
            pieceMan.showPieceGrid();
            layerMan.show(LayerManager.Layer.TILE);
            layerMan.show(LayerManager.Layer.EFFECT);
        }
        else
            entryList.get(0).getGroup().setVisible(true);
        
        CouchLogger.get().recordMessage(this.getClass(), "Groups open: " + entryList.size());
    }   
    
    public void hideGroup(IGroup group)
    {
        // Remove the group.
        for (Iterator<Entry> it = entryList.iterator(); it.hasNext(); )            
        {
            // The entry we are looking at.
            Entry e = it.next();
            
            if (e.getGroup() == group)
            {
                deactivateEntry(e);
                it.remove();
                break;
            }
        }
                
        // Make the top of the list visible.
        if (entryList.isEmpty() == true)
        {
            pieceMan.clearMouseButtonSet();
            pieceMan.showPieceGrid();
            layerMan.show(LayerManager.Layer.TILE);
            layerMan.show(LayerManager.Layer.EFFECT);
        }
        else
            entryList.get(0).getGroup().setVisible(true);
        
        CouchLogger.get().recordMessage(this.getClass(), "Groups open: " + entryList.size());
    }
    
    /**
     * Hides all currently shown groups.
     */
    public void hideAllGroups()
    {
        for (Iterator<Entry> it = entryList.iterator(); it.hasNext(); )            
        {
            // The entry we are looking at.
            Entry e = it.next();                       
            deactivateEntry(e);
            it.remove();                       
        }
        
        pieceMan.clearMouseButtonSet();
        pieceMan.hidePieceGrid();
        layerMan.show(LayerManager.Layer.TILE);
        layerMan.show(LayerManager.Layer.EFFECT);
    }        
    
    /**
     * Registers this group with the Group linked list.
     * 
     * @param group The group to register.
     */
    public void register(IGroup group)
    {
        // Add the group to the static linked list.
        groupList.add(group);
    }
    
    /**
     * Update the logic of all groups if they have detected clicks.
     * 
     * @param game The game state.
     */
    public void updateLogic(Game game, ManagerHub hub)
    {
        for (IGroup group : groupList)
        {
            if (group.isActivated() == true
                    && group.controlChanged() == true)
            {
                group.updateLogic(game, hub);
                group.clearChanged();
            }
        } // end for
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
    protected static class Entry
    {               
        /**
         * The group associated with this entry.
         */
        final protected IGroup group;
        
        /**
         * The button that was used to open this group.
         */
        final protected IButton button;
        
        /**
         * The class of the group.  This is used to hide or show many groups
         * at once.
         */        
        final protected Class groupClass;
        
        /**
         * The layer that the group is on.  This is mainly used to keep
         * the game over screen open under a bunch of menues.
         */
        final protected Layer layer;
        
        /**
         * The constructor.
         */
        public Entry(IGroup group, IButton button, 
                Class groupClass, Layer layer)
        {
            // Set the references.
            this.group = group;
            this.button = button;
            this.groupClass = groupClass;
            this.layer = layer;
        }

        public IGroup getGroup()
        {
            return group;
        }    
        
        public IButton getButton()
        {
            return button;
        }

        public Class getGroupClass()
        {
            return groupClass;
        }

        public Layer getLayer()
        {
            return layer;
        }                                   
    }    
    
}

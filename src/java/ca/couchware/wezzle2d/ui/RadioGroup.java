/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * A UI element for representing a group of RadioItems.
 * Typically used for allowing the user to select among mutually exclusive
 * choices.
 * 
 * For exampe:
 * 
 *   Music Theme:
 *   (*) Type A   ( ) Type B   ( ) Type C
 * 
 * @author cdmckay
 */
public class RadioGroup extends AbstractEntity implements IMouseListener
{
    
    /**
     * The game window the radio group is attached to.
     */
    private IWindow window;
    
    /**
     * Has the radio group been changed?
     */
    private boolean changed = false;
    
    /**
     * The shape of the group.
     */
    private ImmutableRectangle shape;
    
    /** 
     * List of radio items.
     */    
    private List<RadioItem> itemList;   
    
    /**
     * The key of currently selected item.
     */
    private int selectedIndex;
    
    /**
     * The amount of space between each radio item.
     */
    final private int itemSpacing;
    
    /**
     * The constructor.
     * 
     * @param builder
     */    
    private RadioGroup(Builder builder)
    {
        // Add the window reference.
        this.window = builder.win;
        
        // Add list reference.
        //this.itemList = builder.itemList;
        this.itemList = builder.itemList;
        
        // Set the pad.
        this.itemSpacing = builder.itemSpacing;
        
        // Set the coordinates.
        this.x = builder.x;
        this.y = builder.y;
        this.x_ = x;
        this.y_ = y;
        
        // Determine the width and height.
        this.width = 0;
        this.height = 0;
        for (RadioItem item : itemList)
        {
            item.setPosition(x + width, y);
            this.width += itemSpacing + item.getWidth();
            this.height = (item.getHeight() > this.height) 
                    ? item.getHeight()
                    : this.height;
        }       
        this.width -= itemSpacing;
        
        this.width_ = width;
        this.height_ = height;               
        
        // Determine offsets.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);
        
        // Set the shape.
        this.shape = new ImmutableRectangle(x + offsetX, y + offsetY, 
                width, height);
        
        // Set opacity and visibility.
        this.opacity = builder.opacity;
        this.visible = builder.visible;        
        
        // Adjust all the radio items.
        for (RadioItem item : itemList)
        {
            item.translate(offsetX, offsetY);        
            item.setOpacity(opacity);
            item.setVisible(visible);
        }
        
        // Select a key.
        if (builder.selectedIndex != -1)
        {
            this.setSelectedIndex(builder.selectedIndex);
            this.changed = false;
        }
    }
    
    public static class Builder implements IBuilder<RadioGroup>
    {
        // Required values.  
        private final IWindow win;
        private int x;
        private int y;     
        
        // The radio item map.
        private final List<RadioItem> itemList;
        private int selectedIndex = -1;
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);                
        private int opacity = 100;
        private boolean visible = true;        
        private int itemSpacing = 20;                       
        
        public Builder(IWindow win, int x, int y)
        {         
            this.win = win;
            this.x = x;
            this.y = y;
            
            this.itemList = new ArrayList<RadioItem>();
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
               
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
                        
        public Builder opacity(int val)
        { opacity = val; return this; }
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public Builder itemSpacing(int val)
        { itemSpacing = val; return this; }
             
        public Builder add(RadioItem val)        
        { itemList.add(val); return this; }
        
        public Builder add(RadioItem val, boolean selected)        
        { 
            itemList.add(val); 
            
            if (selected == true)
            {
                selectedIndex = itemList.size() - 1; 
            }
            
            return this;
        }
        
        public RadioGroup build()
        {
            RadioGroup group = new RadioGroup(this);
            
            if (visible == true)
                win.addMouseListener(group);
            
            return group;
        }                
    }
    
    @Override
    public void setOpacity(int opacity)
    {
        super.setOpacity(opacity);
        
        for (RadioItem item : itemList)
            item.setOpacity(opacity);
    }
    
    @Override
    public void setVisible(boolean visible)
    {
        // Ignore if visibility not changed.
        if (this.visible == visible)
            return;
        
        // Invoke super.
        super.setVisible(visible);
        
        for (RadioItem item : itemList)
                item.setVisible(visible);
        
        // Add or remove listener based on visibility.
        if (visible == true)
        {                           
            //LogManager.recordMessage("Added");
            window.addMouseListener(this);
        }
        else
        {                            
            window.removeMouseListener(this);            
        }                   
    }
    
    @Override
    public boolean isDirty()
    {
        for (RadioItem item : itemList)
            if (item.isDirty() == true)
                return true;
        
        return false;
    }
    
    @Override
    public void setDirty(boolean dirty)
    {
         for (RadioItem item : itemList)
             item.setDirty(dirty);
    }

    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    final public void setSelectedIndex(int selectedIndex)
    {        
        if (this.selectedIndex == -1 || this.selectedIndex != selectedIndex)
            changed = true;
        
        this.selectedIndex = selectedIndex;
        
        for (int i = 0; i < itemList.size(); i++)
        {
            this.itemList.get(i).setActivated(i == selectedIndex);
        }           
    }        
            
    public boolean changed()
    {
        boolean val = changed;
        changed = false;
        return val;
    }
    
    public boolean changed(boolean preserve)
    {
        if (preserve == true)
            return changed;
        else
            return changed();
    }
    
    @Override
    public boolean draw()
    {
        if (visible == false)
            return false;
        
        for (RadioItem item : itemList)
            item.draw();
        
        return true;
    }

    public void mouseClicked(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mouseEntered(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mouseExited(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mousePressed(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mouseReleased(MouseEvent e)
    {
        // See if the mouse was released over this.
        if (shape.contains(e.getPosition()) == true)
        {            
            // See which one it was released over.
            for (int i = 0; i < itemList.size(); i++)
            {
                if (itemList.get(i).getShape().contains(e.getPosition()))
                {
                    setSelectedIndex(i);                       
                    break;
                }
            }
        }

//        LogManager.recordMessage(itemList.size() + "");
//        for (RadioItem item : itemList)
//            LogManager.recordMessage("isActivated = " + item.isActivated());
    }

    public void mouseDragged(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mouseMoved(MouseEvent e)
    {
//        if (shape.contains(e.getPosition()))
//            LogManager.recordMessage("I'm over the area!");
    }     
    
    public void mouseWheel(MouseEvent e)
    {
        // Intentionally left blank.
    }
    
    @Override
    public void dispose()
    {
        // Stop listening to the mouse events.
        if (this.visible && !this.disabled)
            window.removeMouseListener(this);
        
        // Dispose of the items too.
        for (RadioItem item : itemList)
            item.dispose();
    }
    
}
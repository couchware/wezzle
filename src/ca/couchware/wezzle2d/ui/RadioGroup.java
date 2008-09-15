/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.IGameWindow;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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
public class RadioGroup<T extends Enum<T>> extends AbstractEntity implements IMouseListener
{
    
    /**
     * The game window the radio group is attached to.
     */
    private IGameWindow window;
    
    /**
     * Has the radio group been changed?
     */
    private boolean changed = false;
    
    /**
     * The shape of the group.
     */
    private ImmutableRectangle shape;
    
    /** 
     * Map of radio items. 
     */    
    private Map<T, RadioItem> itemMap;   
    
    /**
     * The key of currently selected item.
     */
    private T selectedKey;
    
    /**
     * The amount of space between each radio item.
     */
    final private int pad;
    
    /**
     * The constructor.
     * 
     * @param builder
     */    
    private RadioGroup(Builder<T> builder)
    {
        // Add the window reference.
        this.window = builder.window;
        
        // Add list reference.
        //this.itemList = builder.itemList;
        this.itemMap = builder.itemMap;
        
        // Set the pad.
        this.pad = builder.pad;
        
        // Set the coordinates.
        this.x = builder.x;
        this.y = builder.y;
        this.x_ = x;
        this.y_ = y;
        
        // Determine the width and height.
        this.width = 0;
        this.height = 0;
        for (RadioItem item : itemMap.values())
        {
            item.setXYPosition(x + width, y);
            this.width += pad + item.getWidth();
            this.height = (item.getHeight() > this.height) 
                    ? item.getHeight()
                    : this.height;
        }       
        this.width -= pad;
        
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
        for (RadioItem item : itemMap.values())
        {
            item.translate(offsetX, offsetY);        
            item.setOpacity(opacity);
            item.setVisible(visible);
        }
        
        // Select a key.
        if (builder.selectedKey != null)
        {
            this.setSelectedKey(builder.selectedKey);
            this.changed = false;
        }
    }
    
    public static class Builder<E extends Enum<E>> implements IBuilder<RadioGroup>
    {
        // Required values.  
        private final IGameWindow window;
        private int x;
        private int y;     
        
        // The radio item map.
        private final Map<E, RadioItem> itemMap;
        private E selectedKey;
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);                
        private int opacity = 100;
        private boolean visible = true;        
        private int pad = 20;                       
        
        public Builder(int x, int y, Class<E> keyType)
        {         
            this.window = ResourceFactory.get().getGameWindow();
            this.x = x;
            this.y = y;
            
            this.itemMap = new EnumMap<E, RadioItem>(keyType);
        }
        
//        public Builder(RadioGroup radioGroup)
//        {                       
//            this.window = radioGroup.window;
//            this.x = radioGroup.x;
//            this.y = radioGroup.y;
//            this.alignment = radioGroup.alignment.clone();            
//            this.opacity = radioGroup.opacity;                        
//            this.visible = radioGroup.visible;   
//            this.pad = radioGroup.pad;
//            
//            for (RadioItem item : radioGroup.itemList)
//                this.itemList.add(new RadioItem.Builder(item).end());
//        }
        
        public Builder<E> x(int val) { x = val; return this; }        
        public Builder<E> y(int val) { y = val; return this; }
               
        public Builder<E> alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
                        
        public Builder<E> opacity(int val)
        { opacity = val; return this; }
        
        public Builder<E> visible(boolean val) 
        { visible = val; return this; }
        
        public Builder<E> pad(int val)
        { pad = val; return this; }
             
        public Builder<E> add(E key, RadioItem val)        
        { itemMap.put(key, val); return this; }
        
        public Builder<E> add(E key, RadioItem val, boolean selected)        
        { itemMap.put(key, val); if (selected) selectedKey = key; return this; }
        
        public RadioGroup<E> end()
        {
            RadioGroup<E> group = new RadioGroup<E>(this);
            
            if (visible == true)
                window.addMouseListener(group);            
            
            return group;
        }                
    }
    
    @Override
    public void setOpacity(int opacity)
    {
        super.setOpacity(opacity);
        
        for (RadioItem item : itemMap.values())
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
        
        for (RadioItem item : itemMap.values())
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
        for (RadioItem item : itemMap.values())
            if (item.isDirty() == true)
                return true;
        
        return false;
    }

    public T getSelectedKey()
    {
        return selectedKey;
    }

    final public void setSelectedKey(T selectedKey)
    {        
        if (this.selectedKey == null || !(this.selectedKey ==  selectedKey))
            changed = true;
        
        this.selectedKey = selectedKey;
        
        for (T key : itemMap.keySet())
        {
            if (key == selectedKey)
            {
                this.itemMap.get(key).setActivated(true);
                continue;
            }
            
            this.itemMap.get(key).setActivated(false);
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
        
        for (RadioItem item : itemMap.values())
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
            for (T key : itemMap.keySet())
                if (itemMap.get(key).getShape().contains(e.getPosition()))
                {
                    setSelectedKey(key);                       
                    break;
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
}
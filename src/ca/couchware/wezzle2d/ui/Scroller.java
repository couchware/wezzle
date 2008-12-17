/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.IGameWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import java.util.EnumSet;
import java.util.List;

/**
 * A UI element that displays a subset of a list of strings and allows the
 * user to scroll up and down through them.
 * 
 * @author cdmckay
 */
public class Scroller extends AbstractEntity
{

    /** The game window. */
    private IGameWindow window;
    
    /** The list of options in the scroller. */
    private List<IButton> optionList;
    
    private Scroller(Builder builder)
    {
        
    }
    
    public static class Builder implements IBuilder<Scroller>
    {
        // Required values.  
        private final IGameWindow window;
        private int x;
        private int y;     
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);              
        private int opacity = 100;
        private int width = 200;
        private int height = 200;       
        private boolean visible = true;
        
        public Builder(int x, int y)
        {            
            this.window = ResourceFactory.get().getGameWindow();
            this.x = x;
            this.y = y;
        }
        
        public Builder(Scroller scroller)
        {            
            this.window = scroller.window;
            this.x = scroller.x;
            this.y = scroller.y;
            this.alignment = scroller.alignment.clone();                 
            this.opacity = scroller.opacity;                        
            this.width = scroller.width;
            this.visible = scroller.visible;            
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
               
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
                        
        public Builder opacity(int val)
        { opacity = val; return this; }
        
        public Builder width(int val)
        { width = val; return this; }
        
        public Builder height(int val)
        { height = val; return this; }               
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
            
        public Builder add(String option)
        {
            return this;
        }
        
        public Builder addAll(List<String> optionList)
        {
            return this;
        }
        
        public Scroller end()
        {
            Scroller scroller = new Scroller(this);                       
            return scroller;
        }                
    }
    
    @Override
    public boolean draw()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }    

}

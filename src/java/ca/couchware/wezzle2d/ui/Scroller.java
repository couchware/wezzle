/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * A UI element that displays a subset of a list of strings and allows the
 * user to scroll up and down through them.
 * 
 * @author cdmckay
 */
public class Scroller extends AbstractEntity implements IMouseListener
{

    /** The game window. */
    final private IWindow window;
    
    /** The shape of the group. */
    private ImmutableRectangle shape;
    
    /** Has the scroller been changed? */
    private boolean changed = false;
    
    /** The text size. */
    private int textSize;
    
    /** The padding. */
    private Padding padding;
    
    /** The list of options in the scroller. */
    final private List<ScrollerRow> rowList;
    
    /** The slider bar used to scroll the scroller. */
    final private SliderBar scrollBar;      
    
    /** The current offset of the scroller. */
    private int scrollOffset = 0;        
    
    /** The number of achievements showing at once in the scroller. */
    final private int rows;   
    
    /** The amount of space between each label in the scoller. */
    final private int spacing;
    
    /** The key of currently selected item. */
    private int selectedIndex;
    
    private Scroller(Builder builder)
    {
        // Set some values from the builder.
        this.window = builder.window;
        this.x = builder.x;
        this.y = builder.y;
        this.width  = builder.width;
        this.height = builder.height;               
        this.padding = builder.padding;
        this.rows = builder.rows;
        this.spacing = (this.height - padding.getTop() - padding.getBottom()) / this.rows;      
        this.textSize = builder.textSize;
                
        // Set to visible.
        this.visible = builder.visible;                
                
        // Set default anchor.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);
               
        // Set the shape.
        this.shape = new ImmutableRectangle(x + offsetX, y + offsetY, 
                width, height);
        
        // Create the button list.
        this.rowList = new ArrayList<ScrollerRow>();
        
        // Create all the buttons.
        IButton templateButton = new Button.Builder(0, 0)
                .text("").textSize(textSize)
                .build();
        
        for (String optionText : builder.optionList)
        {
            IButton button = new Button.Builder((Button) templateButton)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .autoWidth(true)
                .normalOpacity(0)
                .activeOpacity(100)
                .visible(false)
                .text(optionText)
                .build();

            ITextLabel label = new ResourceFactory.LabelBuilder(0, 0)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT))
                .visible(false)
                .text("").size(12)
                .build();

            rowList.add( new ScrollerRow(button, label) );
        }
        
        // Set the selected index.
        this.selectedIndex = builder.selectedIndex;
        if (builder.selectedIndex != -1)            
        {
            rowList.get(selectedIndex).getButton().setActivated(true);
        }            
            
        // Create the scroll bar.
        this.scrollBar = new SliderBar.Builder(
                    this.x + this.offsetX + this.width - padding.getRight(),
                    this.y + this.offsetY + this.height / 2
                )
                .height(this.height - padding.getTop() - padding.getBottom())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .orientation(SliderBar.Orientation.VERTICAL)
                .virtualRange(0, Math.max(0, rowList.size() - rows))
                .virtualValue(0)                
                .build();
        
        // Make the scroller listen for slider bar changes.
        this.scrollBar.addSliderBarListener(new SliderBar.ISliderBarListener() 
        {
            public void sliderBarChanged(int virtualValue)
            {
                // Convert to an integer.
                int offset = virtualValue;

                // See if it's different (enough).
                if (offset != scrollOffset)
                {
                    // Update the offset.
                    scrollOffset = offset;

                    // Update the button position.
                    showButtons();
                }              
            }

            public void sliderBarPressed(int virtualValue)
            {

            }

            public void sliderBarReleased(int virtualValue)
            {
                
            }
        });
        
        // Update the button positions.
        if (this.visible) showButtons();
        else hideButtons();
    }
    
    public static class Builder implements IBuilder<Scroller>
    {
        // Required values.  
        private final IWindow window;
        private int x;
        private int y;     
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);  
        private int width = 400;
        private int height = 200;       
        private boolean visible = true;
        private int textSize = 18;
        private List<String> optionList = new ArrayList<String>(); 
        private int rows = 5;        
        private Padding padding = Padding.NONE;
        private int selectedIndex = -1;
        
        public Builder(int x, int y)
        {            
            this.window = ResourceFactory.get().getWindow();
            this.x = x;
            this.y = y;
        }               
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
               
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
                               
        public Builder width(int val)
        { width = val; return this; }
        
        public Builder height(int val)
        { height = val; return this; }               
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
            
        public Builder textSize(int val)
        { textSize = val; return this; }
        
        public Builder padding(Padding val)                
        { padding = val; return this; }
        
        public Builder rows(int val)
        { this.rows = val; return this; }
        
        public Builder add(String option, boolean selected)
        {
            optionList.add(option);
            
            if (selected == true)
            {
                selectedIndex = optionList.size() - 1;
            }
            
            return this;
        }

        public Builder add(String option)
        {
            return add(option, false);
        }
        
        public Builder selectedIndex(int val)
        { 
            if (val >= optionList.size())
            {
                throw new IllegalArgumentException("Selected index is larger than option list.");
            }
            
            selectedIndex = val; return this; 
        }
        
        public Builder addAll(List<String> optionList)
        {
            optionList.addAll(optionList);
            return this;
        }
        
        public Scroller build()
        {
            Scroller scroller = new Scroller(this);         
            
            // Add the mouse listener.
            if (visible)
                window.addMouseListener(scroller);                                    
                                    
            return scroller;
        }                
    }

    /**
     * Updates the buttons in the scroller, setting their positions
     * and visibility.
     */
    private void showButtons()
    {
        // Make sure the offset isn't too high.
        if ( scrollOffset > (rowList.size() - rows) )
            throw new IllegalStateException("Offset too high.");
                    
        // Make all labels invisible.
        for (ScrollerRow r : rowList)
        {
            r.getButton().setVisible(false);
            r.getLabel().setVisible(false);
        }
        
        // Move the labels into the right position.
        for (int i = 0; i < rows; i++)
        {
            IButton button = rowList.get(i + scrollOffset).getButton();
            button.setX(this.x + offsetX + padding.getLeft());
            button.setY(this.y + offsetY + padding.getTop() + (spacing / 2) + spacing * i);
            button.setVisible(true);

            ITextLabel label = rowList.get(i + scrollOffset).getLabel();
            label.setX(getX() + getWidth() - 50);
            label.setY(this.y + offsetY + padding.getTop() + (spacing / 2) + spacing * i);
            label.setVisible(true);
        }
    }

    private void hideButtons()
    {
        for ( ScrollerRow r : this.rowList )
        {
            r.getButton().setVisible(false);
            r.getLabel().setVisible(false);
        }
    }
    
    @Override
    public void setVisible(boolean visible)
    {
        // Ignore if visibility not changed.
        if (this.visible == visible)
            return;
        
        // Invoke super.
        super.setVisible(visible);             

        // Add or remove listener based on visibility.
        if (visible)
        {                                       
            window.addMouseListener(this);

            // Show the buttons.
            showButtons();
        }
        else
        {                            
            window.removeMouseListener(this);

            // Hide all the buttons.
            hideButtons();
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
        if (preserve)
            return changed;
        else
            return changed();
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
        
        for (int i = 0; i < rowList.size(); i++)
        {
            if (i != selectedIndex)          
            {
                this.rowList.get(i).getButton().setActivated(false);

            }            
        } // end for            
    }      
    
    public Color getColor(int index)
    {
        if(index < 0 || index >= rowList.size())
            throw new IllegalArgumentException("index out of range.");
        
        IButton button = rowList.get(index).getButton();
        
        if (button instanceof Button)
        {
            return ((Button) button).getTextColor();
        }                
        else
        {
            throw new RuntimeException("No button instance found");
        }
    }
    
    public void setColor(int index, Color color)
    {
        if(index < 0 || index >= rowList.size())
            throw new IllegalArgumentException("index out of range.");
        
        IButton button = rowList.get(index).getButton();
        
        if (button instanceof Button)
        {
            ((Button) button).setTextColor(color);
        }                
        else
        {
            throw new RuntimeException("No button instance found");
        }                
    }

    public void setLabelColor(int index, Color color)
    {
        if(index < 0 || index >= rowList.size())
            throw new IllegalArgumentException("index out of range.");

        ITextLabel label = rowList.get(index).getLabel();

        if (label instanceof ITextLabel)
        {
            ((ITextLabel) label).setColor(color);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }

    public void setLabelText(int index, String text)
    {
        if(index < 0 || index >= rowList.size())
            throw new IllegalArgumentException("index out of range.");

        ITextLabel label = rowList.get(index).getLabel();

        if (label instanceof ITextLabel)
        {
            ((ITextLabel) label).setText(text);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public boolean draw()
    {
        // Don't draw if not visible.
        if (visible == false)
            return false;        
        
        for (ScrollerRow row : rowList)
        {
            row.getButton().draw();
            row.getLabel().draw();
        }
        scrollBar.draw();
        
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
        if ( shape.contains(e.getPosition()) )
        {            
            // See which one it was released over.
            for (int i = 0; i < rowList.size(); i++)
            {
                IButton button = rowList.get(i).getButton();
                
                if (button.isVisible()
                        && button.getShape().contains(e.getPosition()))
                {
                    setSelectedIndex(i);                       
                    break;
                }
            }
        } // end if
    }

    public void mouseDragged(MouseEvent e)
    {
    }

    public void mouseMoved(MouseEvent e)
    {
    }    
    
    public void mouseWheel(MouseEvent e)
    {
        int signum = -e.getDeltaWheel() / Math.abs(e.getDeltaWheel());
        int offset = scrollOffset + signum;
        
        if (offset >= 0 && offset <= rowList.size() - rows)
        {
            //scrollOffset = offset;
            scrollBar.setVirtualValue(offset);            
        }
    }
    
    @Override
    public void dispose()
    {
        // Stop listening to the mouse events.
        if (this.visible && !this.disabled)
            window.removeMouseListener(this);
    }

    private class ScrollerRow
    {
        private IButton button;
        private ITextLabel label;

        public ScrollerRow(IButton b, ITextLabel l)
        {
            this.button = b;
            this.label = l;
        }

        public IButton getButton()
        {
            return button;
        }

        public ITextLabel getLabel()
        {
            return label;
        }
    }

}

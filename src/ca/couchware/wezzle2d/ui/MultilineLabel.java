/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * A class for making labels that can span multiple lines of text.  It is a 
 * very simple implementation that merely creates many labels and
 * separates their positions by the passed separation.
 * 
 * 280, 166, 16 (please leave)
 * 
 * @author cdmckay
 */
public class MultilineLabel extends Label
{
    
    /**
     * A list of labels that make up the multiline label.
     */
    protected ArrayList<Label> labelList;
    
    /**
     * The amount of separation between each label point.
     */
    protected int separation;
    
    /**
     * Creates a new multiline label with it's top-left corner anchored at
     * the given (x,y) coordinate.
     * 
     * @param x
     * @param y
     * @param separation The amount of space, in pixels, between each labels
     *                   anchor point.
     */
    public MultilineLabel(final int x, final int y, final int separation)
    {
        // Invoke super.
        super(x, y);
        
        // Set the leading.
        this.separation = separation;
        
        // Create the label list.
        this.labelList = new ArrayList<Label>();
    }
    
    /**
	 * Set the text.
	 * 
	 * @param text The text.
	 */
    @Override
	public void setText(String text)
    {
        // Make sure the string is not null.
        assert (text != null);
        
        // If the string is empty, simply clear the label list.
        if (text.length() == 0)
        {
            labelList.clear();            
            return;
        }
        
        // Split the text into lines.
        String[] lines = text.split("\n");
        
        // Clear the array list if necessary.
        if (labelList.isEmpty() == false)
            labelList.clear();                               
        
        // Create the appropriate number of labels.  For example, if we found
        // 1 newline, that would mean we have 2 lines.
        for (int i = 0; i < lines.length; i++)
        {              
            // Create the label.
            Label label = ResourceFactory.get().getLabel(x, y);            
            
            // Make it so all labels have the same characteristics.
            label.setAlignment(alignment);
            label.setColor(color);
            label.setOpacity(opacity);
            label.setRotation(theta);
            label.setSize(size);     
            label.setText(lines[i]);
                        
            // Add it to the label list.
            labelList.add(label);
        }    
        
        // We know there must be at least one label.
        assert (labelList.size() > 0);               
        
        // Space each line according to the letter height and leading.
        // Skip the first one, since it's already in the right spot. 
        int i = 0;        
        for (Label label : labelList)                                        
        {         
            label.translate(0, separation * i);        
            i++;
        }
    }
    
    /**
	 * Set the color of the text.
	 * 
	 * @param s The size.
	 */
    @Override
	public void setSize(float size)
	{
        super.setSize(size);
        
        for (Label label : labelList)
            label.setSize(size);
	}
	
	/**
	 * Set the anchor of the text box. The anchor is initially set to the top left. 
	 * 
	 * @param x The x anchor coordinate with respect to the top left corner of the text box.
	 * @param y The y anchor coordinate with respect to the top left corner of the text box.
	 */
    @Override
	public void setAlignment(EnumSet<Alignment> alignment)
	{
        super.setAlignment(alignment);
        
        for (Label label : labelList)
            label.setAlignment(alignment);
	}
    
    /**
	 * Set the text color.
	 * The initial color should be black.
	 * 
	 * @param color The color to set to.
	 */
    @Override
	public void setColor(final Color color)
    {
        super.setColor(color);
        
        for (Label label : labelList)
            label.setColor(color);
    }
    
    /**
     * Sets the opacity of the sprite (in percent).
     * 
     * @param opacity The opacity.
     */
    @Override
    public void setOpacity(final int opacity)
    {       
        super.setOpacity(opacity);
        
        for (Label label : labelList)
            label.setOpacity(opacity);
    }
    
    /**
     * Rotates the image by theta.
     * 
     * @param theta
     */
    @Override
    public void setRotation(double theta)
    {
        super.setRotation(theta);
        
        for (Label label : labelList)
            label.setRotation(theta);
    }

    /**
     * This method is not needed for the multiline label since it
     * overrides getDrawRect().
     * 
     * @return
     */
    @Override
    public int getLetterHeight()
    {        
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Simply goes through the label list and draws each label.  They should
     * be positioned correctly when the text is set.
     */
    @Override
    public void draw()
    {
        if (isVisible() == false)
            return;
        
        for (Label label : labelList)
            label.draw();
    }
    
    /**
     * Unions all the draw rectangles of the underlying labels.
     * 
     * @return The draw rectangle.
     */
    @Override
    public Rectangle getDrawRect()
    {
        // If the draw rect is null, generate it.
        if (drawRect == null)
        {
            Rectangle rect = new Rectangle();
            
            for (Label label : labelList)
                rect.add(label.getDrawRect());
            
            drawRect = rect;
        }
               
        return drawRect;
    }
    
}

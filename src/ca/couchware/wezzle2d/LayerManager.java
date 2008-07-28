package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.graphics.*;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A class for managaing layer draw order.  Layer 0 is the bottom (lowest)
 * layer, followed by Layer 1, Layer 2, etc.  Negative layers are not 
 * permitted.
 * 
 * @author cdmckay
 */
public class LayerManager
{
    
    /**
     * The number of layers.
     */
    private int numberOfLayers;
        
    /**
     * TODO Add documentation.
     */
    private ArrayList<ArrayList<IDrawable>> layerList;
    
    /**
     * The list of hidden layers.
     */
    private boolean[] hiddenLayers;
    
    /**
     * The game window.  Used when drawing regions of the screen.
     */
    private GameWindow window;
    
    /**
     * The remove clip.  This clip is used to make sure that things that are
     * removed are drawn over.
     */
    private Rectangle removeRect;
    
    /**
     * The constructor.
     */
    public LayerManager(GameWindow window, int numberOfLayers)
    {
        // Must have at least 1 layer.
        assert(window != null);
        assert(numberOfLayers > 0);       
        
        // Set the window reference.
        this.window = window;                
        
        // Record number of layers.
        this.numberOfLayers = numberOfLayers;
        
        // Initialize layer arraylist.
        layerList = new ArrayList<ArrayList<IDrawable>>(numberOfLayers);
        
        // Initialize hidden layer map.
        hiddenLayers = new boolean[numberOfLayers];
        
        // Create layers.
        for (int i = 0; i < numberOfLayers; i++)
        {
            layerList.add(new ArrayList<IDrawable>());
            hiddenLayers[i] = false;
        }        
        
        // Initialize remove clip.
        this.removeRect = new Rectangle();
    }
    
    /**
     * Adds a drawable to the layer specified.  If the layer does not exist,
     * it is created.
     */
    public void add(final IDrawable element, final int layerNum)
    {            
        if (layerExists(layerNum) == false)
            return;

        // Add the element to the layer.
        layerList.get(layerNum).add(element);        
    }
    
    /**
     * Remove an element from the layer specified.
     * @return True if the element was removed, false if it was not found.
     */
    public boolean remove(final IDrawable element, final int layerNum)
    {   
        if (layerExists(layerNum) == false)
            return false;
        
        // Get the layer.
        final ArrayList<IDrawable> layer = layerList.get(layerNum);
        
        // Get the index.
        int index = layer.indexOf(element);
        
        // If the index is -1, the element is not in this layer.
        if (index != -1)
        {
            Rectangle r = layer.get(index).getDrawRect();
            if (r != null) addRemoveRect(r);
            layer.remove(index);
            return true;
        }
        else        
        {
            Util.handleWarning("Tried to remove an element that did not exist.", 
                    "LayerManager#remove");
            return false;
        }
    }    
    
    public void show(final int layerNum)
    {
        if (layerExists(layerNum) == false)
            return;
        
        if (hiddenLayers[layerNum] == false)
            return;
        
        hiddenLayers[layerNum] = false;
        
        // Grab the layer.
        final ArrayList<IDrawable> layer = layerList.get(layerNum);
        
        for (IDrawable d : layer)
            d.setDirty(true);
    }
    
    public void hide(final int layerNum)
    {             
        if (layerExists(layerNum) == false)
            return;
        
        if (hiddenLayers[layerNum] == true)
            return;
        
        hiddenLayers[layerNum] = true;
        
        // Grab the layer.
        final ArrayList<IDrawable> layer = layerList.get(layerNum);
        
        for (IDrawable d : layer)
            d.setDirty(true);
    }
    
    public void toFront(final IDrawable d, int layerNum)
    {
        if (layerExists(layerNum) == false)     
            return;        
                
        remove(d, layerNum);
        add(d, layerNum);
    }
    
    /**
     * Draws the layers to screen, in order from 0 to N (where N is the number
     * of layers).
     */
    public void draw()
    {
        // The number of sprites drawn.
        //int count = 0;
        //int total = 0;
        
        // The clipping area.        
        Rectangle clip;
        
        if (window.getClip() !=  null)
            clip = window.getClip().getBounds();
        else
            clip = null;
                
        // Cycle through all the layers, drawing them.
        for (int i = 0; i < numberOfLayers; i++)
        {                        
            // Check if layer exists, if it doesn't, skip this iteration.
            if (hiddenLayers[i] == true)                 
                continue;
            
            // Grab this layer.
            final ArrayList<IDrawable> layer = layerList.get(i);
            
            // Draw its contents.
            for (IDrawable d : layer)
            {                                
                if (clip == null 
                        || (d.getDrawRect() != null 
                            && d.getDrawRect().intersects(clip) == true))
                {                   
                    //count++;
                    d.draw();                
                }
                //total++;
            }           
        } // end for
        
        // Report the number of sprites drawn.
        //Util.handleMessage("Drew " + count + " of " + total + " drawables.");
    }
    
    /**
     * Draws anything dirty in that region.
     * 
     * @param rx
     * @param ry
     * @param rwidth
     * @param rheight
     */
    public boolean drawRegion(int rx, int ry, int rwidth, int rheight)
    {
        Rectangle region = new Rectangle(rx, ry, rwidth, rheight);
        Rectangle clip = null;
        
        // Cycle through all the layers, drawing them.
        for (int i = 0; i < numberOfLayers; i++)
        {                                             
            // Grab this layer.
            ArrayList<IDrawable> layer = layerList.get(i);
            
            // See if it's in the region.
            for (IDrawable d : layer)
            {
                if (d.isDirty() == false)
                    continue;                                
                
                // Clear dirtiness.
                d.setDirty(false);
                
                Rectangle r = d.getDrawRect();                
                
                if (r != null && (r.getMinX() < 0 || r.getMinY() < 0))
                {
                    Util.handleWarning("Offending class is " 
                            + d.getClass().getSimpleName(), 
                            "LayerManager#drawRegion");
                    Util.handleWarning("Rectangle is " + r,
                            "LayerManager#drawRegion");
                    
                    if (d instanceof ILabel)
                        Util.handleWarning(((ILabel) d).getText());
                }
                
                //Util.handleWarning(r + "");
                
                if (r != null && region.intersects(r) == true)
                {   
//                    Util.handleWarning("Offending class is " 
//                            + d.getClass().getSimpleName(), 
//                            Thread.currentThread());
//                    Util.handleWarning("Rectangle is " + r,
//                            Thread.currentThread());
                    
//                    if (d instanceof Java2DText)
//                    {
//                        Java2DText j = (Java2DText) d;
//                        Util.handleWarning(j.getText(), 
//                            Thread.currentThread());
//                    }
                    
                    if (clip == null)
                        clip = new Rectangle(r);
                    else
                        clip.add(r);
                }
            }                            
        }     
        
        // If the remove clip is not empty.
        if (getRemoveRect() != null)
        {
            // See if there's a clip to add to, or if we need to make one.
            if (clip == null)
                clip = getRemoveRect();
            else
                clip.add(getRemoveRect());
        }
             
        if (clip != null)
        {
            //Util.handleMessage(clip.toString(), Thread.currentThread());
            
            window.setClip(clip);
            draw();            
            window.clearClip();
            
            // Uncomment the next line if you want boxes to be drawn around
            // each region being drawn.
            //window.drawClip(clip);
            //Util.handleMessage("clip = " + clip);
               
            // Reset the remove clip.
            resetRemoveRect();
            
            return true;
        }        
        else
        {
            return false;
        }
    }
    
    /**
     * Checks for existance of the passed layer number.
     * 
     * @param layer
     * @return
     */
    private boolean layerExists(int layerNum)
    {
        // Sanity check.
        assert(layerNum >= 0);
        
        // Check if layer exists then error out.
        if (layerNum >= layerList.size())
        {      
            Util.handleWarning("Layer does not exist!", 
                    "LayerManager#layerExists");
            return false;                    
        }
        else
        {
            return true;
        }
    }
    
    private void addRemoveRect(Rectangle r)
    {
        if (removeRect == null)
            removeRect = new Rectangle(r);
        else
            removeRect.add(r);
    }
    
    private Rectangle getRemoveRect()
    {
        return removeRect;
    }   
    
    private void resetRemoveRect()
    {       
        removeRect = null;
    }

}

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.java2d.Java2DLabel;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A class for managaing layer draw order.  Layer 0 is the bottom (lowest)
 * layer, followed by Layer 1, Layer 2, etc.  Negative layers are not 
 * permitted.
 * 
 * @author cdmckay
 */
public class LayerManager implements Drawable
{
    
    /**
     * The number of layers.
     */
    private int numberOfLayers;
        
    /**
     * The hash map of layer linked lists.
     * Using 1.5 generics, this would be
     *     HashMap<Integer, LinkedList<Drawable>>.
     */
    private HashMap layerMap;
    
    /**
     * The list of hidden layers.
     */
    private HashMap hiddenLayerMap;
    
    /**
     * The game window.  Used when drawing regions of the screen.
     */
    private GameWindow window;
    
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
        
        // Initialize layer map.
        layerMap = new HashMap();
        
        // Initialize hidden layer map.
        hiddenLayerMap = new HashMap();
        
        // Record number of layers.
        this.numberOfLayers = numberOfLayers;
        
        // Create layers.
        for (int i = 0; i < numberOfLayers; i++)
        {
            layerMap.put(new Integer(i), new LinkedList());
            hiddenLayerMap.put(new Integer(i), Boolean.FALSE);
        }        
    }
    
    /**
     * Adds a drawable to the layer specified.  If the layer does not exist,
     * it is created.
     */
    public void add(final Drawable element, final int layer)
    {            
        // Sanity check.
        assert(layer >= 0);
        
        // Check if layer exists.  If it does not, add it.
        if (layerMap.containsKey(new Integer(layer)) == false)
        {                        
            Util.handleMessage("Layer " + layer + " created.", 
                    Thread.currentThread());
            
            // Increase layer count.
            this.numberOfLayers = layer + 1;
            
            // Increase the layer count.
            layerMap.put(layer, new LinkedList());
            hiddenLayerMap.put(layer, new Boolean(false));
        }

        // Add the element to the layer.
        ((LinkedList) layerMap.get(new Integer(layer))).add(element);        
    }
    
    /**
     * Remove an element from the layer specified.
     * @return True if the element was removed, false if it was not found.
     */
    public boolean remove(final Drawable element, final int layer)
    {
        // Get the layer.
        LinkedList layerList = (LinkedList) layerMap.get(layer);
        
        // Get the index.
        int index = layerList.indexOf(element);
        
        // If the index is -1, the element is not in this layer.
        if (index != -1)
        {
            layerList.remove(index);
            return true;
        }
        else
            return false;
    }    
    
    public void hide(final int layer)
    {
        Integer index = new Integer(layer);
        
        hiddenLayerMap.put(index, Boolean.TRUE);
        
        // Grab the layer.
        final LinkedList layerList = (LinkedList) layerMap.get(index);
        
        for (Iterator it = layerList.iterator(); it.hasNext(); )
            ((Drawable) it.next()).setDirty(true);
    }
    
    public void show(final int layer)
    {
        Integer index = new Integer(layer);
        
        hiddenLayerMap.put(index, Boolean.FALSE);
        
        // Grab the layer.
        final LinkedList layerList = (LinkedList) layerMap.get(index);
        
        for (Iterator it = layerList.iterator(); it.hasNext(); )
            ((Drawable) it.next()).setDirty(true);
    }
    
    /**
     * Draws the layers to screen, in order from 0 to N (where N is the number
     * of layers).
     */
    public void draw()
    {
        // Cycle through all the layers, drawing them.
        for (int i = 0; i < numberOfLayers; i++)
        {
            Integer index = new Integer(i);
            
            // Check if layer exists, if it doesn't, skip this iteration.
            if (((Boolean) hiddenLayerMap.get(index)) == Boolean.TRUE
                    || layerMap.containsKey(index) == false)
                continue;
            
            // Grab this layer.
            LinkedList layerList = (LinkedList) layerMap.get(index);
            
            // Draw its contents.
            for (Iterator it = layerList.iterator(); it.hasNext(); )
            {
                ((Drawable) it.next()).draw();
            }           
        }            
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
            Integer index = new Integer(i);
            
            // Check if layer exists, if it doesn't, skip this iteration.
            if (layerMap.containsKey(index) == false)
                continue;
            
            // Grab this layer.
            LinkedList layerList = (LinkedList) layerMap.get(index);
            
            // See if it's in the region.
            for (Iterator it = layerList.iterator(); it.hasNext(); )
            {
                Drawable d = (Drawable) it.next();
                
                if (d.isDirty() == false)
                    continue;                                
                
                // Clear dirtiness.
                d.setDirty(false);
                
                Rectangle r = d.getDrawRect();                
                
                if (r == null || r.getMinX() < 0 || r.getMinY() < 0)
                {
                    Util.handleWarning("Offending class is " 
                            + d.getClass().getSimpleName(), 
                            Thread.currentThread());
                    Util.handleWarning("Rectangle is " + r,
                            Thread.currentThread());
                }
                
                if (region.intersects(r) == true)
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
             
        if (clip != null)
        {
            //Util.handleMessage(clip.toString(), Thread.currentThread());
            
            window.setClip(clip);
            draw();            
            window.clearClip();
            //window.drawClip(clip);
                        
            return true;
        }        
        else
        {
            return false;
        }
    }

    public void setVisible(boolean visible)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isVisible()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDirty(boolean dirty)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDirty()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Rectangle getDrawRect()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetDrawRect()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

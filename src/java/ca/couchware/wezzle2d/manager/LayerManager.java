package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IGraphics;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.graphics.IDisposable;
import ca.couchware.wezzle2d.graphics.IDrawable;
import ca.couchware.wezzle2d.graphics.IDrawer;
import ca.couchware.wezzle2d.graphics.IEntity;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class for managaing layer draw order.  Layer 0 is the bottom (lowest)
 * layer, followed by Layer 1, Layer 2, etc.  Negative layers are not 
 * permitted.  
 * 
 * @author cdmckay
 */
public class LayerManager implements IDisposable, IDrawer
{           
    
    /**
     * The different layers supported by the layer manager.
     */
    public enum Layer
    {
        BACKGROUND,
        INFORMATION,
        BOARD,
        TILE,
        PIECE_GRID,
        EFFECT,        
        UI,
        HELP
    }        
        
    /**
     * Is the layer manager disabled?
     */
    private boolean disabled = false;
    
    /**
     * The list of all layers.
     */
    private ArrayList<ArrayList<IDrawable>> layerList;
    
    /**
     * The list of hidden layers.
     */
    private boolean[] hidden;
    
    private IWindow win;
    private IGraphics graphics;
    
    /**
     * The constructor.
     */
    private LayerManager(IWindow win)
    {
        this.win = win;
        this.graphics = win.getGraphics();
        
        // Initialize layer arraylist.
        layerList = new ArrayList<ArrayList<IDrawable>>(Layer.values().length);
        
        // Initialize hidden layer map.
        hidden = new boolean[Layer.values().length];
        
        // Create layers.
        for (int i = 0; i < Layer.values().length; i++)
        {
            layerList.add(new ArrayList<IDrawable>());
            hidden[i] = false;
        }                      
    }
        
    /**
     * Create a new layer manager instance.
     * 
     * @return
     */
    public static LayerManager newInstance(IWindow win)
    {
        return new LayerManager(win);
    }
    
    /**
     * Adds a drawable to the layer specified.  If the layer does not exist,
     * an exception is thrown
     */
    public void add(final IDrawable drawable, Layer layer)
    {         
        // The drawable cannot be null.
        if ( drawable == null )
            throw new IllegalArgumentException("Drawable cannot be null");

        if ( layer == null )
            throw new IllegalArgumentException("Layer cannot be null");

        // Set disabledness.
        if (disabled == true && drawable instanceof IEntity)
            ((IEntity) drawable).setDisabled(true);
        
        // Add the element to the layer.
        layerList.get(layer.ordinal()).add(drawable);        
    }
    
    /**
     * Remove an element from the layer specified.
     * @return True if the element was removed, false if it was not found.
     */
    public void remove(final IDrawable drawable, Layer layer)
    {   
        // The drawable cannot be null.
        if (drawable == null)
            throw new NullPointerException("Drawable does not exist!");
        
        if (layer == null)
            throw new NullPointerException("Layer does not exist!");
        
        // Get the layer.
        final ArrayList<IDrawable> list = layerList.get(layer.ordinal());
        
        // Get the index.
        int index = list.indexOf(drawable);
        
        // If the index is -1, the element is not in this layer.
        if (index != -1)
        {
//            Rectangle r = list.get(index).getDrawRect();
//            if (r != null) addRemoveRect(r);
            list.remove(index);            
        }
        else        
        {
            throw new RuntimeException("Tried to remove non-exist element!");            
        }
    }

    /**
     * Remove an element from the layer specified.
     * @return True if the element was removed, false if it was not found.
     */
    public void c(final IDrawable drawable, Layer layer)
    {
        // The drawable cannot be null.
        if (drawable == null)
            throw new NullPointerException("Drawable does not exist!");

        if (layer == null)
            throw new NullPointerException("Layer does not exist!");

        // Get the layer.
        final ArrayList<IDrawable> list = layerList.get(layer.ordinal());

        // Get the index.
        int index = list.indexOf(drawable);

        // If the index is -1, the element is not in this layer.
        if (index != -1)
        {
//            Rectangle r = list.get(index).getDrawRect();
//            if (r != null) addRemoveRect(r);
            list.remove(index);
        }
        else
        {
            throw new RuntimeException("Tried to remove non-exist element!");
        }
    }
    
    /**
     * Returns a read-only list representing the specified layer.
     * 
     * @param layer
     * @return
     */
    public List<IDrawable> getLayer(Layer layer)
    {
        return Collections.unmodifiableList(layerList.get(layer.ordinal()));
    }
    
    /**
     * Clear a layer from the layer manager.
     * 
     * @param layer
     */
    public void clearLayer(Layer layer)
    {
        CouchLogger.get().recordMessage(this.getClass(),
                String.format("Layer %s cleared of %d entities",
                    layer.toString(), layerList.get(layer.ordinal()).size()));
        
        layerList.get(layer.ordinal()).clear();
    }        
    
    public boolean contains(final IDrawable drawable, Layer layer)
    {
        // The drawable cannot be null.
        if (drawable == null)
            throw new NullPointerException("Drawable does not exist!");
        
        // Get the layer.
        final ArrayList<IDrawable> list = layerList.get(layer.ordinal());
        
        // Get the index.
        int index = list.indexOf(drawable);
        
        // If it's -1, it doesn't exist.
        return (index != -1);
    }
    
    public void show(Layer layer)
    {
        if (layer == null)
            throw new NullPointerException("Layer does not exist!");
        
        if (!hidden[layer.ordinal()])
            return;
        
        hidden[layer.ordinal()] = false;
        
        // Grab the layer.
        final ArrayList<IDrawable> list = layerList.get(layer.ordinal());
        
        for (IDrawable d : list)
        {
            d.setDirty(true);
            if (d instanceof IEntity) ((IEntity) d).setDisabled( false );
        }
    }
    
    public void hide(Layer layer)
    {             
        if (layer == null)
            throw new RuntimeException("Layer does not exist!");
        
        if (hidden[layer.ordinal()])
            return;
        
        hidden[layer.ordinal()] = true;
        
        // Grab the layer.
        final ArrayList<IDrawable> list = layerList.get(layer.ordinal());
        
        for (IDrawable d : list)
        {
            d.setDirty(true);
            if (d instanceof IEntity) ((IEntity) d).setDisabled( true );
        }
    }
    
    public void toFront(final IDrawable d, Layer layer)
    {
        if (layer == null)     
            throw new RuntimeException("Layer does not exist!");      
                
        remove(d, layer);
        add(d, layer);
    }
    
    public void toBack(final IDrawable d, Layer layer)
    {
        if (layer == null)     
            throw new RuntimeException("Layer does not exist!");
        
        remove(d, layer);
        layerList.get(layer.ordinal()).add(0, d);
    }
    
    /**
     * Draws the layers to screen, in order from 0 to N (where N is the number
     * of layers).
     */
    public void drawAll()
    {
        // Cycle through all the layers, drawing them.
        for (int i = 0; i < Layer.values().length; i++)
        {                        
            // Check if layer exists, if it doesn't, skip this iteration.
            if (hidden[i] == true)
                continue;
            
            // Grab this layer.
            final List<IDrawable> layer = layerList.get(i);
            
            // Draw its contents.
            for (IDrawable d : layer)
            {             
                d.draw();               
            }           
        } // end for                
    }
    
    /**
     * Draws anything dirty touching the passed region.
     * 
     * @param shape The region to draw.
     * @param exact Whether or not we should draw the passed region exactly. If
     * exact is false, then only the area that has changed will be drawn.
     */
    public boolean draw(Shape region, boolean exact)
    {               
        // If we want the exact region, then we must clip.
        if (exact)
        {
            graphics.setClip(region);
            drawAll();
            graphics.setClip(null);
            return true;
        }

        // Draw everything.
        drawAll();
        return true;
    }               
    
    public boolean draw(Shape region)
    {
        return draw(region, false);
    }
    
    public boolean draw()
    {
        return draw(Game.SCREEN_RECTANGLE, false);
    }            
    
    @Override
    public String toString()
    {
        // Create a string builda'.
        StringBuilder buffer = new StringBuilder();
        
        // Print out the number of drawables on each layer.
        int i = 0;
        for (List<IDrawable> layer : layerList)
        {            
            buffer.append("Layer " + i + ": " 
                    + layer.size() + " drawables" + Settings.getLineSeparator());
            i++;
        }       
        
        return buffer.toString();
    }   

    public boolean addAll(LayerManager layerMan)
    {
        // Since all layer managers have the same number of layers,
        // we can merge them by adding all the array lists together.
        boolean changed = false; 
        
        for (int i = 0; i < layerList.size(); i++)        
           if (layerList.get(i).addAll(layerMan.layerList.get(i)) == true) 
               changed = true;
        
        return changed;
    }       

    public boolean retainAll(LayerManager layerMan)
    {
        // Since all layer managers have the same number of layers,
        // we can merge them by adding all the array lists together.
        boolean changed = false; 
        
        for (int i = 0; i < layerList.size(); i++)        
           if (layerList.get(i).retainAll(layerMan.layerList.get(i)) == true) 
               changed = true;
        
        return changed;  
    }      

    public boolean removeAll(LayerManager layerMan)
    {
        // Since all layer managers have the same number of layers,
        // we can merge them by adding all the array lists together.
        boolean changed = false; 
        
        for (int i = 0; i < layerList.size(); i++)        
           if (layerList.get(i).removeAll(layerMan.layerList.get(i)) == true) 
               changed = true;
        
        return changed;
    }    

    public boolean isDisabled()
    {
        return disabled;
    }

    public void setDisabled(boolean disabled)
    {
        // Save the state.
        this.disabled = disabled;
       
        // Go through all the layers and call their disabled method if they
        // are entities.
        for (List<IDrawable> list : layerList)
        {
            for (IDrawable drawable : list)
            {
                if (drawable instanceof IEntity)
                {
                    ((IEntity) drawable).setDisabled(disabled);
                }
            }
        } // end for
    }

    /**
     * Empties the contents of the layer manager.
     */
    public void dispose()
    {
        for (List<IDrawable> list : layerList)
        {
            for (IDrawable drawable : list)
            {
                if (drawable instanceof IDisposable)
                {
                    ((IDisposable) drawable).dispose();
                }
            } // end for
        } // end for
    }
    
}

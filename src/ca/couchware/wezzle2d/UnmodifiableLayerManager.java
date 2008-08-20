/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.LayerManager.Layer;
import ca.couchware.wezzle2d.graphics.IDrawable;
import ca.couchware.wezzle2d.graphics.IEntity;
import java.util.List;

public class UnmodifiableLayerManager
{
    
    /**
     * The layer manager this class is wrapping.
     */
    private LayerManager layerMan;
    
    /**
     * Creates an unmodifiable layer manager object that only allows the user
     * to get layers.
     * 
     * @param layerManager
     */    
    public UnmodifiableLayerManager(LayerManager layerMan)
    {
        this.layerMan = layerMan;
    }
    
    /**
     * Returns a read-only list representing the specified layer.
     * 
     * @param layer
     * @return
     */
    public List<IDrawable> getLayer(Layer layer)
    {
        return layerMan.getLayer(layer);
    }
    
    /**
     * Gets all the entities in the layer manager as a list.
     * 
     * @return
     */
    public List<IEntity> getEntities()
    {
        return layerMan.getEntities();
    }
    
}
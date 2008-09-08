/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.transition;

import ca.couchware.wezzle2d.animation.*;
import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.util.Util;
import java.awt.geom.Ellipse2D;

/**
 * A layer manager transition that introduces a layer manager using a slowly
 * growing circle.
 * 
 * @author cdmckay
 */
public class CircularTransition extends AbstractTransition
{

    /**
     * The layer manager used in the transition.
     */
    private final LayerManager layerMan;
    
    /**
     * The number of milliseconds that have passed in this transition.
     */
    private long counter = 0;
    
    /**
     * The speed at which the circle should expand.
     */
    private double v;
    
    /**
     * The minimum radius.
     */
    private int minRadius;
    
    /**
     * The maximum radius
     */
    private int maxRadius;
    
    /**
     * The current radius.
     */
    private int currentRadius;
    
    /**
     * How long to wait before running.
     */
    private int wait;
    
    /**
     * Whether or not wait period is finished.
     */
    private boolean waitFinished = false;
    
    /**
     * The duration.
     */
    //private int duration;
    
    /**
     * The private constructor.
     * 
     * @param builder
     */    
    private CircularTransition(Builder builder)
    {
        this.layerMan = builder.layerMan;
        this.currentRadius = builder.minRadius;
        this.minRadius = builder.minRadius;
        this.maxRadius = builder.maxRadius;
        this.wait = builder.wait;
        this.v = builder.v;        
    }
    
    public static class Builder implements IBuilder<CircularTransition>
    {       
        private final LayerManager layerMan;
        
        private double v = 0.5;
        private int minRadius = 10;
        private int maxRadius = (int) Math.sqrt(Util.sq(Game.SCREEN_HEIGHT / 2) 
                + Util.sq(Game.SCREEN_WIDTH / 2));
        private int wait = 0;
        //private int duration = 500;
        
        public Builder(LayerManager layerMan)
        { 
            this.layerMan = layerMan;
        }       
        
        public Builder wait(int val) { wait = val; return this; }
        //public Builder duration(int val) { duration = val; return this; }
        public Builder minRadius(int val) { minRadius = val; return this; }
        public Builder maxRadius(int val) { maxRadius = val; return this; }
        public Builder v(double val) { v = val; return this; }

        public CircularTransition end()
        {
            return new CircularTransition(this);
        }                
    }
    
    @Override
    public void nextFrame(long delta)
    {
        // Indicate that the transition has started.
        setStarted();
        
        // Add delta to counter.  This serves as the time variable.
        counter += delta;                                              
        
        // See if the waiting is done.
        if (waitFinished == false && counter > wait)
        {
            waitFinished = true;
            counter -= wait;
        }
        
        if (waitFinished == true)
        {
            // The change in radius.
            int deltaRadius = (int) ((double) counter * v);
            currentRadius = minRadius + deltaRadius;
            
            if (currentRadius >= maxRadius)
                setFinished();
        }        
    }

    public boolean draw()
    {
        this.layerMan.draw(new Ellipse2D.Double(
                Game.SCREEN_RECTANGLE.getCenterX() - currentRadius,
                Game.SCREEN_RECTANGLE.getCenterY() - currentRadius,
                currentRadius * 2,
                currentRadius * 2),
                true);
        
        return true;
    }

}

/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.transition;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.graphics.IDrawer;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.NumUtil;
import java.awt.Shape;
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
    private final IDrawer drawer;
    
    /**
     * The number of milliseconds that have passed in this transition.
     */
    private int ticks = 0;
    
    /**
     * The speed at which the circle should expand.
     */
    private int speed;
    
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
        this.drawer = builder.drawer;
        this.currentRadius = builder.minRadius;
        this.minRadius = builder.minRadius;
        this.maxRadius = builder.maxRadius;
        this.wait = builder.wait;
        this.speed = builder.speed;        
    }
    
    public static class Builder implements IBuilder<CircularTransition>
    {       
        private final IDrawer drawer;
        
        private int speed = 500;
        private int minRadius = 10;
        private int maxRadius = (int) Math.sqrt(NumUtil.sqInt(Game.SCREEN_HEIGHT / 2) 
                + NumUtil.sqInt(Game.SCREEN_WIDTH / 2)) + 10;
        private int wait = 0;
        //private int duration = 500;
        
        public Builder(IDrawer drawer)
        { 
            this.drawer = drawer;
        }       
        
        public Builder wait(int val) { wait = val; return this; }
        //public Builder duration(int val) { duration = val; return this; }
        public Builder minRadius(int val) { minRadius = val; return this; }
        public Builder maxRadius(int val) { maxRadius = val; return this; }
        public Builder speed(int val) { speed = val; return this; }

        public CircularTransition end()
        {
            return new CircularTransition(this);
        }                
    }
    
    @Override
    public void nextFrame()
    {        
        // Make sure we've set the started flag.
        if (this.started == false)
        {
            // Record the initial position.                
            setStarted();
        }
        
        // Check if we're done, if we are, return.
        if (this.finished == true)
        {
            //LogManager.recordMessage("Move finished!");
            return;
        }
        
        // Increment counter.  This serves as the time variable.
        ticks++;
        
        // Convert to ms.
        int ms = ticks * Settings.getMillisecondsPerTick();        
                                                          
        if (waitFinished == false && ms > wait)
        {           
            // And start!
            waitFinished = true;
            ticks = 1;
            ms = ticks * Settings.getMillisecondsPerTick();
        }    
        
        if (waitFinished == true)
        {           
            // The change in radius.
            int deltaRadius = (speed * ms) / 1000;
            currentRadius = minRadius + deltaRadius;
            
            if (currentRadius >= maxRadius)
                setFinished();
        }        
    }

    public boolean draw()
    {
        //LogManager.recordMessage("currentRadius = " + currentRadius);
        if (waitFinished == true)
        {
            this.drawer.draw(new Ellipse2D.Double(
                    Game.SCREEN_RECTANGLE.getCenterX() - currentRadius,
                    Game.SCREEN_RECTANGLE.getCenterY() - currentRadius,
                    currentRadius * 2,
                    currentRadius * 2),
                    true);
        }       
        
        return true;
    }

    public boolean draw(Shape region, boolean exact)
    {
        return draw();
    }

    public boolean draw(Shape region)
    {
        return draw();
    }

}

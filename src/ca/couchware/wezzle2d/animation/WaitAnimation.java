package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.manager.Settings;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class WaitAnimation extends AbstractAnimation
{
    /** Counts the number of ticks. */
    private int ticks;

    /**  The amount of time to wait before starting. */
    private int wait;   

   /**
    * Fire an entity with the given launch speed, angle and gravity and 
    * rotation speed.        
    */
    public WaitAnimation(int wait)
    {
        if (wait == 0)
            throw new IllegalArgumentException("Wait time must be > 0");

        this.wait = wait;               
    }           

    public void nextFrame()
    {                   
        // Make sure we've set the started flag.
        if (!this.started)
        {
            // Record the initial position.                
            setStarted();
        }
        
        // Check if we're done, if we are, return.
        if (this.finished)
        {
            //LogManager.recordMessage("Move finished!");
            return;
        }
              
        // Increment counter.  This serves as the time variable.
        ticks++;
        
        // Convert to ms.
        int ms = ticks * Settings.getMillisecondsPerTick();             
        
        if (!this.finished && ms > wait)
            setFinished();
                
    }       
    
}

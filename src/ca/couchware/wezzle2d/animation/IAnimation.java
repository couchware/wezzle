package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;

/**
 * An interface for animating entities.
 * 
 * @author cdmckay
 */
public interface IAnimation 
{    
    
    /**
     * Determines how the entity should be changed based on how much time
     * has passed since last update.
     */
    public void nextFrame(long delta);       
    
    /**
     * Performs cleanup to the animation so that it may remain in a consistent
     * state even if it is called half-way through an animation.
     */
    public void cleanUp();    
    
    /**
     * Checks whether the animation is done.  Always returns false if the
     * animation is a looping animation.
     */
    public boolean isDone();    
    
    /**
     * Checks the visibility of the animation.
     * 
     * @return True if visible, false otherwise.
     */
    public boolean isVisible();    
    
    /**
     * Sets the visibility of the animation.
     * 
     * @param visible True if visible, false if not.
     */
    public void setVisible(final boolean visible);       
    
    /**
     * Set the start action.
     */
    public void setStartAction(Runnable startAction);
    
    /**
     * Get the start action.
     */
    public Runnable getStartAction();
    
    /**
     * Set the finish action.
     */
    public void setFinishAction(Runnable finishAction);
    
    /**
     * Get the finish action.
     */
    public Runnable getFinishAction();        
    
    /**
     * This method is run when the animation is first loaded by the
     * animation manager.
     */
    public void onStart();
    
    /**
     * This method is run when the animation is finished running.
     */
    public void onFinish();    
        
}

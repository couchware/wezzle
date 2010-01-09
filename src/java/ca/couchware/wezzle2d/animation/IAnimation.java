package ca.couchware.wezzle2d.animation;

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
    public void nextFrame();       
    
    /**
     * Performs cleanup to the animation so that it may remain in a consistent
     * state even if it is called half-way through an animation.
     */
    public void cleanUp();    
    
    /**
     * Checks whether the animation is done.  Always returns false if the
     * animation is a looping animation.
     */
    public boolean isFinished(); 
    
    public void setFinished();    
    
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
    
    /** An interface for listening for animation events. */
    public static interface IAnimationListener 
    {
        public void animationStarted();
        public void animationFinished();
    }       
    
    /**
     * Registers a button listener.
     * @param listener
     */
    public void addAnimationListener(IAnimationListener listener);    

    /**
     * Unregisters a button listener.
     * @param listener
     */
    public void removeAnimationListener(IAnimationListener listener);     
    
//    /**
//     * Set the start action.
//     */
//    public void setStartRunnable(Runnable startRunnable);        
//    
//    public Runnable getStartRunnable();    
//    
//    /**
//     * Set the finish action.
//     */
//    public void setFinishRunnable(Runnable finishRunnable);      
//    
//    public Runnable getFinishRunnable();    
//    
//    /**
//     * This method is run when the animation is first loaded by the
//     * animation manager.
//     */
//    public void onStart();
//    
//    /**
//     * This method is run when the animation is finished running.
//     */
//    public void onFinish();    
        
}

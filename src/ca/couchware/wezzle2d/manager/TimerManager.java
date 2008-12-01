package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.event.ILevelListener;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.util.Util;

/**
 * The TimeManager is a class that manages the time for the Timer.
 * The class works as follows: An internal count is held starting at 0.
 * An offset is passed in every update with an increment. When the internal count
 * goes above 1000ms (or 1 second) the timer is decremented.
 * 
 * @author Kevin Grad
 *
 */
public class TimerManager implements ILevelListener
{       
    /**
     * The default time.
     */
    private static int maximumTime = 15;

    public static void setMaximumTime(int time)
    {
        maximumTime = time;
    }        
    
    /** 
     * The minimum time.
     */
    private static int minimumTime = 5;

    public static void setMinimumTime(int time)
    {
        minimumTime = time;
    }        
    
	/** 
     * The current time on the timer. 
     */
	private int currentTime;
	
	/** 
     * The initial time for this timer. 
     */
	private int initialTime;
	
	/** 
     * Holds the internal time for comparison to time increments.
     */
	private long internalTime;
    
    /** 
     * Is the timer paused? 
     */
    private boolean paused;
    
    /**
     * Is the timer stopped?
     * It may seem like there's no point in having a pause and a stop, and to
     * an extent, you are right.  However, there is a distinction.  A pause
     * is fleeting, while a stop is more permanent.
     * 
     * For example, the timer is paused between each turn (fleeting).  However,
     * if a tutorial is running, sometime we want the timer off regardless
     * of how many turns have taken place (more permanent).
     */
    private boolean stopped;
    
    /**
     * Has the time changed since this value was checked?
     */
    private boolean changed;
	
	/**
	 * The overloaded constructor created a timer manager with a passed in 
	 * initial time.
	 * 
	 * @param initialTime The initial time on the timer.
	 */
	private TimerManager()
	{		
		this.currentTime = initialTime;
		this.initialTime = maximumTime;
		this.internalTime = 0;
        this.paused = false;
        this.stopped = false;
	}
        
    /**
     * Create a new timer manager instance.
     * 
     * @return
     */
    public static TimerManager newInstance()
    {
        return new TimerManager();
    }                

	/**
	 * A method to set the time on the timer.
	 * 
	 * @param time The new time.
	 */
	public void setTime(int time)
	{
		assert time >= 0;
		
		this.currentTime = time;
	}

	/**
	 * Get the timer time.
	 * @return The time.
	 */
	public int getTime()
	{
		return this.currentTime;
	}
	
	/**
	 * Reset the internal second count. I.e. the part that is modified by 
     * delta.
	 */
	public void resetInternalTimer()
	{
		this.internalTime = 0;
	}

	/**
	 * Reset the timer.
	 */
	public void resetTimer()
	{
		this.currentTime = this.initialTime;
        this.resetInternalTimer();
	}        
    
	/**
	 * Get the initial time.
	 * @return The initial time.
	 */
	public int getInitialTime()
	{
		return this.initialTime;
	}

	/**
	 * Set the initial time.
     * 
	 * @param time The new time.
	 */
	public void setInitialTime(int time)
	{
		this.initialTime = time;
	}
    
    public void resetInitialTime()
    {
        this.initialTime = maximumTime;
    }
	
	/**
	 * A method to increment the internal time. If a second has passed
	 * the internal time goes to 0 and the current time is decremented.
	 * 
	 * @param offset The elapsed time.
	 */
	public void incrementInternalTime()
	{                
        // If the timer is paused, don't do anything.
        if (paused == true || stopped == true)
            return;                        		
		
		this.internalTime += Settings.getMillisecondsPerTick();
		
		// Check to see if it has been a second.
		if (this.internalTime >= 1000)
		{
			// A second has elapsed, decrement the current time and the
            // internal time by 1000 (a second).
			this.currentTime--;
			this.internalTime -= 1000;          
            
            // Notify that it has changed.
            changed = true;
		}
		else
		{
			// Purposefully left blank. 
            // In this scenario nothing should be done.
		}
	}
    
    /**
     * Pause the timer.
     * 
     * @param paused True to pause the timer, false to unpause it.
     */
    public void setPaused(final boolean paused)
    {
        this.paused = paused;
    }
    
    /**
     * Check if the timer is paused.
     * 
     * @return Whether or not the timer is paused.
     */
    public boolean isPaused()
    {
        return this.paused;
    }

    public boolean isStopped()
    {
        return stopped;
    }

    public void setStopped(boolean stopped)
    {
        this.stopped = stopped;
    }

    public boolean isChanged()
    {
        boolean c = changed;
        changed = false;
        return c;
    }

    public void levelChanged(LevelEvent event)
    {
        int time = getInitialTime();
        
        if (time > minimumTime) 
            time--;
        
        setInitialTime(time);
    }
		
}

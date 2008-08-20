package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A class for managing animations.  All it does is step all animations when
 * told to and remove them when they're done or explicitly removed.
 * 
 * @author cdmckay
 */
public class AnimationManager 
{
    /**
     * The animation linked list.
     */
    private ArrayList<IAnimation> animationList;
    
    private AnimationManager()
    {
        // Initialize animation list.
        animationList = new ArrayList<IAnimation>();
    }
    
    // Public API.
    public static AnimationManager newInstance()
    {
        return new AnimationManager();
    }
    
    public void add(IAnimation a)
    {
        animationList.add(a);
        a.onStart();
    }
    
    public void addAll(Collection<IAnimation> collection)
    {
        for (IAnimation a : collection) 
            add(a);
    }
    
    public void remove(IAnimation a)
    {
        if (contains(a) == true)
            animationList.remove(a);
    }
    
    public boolean contains(IAnimation a)
    {
        return animationList.contains(a);
    }      
    
    public void animate(final long delta)
    {
        for (Iterator it = animationList.iterator(); it.hasNext(); ) 
        {
            IAnimation a = (IAnimation) it.next();
            if (a.isFinished() == true)
            {
                a.onFinish();
                it.remove();
            }
            else            
                a.nextFrame(delta);            
        }
        
        //Util.handleMessage(animationList.size() + "", "AnimationManager#animate");
    }
}

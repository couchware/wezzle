package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.animation.Animation;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
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
    private ArrayList<Animation> animationList;
    
    public AnimationManager()
    {
        // Initialize animation list.
        animationList = new ArrayList<Animation>();
    }
    
    public void add(Animation a)
    {
        animationList.add(a);
        a.onStart();
    }
    
    public void remove(Animation a)
    {
        if (contains(a) == true)
            animationList.remove(a);
    }
    
    public boolean contains(Animation a)
    {
        return animationList.contains(a);
    }
    
    public Iterator iterator()
    {
        return animationList.iterator();
    }
    
    public void animate(final long delta)
    {
        for (Iterator it = animationList.iterator(); it.hasNext(); ) 
        {
            Animation a = (Animation) it.next();
            if (a.isDone() == true)
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

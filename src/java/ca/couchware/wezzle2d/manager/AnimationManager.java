package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.animation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A class for managing animations.  All it does is step all animations when
 * told to and remove them when they're done or explicitly removed.
 * 
 * @author cdmckay
 */
public class AnimationManager implements ISaveable
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
        //a.onStart();
    }
    
    public void addAll(Collection<IAnimation> collection)
    {
        for (IAnimation a : collection) 
            add(a);
    }
    
    public void remove(IAnimation a)
    {
        if ( this.contains(a) )
        {
            animationList.remove(a);            
        }            
    }
    
    public boolean contains(IAnimation a)
    {
        return animationList.contains(a);
    }      
    
    public void animate()
    {
        for (Iterator it = animationList.iterator(); it.hasNext(); ) 
        {
            IAnimation a = (IAnimation) it.next();
            if (a.isFinished())
            {
                //a.onFinish();
                it.remove();
            }
            else            
                a.nextFrame();            
        }
        
        //Util.handleMessage(animationList.size() + "", "AnimationManager#animate");
    }

    public void saveState()
    {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void loadState()
    {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void resetState()
    {
        // Empty the animation list.
        this.animationList.clear();
    }
        
}

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.util.*;

/**
 * An interface for implementing a clickable button.
 * 
 * @author cdmckay
 */
public interface IButton extends IEntity, IMouseListener
{        
           
    /**
     * Set to true if the button has been clicked.  Automatically resets
     * to false each time it is read.
     * 
     * @return
     */
    public boolean clicked();   
    
    /**
     * A special version of <pre>clicked()</pre> that does not automatically 
     * reset the flag if <pre>preserve</pre> is true.
     * 
     * @param preserve
     * @return
     */
    public boolean clicked(boolean preserve);               	        
    
    /**
     * Returns true if the button is activated, false if it is not activated.
     * 
     * @return
     */
    public boolean isActivated();
    
    /**
     * Sets the activation status of the button.  This may change how the button
     * is the displayed, and what text is showing.
     * 
     * @activated The activation status to set.
     */
    public void setActivated(boolean activated);
    
    /**
     * Get the shape of the button.
     */
    public ImmutableRectangle getShape();
         
}

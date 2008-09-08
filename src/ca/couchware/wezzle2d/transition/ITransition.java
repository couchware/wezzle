/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.transition;

import ca.couchware.wezzle2d.animation.*;

/**
 * A special type of animation that controls a layer manager, usually to 
 * transition between two layer manager images.
 * 
 * @author cdmckay
 */
public interface ITransition extends IAnimation
{
    /**
     * Draws something to the screen.  Use in the same situation you'd use
     * a layer manager.
     * 
     * @return True if something on the screen changed.
     */
    boolean draw();    
}

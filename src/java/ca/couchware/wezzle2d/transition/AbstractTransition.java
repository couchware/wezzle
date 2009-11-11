/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.transition;

import ca.couchware.wezzle2d.animation.*;

/**
 * A skeletal implementation of the ITransition interface.
 * 
 * @author cdmckay
 */
public abstract class AbstractTransition extends AbstractAnimation implements ITransition
{
    // Intentionally left blank.  The sub-class should take care of the 
    // draw() method.
}

/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

/**
 * An interface for listening for piece events.
 * 
 * @author cdmckay
 */
public interface IPieceListener 
{    
    /**
     * This event is triggered when a piece is added to the board.
     * 
     * @param event
     */
    public void pieceAdded(PieceEvent event);
}

/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import ca.couchware.wezzle2d.piece.Piece;
import java.util.EventObject;

/**
 * The piece event.
 * 
 * @author cdmckay
 */
public class PieceEvent extends EventObject
{
    
    final private Piece currentPiece;
    final private Piece nextPiece;
    
    public PieceEvent(Object source, Piece currentPiece, Piece nextPiece)
    {
        super(source);        
        this.currentPiece = currentPiece;
        this.nextPiece = nextPiece;
    }

    public Piece getCurrentPiece()
    {
        return currentPiece;
    }

    public Piece getNextPiece()
    {
        return nextPiece;
    }        
    
}

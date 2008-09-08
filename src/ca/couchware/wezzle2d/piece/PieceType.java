/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.piece;

/**
 * The different kinds of Wezzle pieces.
 * 
 * @author cdmckay
 */
public enum PieceType
{
    DOT(new PieceDot()), 
    DASH(new PieceDash()), 
    LINE(new PieceLine()), 
    DIAGONAL(new PieceDiagonal()), 
    L(new PieceL());
    
    private Piece piece;
    
    PieceType(Piece piece)
    { this.piece = piece; }
    
    public Piece getPiece()
    { return piece; }
}    

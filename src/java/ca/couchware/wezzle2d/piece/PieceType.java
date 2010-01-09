/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.piece;

import ca.couchware.wezzle2d.util.NumUtil;
import java.awt.Color;

/**
 * The different kinds of Wezzle pieces.
 * 
 * @author cdmckay
 */
public enum PieceType
{
    
    DOT(Color.RED), 
    DASH(Color.CYAN), 
    LINE(Color.YELLOW), 
    DIAGONAL(Color.LIGHT_GRAY), 
    L(Color.GREEN);
    
    private Color color;
    
    PieceType(Color color)
    { this.color = color; }

    public Color getColor()
    {
        return color;
    }        
    
    public Piece getPiece()
    { 
        switch(this)
        {
            case DOT:
                return new PieceDot();                
                
            case DASH:
                return new PieceDash();   
                
            case LINE:
                return new PieceLine();   
                
            case DIAGONAL:
                return new PieceDiagonal();   
                
            case L:
                return new PieceL();   
                
            default: throw new AssertionError();
        }
    }
    
    public static PieceType getRandom()
    {
        // Get an array of all the types.
        PieceType[] pt = PieceType.values();
        
        // Load a random one.
		return pt[NumUtil.random.nextInt(pt.length)];
    }
    
}    

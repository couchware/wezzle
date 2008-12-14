/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

import ca.couchware.wezzle2d.manager.LogManager;

/**
 * Contains all the ASCII values.
 * 
 * @author cdmckay
 */
public enum Ascii
{
    NUL,
    SOH,
    STX,
    ETX,
    EOT,
    ENQ,
    ACK,
    BEL,
    BACKSPACE,
    HT,
    LF,
    VT,
    FF,
    CR,
    SO,
    SI,
    DLE,
    DC1,
    DC2,
    DC3,
    DC4,
    NAK,
    SYN,
    ETB,
    CAN,
    EM,
    SUB,
    ESC,
    FS,
    GS,
    RS,
    US, 
    SP, 
    EXCLAMATION_MARK,   
    DOUBLE_QUOTE,
    NUMBER_SIGN,
    DOLLAR_SIGN,
    PERCENT,
    AMPERSAND,
    SINGLE_QUOTE,
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    ASTERISK,
    PLUS,
    COMMA,
    MINUS,
    DOT,
    FORWARD_SLASH,
    NUM_0,
    NUM_1,
    NUM_2,
    NUM_3,
    NUM_4,
    NUM_5,
    NUM_6,
    NUM_7,
    NUM_8,
    NUM_9,
    COLON,
    SEMI_COLON,
    LESS_THAN,
    EQUAL_SIGN,
    GREATER_THAN,
    QUESTION_MARK,
    AT_SYMBOL,
    UPPER_A,
    UPPER_B,
    UPPER_C,
    UPPER_D,
    UPPER_E,
    UPPER_F,
    UPPER_G,
    UPPER_H,
    UPPER_I,
    UPPER_J,
    UPPER_K,
    UPPER_L,
    UPPER_M,
    UPPER_N,
    UPPER_O,
    UPPER_P,
    UPPER_Q,
    UPPER_R,
    UPPER_S,
    UPPER_T,
    UPPER_U,
    UPPER_V,
    UPPER_W,
    UPPER_X,
    UPPER_Y,
    UPPER_Z,
    LEFT_BRACKET,
    BACK_SLASH,
    RIGHT_BRACKET,
    CARET,
    UNDERSCORE,
    BACKTICK,
    LOWER_A,
    LOWER_B,
    LOWER_C,
    LOWER_D,
    LOWER_E,
    LOWER_F,
    LOWER_G,
    LOWER_H,
    LOWER_I,
    LOWER_J,
    LOWER_K,
    LOWER_L,
    LOWER_M,
    LOWER_N,
    LOWER_O,
    LOWER_P,
    LOWER_Q,
    LOWER_R,
    LOWER_S,
    LOWER_T,
    LOWER_U,
    LOWER_V,
    LOWER_W,
    LOWER_X,
    LOWER_Y,
    LOWER_Z,
    LEFT_BRACE,
    PIPE,
    RIGHT_BRACE,
    TILDE,
    DELETE;
    
    public char getChar()
    { return (char) this.ordinal(); }
    
    public static Ascii valueOf(char ch)
    {
        Ascii[] values = values();        
        
        if (ch < values.length)
            return values[ch];
            
        throw new IllegalArgumentException("Not a valid ASCII character.");                    
    }
    
    @Override
    public String toString()
    {       
        return String.valueOf(getChar());
    }
}

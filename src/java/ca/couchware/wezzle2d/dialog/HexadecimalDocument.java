/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.dialog;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A document filter that only allows 8 digit hex numbers.
 *
 * @author cdmckay
 */
public class HexadecimalDocument extends PlainDocument
{
    private int length = 8;

    public HexadecimalDocument(int length)
    {
        this.length = length;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException
    {
        int max = this.length - offset;

        String filteredStr = str
                .toUpperCase()
                .replaceAll("[^A-Fa-f0-9]", "");

        String truncatedStr = filteredStr
                .substring( 0, Math.min(max, filteredStr.length()) );

        super.insertString(offset, truncatedStr, a);
    }
    
}

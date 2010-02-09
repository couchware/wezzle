/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

import java.text.ParseException;
import javax.swing.text.MaskFormatter;

/**
 * This mask formatter allows a client to specify a mask, and have any
 * part of the mask be allowed as a valid value. That is, imagine the
 * mask is specified as "#####-####". Using the standard mask formatter,
 * the value must completely match the mask. Using this mask formatter,
 * the value must only match the mask up to the value's length. That is,
 * "1234" matches, as does "12345-6".
 *
 * @author JayDS
 */
public class PartialMaskFormatter extends MaskFormatter
{

    /**
     * Constructor without a mask; must call {@link #setMask(String)}
     * later.
     */
    public PartialMaskFormatter()
    {
        super();
    }

    /**
     * Constructor specifying the mask
     *
     * @param mask
     * @throws ParseException
     */
    public PartialMaskFormatter(String mask) throws ParseException
    {
        super(mask);
    }

    /* (non-Javadoc)
     * @see javax.swing.text.MaskFormatter#stringToValue(java.lang.String)
     */
    @Override
    public Object stringToValue(String value) throws ParseException
    {
        Object rv;

        // Get the mask
        String mask = getMask();

        if (mask != null)
        {
            // Change the mask based upon the string passed in
            setMask(getMaskForString(mask, value));

            // Using the substring of the given string up to the mask length,
            // convert it to an object
            rv = super.stringToValue(value.substring(0, getMask().length()));

            // Change mask back to original mask
            setMask(mask);
        }
        else
        {
            rv = super.stringToValue(value);
        }

        // Return converted value
        return rv;
    }

    /**
     * Answer what the mask should be for the given string based on the
     * given mask. This mask is just the subset of the given mask up to
     * the length of the given string or where the first placeholder
     * character occurs in the given string. The underlying assumption
     * here is that the given string is simply the text from the
     * formatted field upon which we are installed.
     *
     * @param value The string for which to determine the mask
     * @return A mask appropriate for the given string
     */
    protected String getMaskForString(String mask, String value)
    {
        StringBuffer sb = new StringBuffer();
        int maskLength = mask.length();
        char placeHolder = getPlaceholderCharacter();
        for (int k = 0, size = value.length(); k < size && k < maskLength; k++)
        {
            if (placeHolder == value.charAt(k))
            {
                break;
            }
            sb.append(mask.charAt(k));
        }
        return sb.toString();
    }

}


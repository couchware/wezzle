/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * A collection of string utility methods.
 * 
 * @author cdmckay
 */
public class StringUtil 
{

    /**
     * A method for joing together a collection of strings using a specified
     * delimiter.
     * 
     * @param collection
     * @param delimiter
     * @return
     */
    public static <T> String join(final Collection<T> collection, final String delimiter)
    {
        if (collection == null || collection.isEmpty())
        {
            return "";
        }

        Iterator<T> it = collection.iterator();
        StringBuffer buffer = new StringBuffer(it.next().toString());

        while (it.hasNext())
        {
            buffer.append(delimiter).append(it.next().toString());
        }

        return buffer.toString();
    }


    /**
     * Extracts the file extension from a file with the name.ext format.
     * 
     * @param path
     * @return The .ext part of the path.
     */
    public static String getFileExtension(final String path)
    {
        String fileName = new java.io.File(path).getName();
        
        String ext = "";
        
        if (fileName.lastIndexOf(".") != -1)        
            ext = fileName.substring(fileName.lastIndexOf(".") + 1);        
        
        return ext;
    }       
    
    /**
     * Pad the right side of a string with the given character up to a given
     * number of characters.
     * 
     * @param str
     * @param ch
     * @param length
     * @return
     */
    public static String padString(String str, char ch, int length)
    {
        StringBuffer buffer = new StringBuffer(str);
        int strLength  = buffer.length();
        
        if (length > 0 && length > strLength)
        {
            for (int i = strLength; i < length; i++)
            {
                buffer.append(ch);                
            } // end for
        } // end if
        
        return buffer.toString();
    }
    
    /**
     * Capitalize the first letter of the string and lowercase the rest of the
     * string.
     * 
     * Example:
     * capitalizeFully("abba ooba SNOOBA") == "Abba ooba snooba"
     * 
     * @param str
     * @return
     */
    public static String capitalizeFirst(String str)
    {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
            
}

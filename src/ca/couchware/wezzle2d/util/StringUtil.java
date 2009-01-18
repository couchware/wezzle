/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

/**
 * A collection of string utility methods.
 * 
 * @author cdmckay
 */
public class StringUtil 
{

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

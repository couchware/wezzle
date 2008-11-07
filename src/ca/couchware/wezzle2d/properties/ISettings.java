/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.properties;

import java.util.Map;

/**
 *
 * @author cdmckay
 */
public interface ISettings 
{
    /** The path to the settings file. */
    final public static String PATH = 
            System.getProperty("user.home") + "/.Couchware/Wezzle";
    
    public String getPath();
    public String getFilename();
    public String getComment();
    public Map<String, String> getDefaults();
}

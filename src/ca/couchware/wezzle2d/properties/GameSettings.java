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
public class GameSettings implements ISettings
{              
    
    /** The name of the settings file. */
    final public static String FILENAME = "GameSettings.xml";
    
    /** The comment to show in the settings file. */
    final public static String COMMENT = "Game Settings File";

    final public static GameSettings single = new GameSettings();
    
    private GameSettings()
    { }
    
    public static GameSettings get()
    {
        return single;
    }
    
    public String getPath()
    {
        return PATH;
    }

    public String getFilename()
    {
        return FILENAME;
    }
    
    public String getComment()
    {
        return COMMENT;
    }

    public Map<String, String> getDefaults()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}

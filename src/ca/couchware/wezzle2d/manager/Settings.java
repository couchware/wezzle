/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

/**
 * Contains all the settings keys and values.
 * 
 * @author cdmckay
 */
public class Settings 
{
    /** The path to the resources. */
    final public static String RESOURCES_PATH = "resources";        

    /** The path to the fonts. */
    final public static String FONT_RESOURCES_PATH = RESOURCES_PATH + "/fonts";
    
    /** The path to the sprites. */
    final public static String SPRITE_RESOURCES_PATH = RESOURCES_PATH + "/sprites";
    
    /** The path to the sounds. */
    final public static String SOUND_RESOURCES_PATH = RESOURCES_PATH + "/sounds";
    
    /** The path to the music. */
    final public static String MUSIC_PATH = RESOURCES_PATH + "/music";
        
    /** The name of the settings file. */
    final public static String SETTINGS_FILENAME = "settings.xml";
    
    /** The path to the default settings file. */
    final public static String DEFAULT_SETTINGS_PATH = RESOURCES_PATH + "/settings";
    
     /** The file path of default settings file. */
    final public static String DEFAULT_SETTINGS_FILEPATH = DEFAULT_SETTINGS_PATH 
            + "/" + SETTINGS_FILENAME;
    
    /** The path to the user settings file. */
    final public static String USER_SETTINGS_PATH = 
            System.getProperty("user.home") + "/.Couchware/Wezzle";
                    
    /** The file path of default settings file. */
    final public static String USER_SETTINGS_FILEPATH = USER_SETTINGS_PATH 
            + "/" + SETTINGS_FILENAME;
    
    /** The path to the log file. */
    final public static String LOG_PATH = USER_SETTINGS_PATH;
    
    /** The file path to the log file. */    
    final public static String LOG_FILEPATH = LOG_PATH + "/log.txt";
    
    /** All the keys that may be used. */
    public enum Key
    {
        CONF_VERSION, 
        GAME_RENDER_TYPE,        
        GAME_MUSIC,
        GAME_MUSIC_VOLUME,
        GAME_SOUND,
        GAME_SOUND_VOLUME,
        USER_HIGHSCORE_NAME_1,
        USER_HIGHSCORE_NAME_2,
        USER_HIGHSCORE_NAME_3,
        USER_HIGHSCORE_NAME_4,
        USER_HIGHSCORE_NAME_5,
        USER_HIGHSCORE_SCORE_1,
        USER_HIGHSCORE_SCORE_2,
        USER_HIGHSCORE_SCORE_3,
        USER_HIGHSCORE_SCORE_4,
        USER_HIGHSCORE_SCORE_5,
        USER_HIGHSCORE_LEVEL_1,
        USER_HIGHSCORE_LEVEL_2,
        USER_HIGHSCORE_LEVEL_3,
        USER_HIGHSCORE_LEVEL_4,
        USER_HIGHSCORE_LEVEL_5;                     
    }
    
    public enum Value
    {
        CONF_VERSION_1,
        GAME_RENDER_JAVA2D,
        GAME_RENDER_LWJGL;
    }
}

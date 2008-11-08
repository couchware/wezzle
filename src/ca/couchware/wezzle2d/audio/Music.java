/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.audio;

import ca.couchware.wezzle2d.manager.Settings;

/**
 * Contains all possible music tracks.
 * 
 * @author cdmckay
 */
public enum Music 
{             
    TRON1(Settings.MUSIC_PATH + "/Tron1.ogg"),
    TRON2(Settings.MUSIC_PATH + "/Tron2.ogg"),
    TRON3(Settings.MUSIC_PATH + "/Tron3.ogg"),
    ELECTRONIC1(Settings.MUSIC_PATH + "/Electronic1.ogg"),
    ELECTRONIC2(Settings.MUSIC_PATH + "/Electronic2.ogg"),
    ELECTRONIC3(Settings.MUSIC_PATH + "/Electronic3.ogg"),
    HIPPOP1(Settings.MUSIC_PATH + "/HipPop1.ogg"),
    HIPPOP2(Settings.MUSIC_PATH + "/HipPop2.ogg"),
    HIPPOP3(Settings.MUSIC_PATH + "/HipPop3.ogg");
    
    private String path;
    
    Music(String path)
    { this.path = path; }
    
    public String getPath()
    { return path; }
}

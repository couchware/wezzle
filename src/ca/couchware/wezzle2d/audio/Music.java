/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.audio;

import ca.couchware.wezzle2d.Game;

/**
 * Contains all possible music tracks.
 * 
 * @author cdmckay
 */
public enum Music 
{             
    TRON1(Game.MUSIC_PATH + "/Tron1.ogg"),
    TRON2(Game.MUSIC_PATH + "/Tron2.ogg"),
    TRON3(Game.MUSIC_PATH + "/Tron3.ogg"),
    ELECTRONIC1(Game.MUSIC_PATH + "/Electronic1.ogg"),
    ELECTRONIC2(Game.MUSIC_PATH + "/Electronic2.ogg"),
    ELECTRONIC3(Game.MUSIC_PATH + "/Electronic3.ogg"),
    HIPPOP1(Game.MUSIC_PATH + "/HipPop1.ogg"),
    HIPPOP2(Game.MUSIC_PATH + "/HipPop2.ogg"),
    HIPPOP3(Game.MUSIC_PATH + "/HipPop3.ogg");
    
    private String path;
    
    Music(String path)
    { this.path = path; }
    
    public String getPath()
    { return path; }
}

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
    TRON1(Settings.getMusicResourcesPath() + "/Tron1.ogg"),
    TRON2(Settings.getMusicResourcesPath() + "/Tron2.ogg"),
    TRON3(Settings.getMusicResourcesPath() + "/Tron3.ogg"),
    ELECTRONIC1(Settings.getMusicResourcesPath() + "/Electronic1.ogg"),
    ELECTRONIC2(Settings.getMusicResourcesPath() + "/Electronic2.ogg"),
    ELECTRONIC3(Settings.getMusicResourcesPath() + "/Electronic3.ogg"),
    HIPPOP1(Settings.getMusicResourcesPath() + "/HipPop1.ogg"),
    HIPPOP2(Settings.getMusicResourcesPath() + "/HipPop2.ogg"),
    HIPPOP3(Settings.getMusicResourcesPath() + "/HipPop3.ogg"),
    ERHU(Settings.getMusicResourcesPath() + "/Erhu.ogg");
    
    private String path;
    
    Music(String path)
    { this.path = path; }
    
    public String getPath()
    { return path; }
}

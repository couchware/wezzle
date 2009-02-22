/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.RadioGroup;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.SliderBar;
import java.util.EnumSet;

/**
 * The sound and music menu for Wezzle.  This menu consists of two buttons 
 * for turning the music on and off, and two sliders, for adjusting the music
 * volume.
 * 
 * @author cdmckay
 */
public class AudioGroup extends AbstractGroup
{           
    
    /** The header label. */
    private ITextLabel headerLabel;       
        
    /** The sound radio group options. */    
    final private static int SOUND_ON  = 0;
    final private static int SOUND_OFF = 1;
    
    /** The sound on/off radio group. */        
    private RadioGroup soundRadio;    
    
    /** The sound slider bar. */
    private SliderBar soundSlider;
    
    /** The music radio group options. */
    //private enum Music { ON, OFF }
    final private static int MUSIC_ON  = 0;
    final private static int MUSIC_OFF = 1;
    
    /** The music on/off radio group. */     
    private RadioGroup musicRadio;         
    
    /** The music slider bar. */
    private SliderBar musicSlider;    
    
    /** The back button. */
    private IButton backButton; 
    
    /**
     * The constructor.
     * 
     * @param window
     * @param layerMan
     * @param groupMan
     * @param propertyMan
     */    
    public AudioGroup(ManagerHub hub)
    {        
        // Create the options header.
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(26)
                .text("Audio").visible(false).end();
        hub.layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);
               
        // Create the "on" and "off" radio items.  These are used
        // in the radio groups below.
        RadioItem soundItem1 = new RadioItem.Builder().text("Sound On").end();
        RadioItem soundItem2 = new RadioItem.Builder().text("Off").end();        
        soundRadio = new RadioGroup.Builder(400, 233)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(soundItem1, SettingsManager.get().getBoolean(Key.USER_SOUND))
                .add(soundItem2, !SettingsManager.get().getBoolean(Key.USER_SOUND))
                .visible(false).end();
        hub.layerMan.add(soundRadio, Layer.UI);
        entityList.add(soundRadio);             
        
        // Clear flag.
        soundRadio.changed();
        
        // Create the sound slider bar.
        soundSlider = new SliderBar.Builder(400, 272)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .virtualRange(0, 100)                    
                .virtualValue(SettingsManager.get().getInt(Key.USER_SOUND_VOLUME))
                .visible(false).end();
        hub.layerMan.add(soundSlider, Layer.UI);
        entityList.add(soundSlider);        
                        
        // Create the "on" and "off" radio items.  These are used
        // in the radio groups below.
        RadioItem musicItem1 = new RadioItem.Builder().text("Music On").end();
        RadioItem musicItem2 = new RadioItem.Builder().text("Off").end();        
        musicRadio = new RadioGroup.Builder(400, 321)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(musicItem1, SettingsManager.get().getBoolean(Key.USER_MUSIC))
                .add(musicItem2, !SettingsManager.get().getBoolean(Key.USER_MUSIC))
                .visible(false).end();
        hub.layerMan.add(musicRadio, Layer.UI);
        entityList.add(musicRadio);            
        
        // Clear flag.
        musicRadio.changed();
        
        // Create the music slider bar.
        musicSlider = new SliderBar.Builder(400, 359)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .virtualRange(0, 100)
                .virtualValue(SettingsManager.get().getInt(Key.USER_MUSIC_VOLUME))
                .visible(false).end();
        hub.layerMan.add(musicSlider, Layer.UI);
        entityList.add(musicSlider);                      
        
        // Create back button.
        backButton = new Button.Builder(400, 420)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                //.type(SpriteButton.Type.THIN)
                .text("Back").normalOpacity(70).visible(false).end();        
        hub.layerMan.add(backButton, Layer.UI);     
        entityList.add(backButton);
    }
    
    /**
     * Override the update logic method.
     * 
     * @param game The game state.
     */    
    public void updateLogic(Game game, ManagerHub hub)
    {                
        // Sanity check.
        if(game == null || hub == null)
            throw new IllegalArgumentException("game and hub must not be null.");
        
        // Check if the back button was pressed.
        if (backButton.isActivated() == true)
        {                                    
            // Hide all side triggered menues.
            backButton.setActivated(false);
            hub.groupMan.hideGroup(this);                        
        }   
        else if (soundRadio.changed() == true)
        {
            boolean soundOn = soundRadio.getSelectedIndex() == SOUND_ON;
            
            // Set the property.            
            SettingsManager.get().setBoolean(Key.USER_SOUND, soundOn);            
         
            // Pause or unpause the sound depending on whether or not
            // the button is activated.
            hub.soundMan.setPaused(!soundOn);            
        }
        else if (musicRadio.changed() == true)
        {
            boolean musicOn = musicRadio.getSelectedIndex() == MUSIC_ON;
            
            // Set the property.            
            SettingsManager.get().setBoolean(Key.USER_MUSIC, musicOn);
            
            // Set the pausedness.
            hub.musicMan.setPaused(!musicOn);           
        } 
        else if (soundSlider.changed() == true)
        {
            // The new sound value.
            double gain = soundSlider.getVirtualPercent();                  
            
            // Set the volume.
            hub.soundMan.setNormalizedGain(gain);
        }
        else if (musicSlider.changed() == true)
        {
            // The new sound value.
            double gain = musicSlider.getVirtualPercent();
            
            // Set the volume.
            hub.musicMan.setNormalizedGain(gain);
        }
    }
}

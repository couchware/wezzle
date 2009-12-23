/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.group;

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

    private ManagerHub hub;

    private ITextLabel headerLabel;       
            
    final private static int ON  = 0;
    final private static int OFF = 1;
        
    private RadioGroup soundRadio;           
    private SliderBar soundSlider;      
        
    private RadioGroup musicRadio;            
    private SliderBar musicSlider;    
        
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
        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        this.hub = hub;

        // Create the options header.
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(26)
                .text("Audio").visible(false).build();
        hub.layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);
               
        // Create the "on" and "off" radio items.  These are used
        // in the radio groups below.
        RadioItem soundItem1 = new RadioItem.Builder().text("Sound On").build();
        RadioItem soundItem2 = new RadioItem.Builder().text("Off").build();
        soundRadio = new RadioGroup.Builder(400, 233)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(soundItem1, hub.settingsMan.getBool(Key.USER_SOUND))
                .add(soundItem2, !hub.settingsMan.getBool(Key.USER_SOUND))
                .visible(false).build();
        hub.layerMan.add(soundRadio, Layer.UI);
        entityList.add(soundRadio);
        
        // Clear flag.
        soundRadio.changed();
        
        // Create the sound slider bar.
        soundSlider = new SliderBar.Builder(400, 272)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .virtualRange(0, 100)                    
                .virtualValue(hub.settingsMan.getInt(Key.USER_SOUND_VOLUME))
                .visible(false).build();
        hub.layerMan.add(soundSlider, Layer.UI);
        entityList.add(soundSlider);
                        
        // Create the "on" and "off" radio items.  These are used
        // in the radio groups below.
        RadioItem musicItem1 = new RadioItem.Builder().text("Music On").build();
        RadioItem musicItem2 = new RadioItem.Builder().text("Off").build();
        musicRadio = new RadioGroup.Builder(400, 321)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(musicItem1, hub.settingsMan.getBool(Key.USER_MUSIC))
                .add(musicItem2, !hub.settingsMan.getBool(Key.USER_MUSIC))
                .visible(false).build();
        hub.layerMan.add(musicRadio, Layer.UI);
        entityList.add(musicRadio);
        
        // Clear flag.
        musicRadio.changed();
        
        // Create the music slider bar.
        musicSlider = new SliderBar.Builder(400, 359)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .virtualRange(0, 100)
                .virtualValue(hub.settingsMan.getInt(Key.USER_MUSIC_VOLUME))
                .visible(false).build();
        hub.layerMan.add(musicSlider, Layer.UI);
        entityList.add(musicSlider);
        
        // Create back button.
        backButton = new Button.Builder(400, 420)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                //.type(SpriteButton.Type.THIN)
                .text("Back").normalOpacity(70).visible(false).build();
        hub.layerMan.add(backButton, Layer.UI);     
        entityList.add(backButton);
    }

    @Override
    public void setActivated(boolean activated)
    {
        if (activated)
        {
            musicRadio.setSelectedIndex( hub.settingsMan.getBool( Key.USER_MUSIC ) ? 0 : 1 );
            musicSlider.setVirtualValue( hub.settingsMan.getInt(Key.USER_MUSIC_VOLUME) );
            soundRadio.setSelectedIndex( hub.settingsMan.getBool( Key.USER_SOUND ) ? 0 : 1 );
            soundSlider.setVirtualValue( hub.settingsMan.getInt(Key.USER_SOUND_VOLUME) );            
        }

        super.setActivated( activated );
    }
    
    /**
     * Override the update logic method.
     * 
     * @param game The game state.
     */    
    @Override
    public void updateLogic(Game game, ManagerHub hub)
    {
        super.updateLogic( game, hub );

        // Sanity check.
        if (game == null)
            throw new IllegalArgumentException("Game must not be null");

        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        // Make sure something changed.
        if ( !this.controlChanged() ) return;
        
        // Check if the back button was pressed.
        if ( false ) { }
        else if ( backButton.isActivated() )
        {                                    
            // Hide all side triggered menues.
            backButton.setActivated(false);
            hub.groupMan.hideGroup(this, !game.isCompletelyBusy());
        }   
        else if ( soundRadio.changed() )
        {
            boolean soundOn = soundRadio.getSelectedIndex() == ON;
            
            // Set the property.            
            SettingsManager.get().setBool(Key.USER_SOUND, soundOn);
         
            // Pause or unpause the sound depending on whether or not
            // the button is activated.
            hub.soundMan.setPaused(!soundOn);            
        }
        else if ( musicRadio.changed() )
        {
            boolean musicOn = musicRadio.getSelectedIndex() == ON;
            
            // Set the property.            
            SettingsManager.get().setBool(Key.USER_MUSIC, musicOn);
            
            // Set the pausedness.
            hub.musicMan.setPaused(!musicOn);           
        } 
        else if ( soundSlider.changed() )
        {
            // The new sound value.
            double gain = soundSlider.getVirtualPercent();                  
            
            // Set the volume.
            hub.soundMan.setNormalizedGain(gain);
        }
        else if ( musicSlider.changed() )
        {
            // The new sound value.
            double gain = musicSlider.getVirtualPercent();
            
            // Set the volume.
            hub.musicMan.setNormalizedGain(gain);
            hub.musicMan.exportSettings();
        }

        // Clear the change setting.
        this.clearChanged();
    }
}

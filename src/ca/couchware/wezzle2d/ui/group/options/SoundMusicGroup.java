/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui.group.options;

import ca.couchware.wezzle2d.manager.PropertyManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.SpriteButton;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.ui.group.*;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ui.*;
import java.util.EnumSet;

/**
 * The sound and music menu for Wezzle.  This menu consists of two buttons 
 * for turning the music on and off, and two sliders, for adjusting the music
 * volume.
 * 
 * @author cdmckay
 */
public class SoundMusicGroup extends AbstractGroup
{    
    
    /**
     * The layer manager.
     */
    final private LayerManager layerMan;
    
    /**
     * The header label.
     */
    private ILabel headerLabel;       
        
    /**
     * The sound radio group options.
     */
    private enum Sound { ON, OFF }
    
    /**
     * The sound on/off radio group.
     */        
    private RadioGroup<Sound> soundRadio;    
    
    /**
     * The sound slider bar.
     */
    private SliderBar soundSlider;
    
    /**
     * The music radio group options.
     */
    private enum Music { ON, OFF }
    
    /**
     * The music on/off radio group.
     */     
    private RadioGroup<Music> musicRadio;         
    
    /**
     * The music slider bar.
     */
    private SliderBar musicSlider;    
    
    /**
     * The back button.
     */
    private IButton backButton; 
    
    /**
     * The constructor.
     * 
     * @param window
     * @param layerMan
     * @param groupMan
     * @param propertyMan
     */    
    public SoundMusicGroup(
            final LayerManager layerMan,
            final PropertyManager propertyMan)
    {
        // Set the layer manager.
        this.layerMan = layerMan;
                
        // Create the options header.
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1).size(26).text("Sound/Music")
                .visible(false).end();
        layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);
               
        // Create the "on" and "off" radio items.  These are used
        // in the radio groups below.
        RadioItem soundItem1 = new RadioItem.Builder().text("Sound On").end();
        RadioItem soundItem2 = new RadioItem.Builder().text("Off").end();        
        soundRadio = new RadioGroup.Builder<Sound>(400, 233, Sound.class)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(Sound.ON, soundItem1).add(Sound.OFF, soundItem2)
                .visible(false).end();
        layerMan.add(soundRadio, Layer.UI);
        entityList.add(soundRadio);
        
        // Check the properties.
        if (propertyMan.getStringProperty(PropertyManager.KEY_SOUND)
                .equals(PropertyManager.VALUE_ON))  
        {
            //soundRadio.setSelectedItem(0);                    
            soundRadio.setSelectedKey(Sound.ON);
        }
        else        
        {
            //soundRadio.setSelectedItem(1);
            soundRadio.setSelectedKey(Sound.OFF);
        }
        
        // Clear flag.
        soundRadio.changed();
        
        // Create the sound slider bar.
        soundSlider = new SliderBar.Builder(400, 272)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .virtualRange(
                    propertyMan.getFloatProperty(PropertyManager.KEY_SOUND_MIN),
                    propertyMan.getFloatProperty(PropertyManager.KEY_SOUND_MAX))
                .virtualValue(
                    propertyMan.getFloatProperty(PropertyManager.KEY_SOUND_VOLUME))
                .visible(false).end();
        layerMan.add(soundSlider, Layer.UI);
        entityList.add(soundSlider);        
                        
        // Create the "on" and "off" radio items.  These are used
        // in the radio groups below.
        RadioItem musicItem1 = new RadioItem.Builder().text("Music On").end();
        RadioItem musicItem2 = new RadioItem.Builder().text("Off").end();        
        musicRadio = new RadioGroup.Builder<Music>(400, 321, Music.class)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(Music.ON, musicItem1).add(Music.OFF, musicItem2)
                .visible(false).end();
        layerMan.add(musicRadio, Layer.UI);
        entityList.add(musicRadio);
        
        // Check the properties.
        if (propertyMan.getStringProperty(PropertyManager.KEY_MUSIC)
                .equals(PropertyManager.VALUE_ON))
        {
            //musicRadio.setSelectedItem(0);
            musicRadio.setSelectedKey(Music.ON);
        }
        else        
        {
            //musicRadio.setSelectedItem(1);            
            musicRadio.setSelectedKey(Music.OFF);
        }
        
        // Clear flag.
        musicRadio.changed();
        
        // Create the music slider bar.
        musicSlider = new SliderBar.Builder(400, 359)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .virtualRange(
                    propertyMan.getFloatProperty(PropertyManager.KEY_MUSIC_MIN),
                    propertyMan.getFloatProperty(PropertyManager.KEY_MUSIC_MAX))
                .virtualValue(
                    propertyMan.getFloatProperty(PropertyManager.KEY_MUSIC_VOLUME))
                .visible(false).end();
        layerMan.add(musicSlider, Layer.UI);
        entityList.add(musicSlider);                      
        
        // Create back button.
        backButton = new SpriteButton.Builder(400, 408)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(SpriteButton.Type.THIN)
                .text("Back").offOpacity(70).visible(false).end();        
        layerMan.add(backButton, Layer.UI);     
        entityList.add(backButton);
    }
    
    /**
     * Override the update logic method.
     * 
     * @param game The game state.
     */    
    public void updateLogic(Game game)
    {                
        // Check if the back button was pressed.
        if (backButton.isActivated() == true)
        {                                    
            // Hide all side triggered menues.
            backButton.setActivated(false);
            game.groupMan.hideGroup(this);                        
        }   
        else if (soundRadio.changed() == true)
        {
            boolean soundOn = soundRadio.getSelectedKey() == Sound.ON;
            
            // Set the property.            
            game.propertyMan.setProperty(PropertyManager.KEY_SOUND,
                    soundOn == true
                    ? PropertyManager.VALUE_ON
                    : PropertyManager.VALUE_OFF);
            
         
            // Pause or unpause the sound depending on whether or not
            // the button is activated.
            game.soundMan.setPaused(!soundOn);            
        }
        else if (musicRadio.changed() == true)
        {
            boolean musicOn = musicRadio.getSelectedKey() == Music.ON;
            
            // Set the property.            
            game.propertyMan.setProperty(PropertyManager.KEY_MUSIC,
                    musicOn == true
                    ? PropertyManager.VALUE_ON
                    : PropertyManager.VALUE_OFF);
            
            // Unpause or start the music.
            if (musicOn == true)
            {
                game.musicMan.setPaused(false);
                
                if (game.musicMan.isPlaying() == false)                    
                    game.musicMan.playNext();                
            }
            // Pause music.
            else
            {
                game.musicMan.setPaused(true);
            }
        } 
        else if (soundSlider.changed() == true)
        {
            // The new sound value.
            double volume = soundSlider.getVirtualLower() 
                    + soundSlider.getVirtualValue();                      
            
            // Set the volume.
            game.soundMan.setVolume((float) volume);
        }
        else if (musicSlider.changed() == true)
        {
            // The new sound value.
            double volume = musicSlider.getVirtualLower() 
                    + musicSlider.getVirtualValue();                        
            
            // Set the volume.
            game.musicMan.setVolume((float) volume);
        }
    }
}

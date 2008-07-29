/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui.group.options;

import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.ui.group.*;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ui.*;
import ca.couchware.wezzle2d.ui.button.*;
import ca.couchware.wezzle2d.util.Util;
import java.util.EnumSet;

/**
 * The sound and music menu for Wezzle.  This menu consists of two buttons 
 * for turning the music on and off, and two sliders, for adjusting the music
 * volume.
 * 
 * @author cdmckay
 */
public class SoundMusicGroup extends Group
{
    /**
     * The header label.
     */
    private ILabel headerLabel;
    
    /**
     * The sound on/off button.
     */
    private IButton soundButton;
    
    /**
     * The sound slider bar.
     */
    private SliderBar soundSlider;
    
    /**
     * The music on/off button.
     */
    private IButton musicButton;
    
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
    public SoundMusicGroup(final GameWindow window, 
            final LayerManager layerMan, final GroupManager groupMan,
            final PropertyManager propertyMan)
    {
        // Invoke super.
        super(window, layerMan, groupMan);
        
        // Create the options header.
//        headerLabel = ResourceFactory.get().getLabel(400, 171);        
//        headerLabel.setSize(26);
//        headerLabel.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
//        headerLabel.setColor(Game.TEXT_COLOR);
//        headerLabel.setText("Sound/Music");
//        headerLabel.setVisible(false);
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(26).text("Sound/Music")
                .visible(false).end();
        layerMan.add(headerLabel, Game.LAYER_UI);
        entityList.add(headerLabel);
        
        // Create the sound on/off button.
        soundButton = new SpriteButton.Builder(window, 400, 233)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .text("Sound: Off").activeText("Sound: On")
                .normalOpacity(70).visible(false).end();       
        layerMan.add(soundButton, Game.LAYER_UI);
        entityList.add(soundButton);
        
        // Check the properties.
        if (propertyMan.getStringProperty(PropertyManager.KEY_SOUND)
                .equals(PropertyManager.VALUE_ON))
        {
            soundButton.setActivated(true);
        }
        
        // Create the sound slider bar.
        soundSlider = new SliderBar.Builder(window, 400, 272)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .virtualRange(
                    propertyMan.getFloatProperty(PropertyManager.KEY_SOUND_MIN),
                    propertyMan.getFloatProperty(PropertyManager.KEY_SOUND_MAX))
                .virtualValue(
                    propertyMan.getFloatProperty(PropertyManager.KEY_SOUND_VOLUME))
                .visible(false).end();
        layerMan.add(soundSlider, Game.LAYER_UI);
        entityList.add(soundSlider);        
                
        // Create the music on/off button.
        musicButton = new SpriteButton.Builder((SpriteButton) soundButton)
                .y(321).text("Music: Off").activeText("Music: On").end();     
        layerMan.add(musicButton, Game.LAYER_UI);
        entityList.add(musicButton);
        
        // Check the properties.
        if (propertyMan.getStringProperty(PropertyManager.KEY_MUSIC)
                .equals(PropertyManager.VALUE_ON))
        {           
            musicButton.setActivated(true);
        }
        
        // Create the music slider bar.
        musicSlider = new SliderBar.Builder(window, 400, 359)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .virtualRange(
                    propertyMan.getFloatProperty(PropertyManager.KEY_MUSIC_MIN),
                    propertyMan.getFloatProperty(PropertyManager.KEY_MUSIC_MAX))
                .virtualValue(
                    propertyMan.getFloatProperty(PropertyManager.KEY_MUSIC_VOLUME))
                .visible(false).end();
        layerMan.add(musicSlider, Game.LAYER_UI);
        entityList.add(musicSlider);                      
        
        // Create back button.
        backButton = new SpriteButton.Builder((SpriteButton) soundButton)
                .y(408).text("Back").end();      
        layerMan.add(backButton, Game.LAYER_UI);     
        entityList.add(backButton);
    }
    
    /**
     * Override the update logic method.
     * 
     * @param game The game state.
     */
    @Override
    public void updateLogic(Game game)
    {                
        // Check if the back button was pressed.
        if (backButton.isActivated() == true)
        {                                    
            // Hide all side triggered menues.
            backButton.setActivated(false);
            game.groupMan.hideGroup(this);
            
            // Save the volume settings.
            
        }   
        else if (soundButton.clicked() == true)
        {
            // Set the property.            
            game.propertyMan.setProperty(PropertyManager.KEY_SOUND,
                    soundButton.isActivated() == true
                    ? PropertyManager.VALUE_ON
                    : PropertyManager.VALUE_OFF);
            
         
            // Pause or unpause the sound depending on whether or not
            // the button is activated.
            game.soundMan.setPaused(!soundButton.isActivated());            
        }
        else if (musicButton.clicked() == true)
        {
            // Set the property.            
            game.propertyMan.setProperty(PropertyManager.KEY_MUSIC,
                    musicButton.isActivated() == true
                    ? PropertyManager.VALUE_ON
                    : PropertyManager.VALUE_OFF);
            
            // Unpause or start the music.
            if (musicButton.isActivated() == true)
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

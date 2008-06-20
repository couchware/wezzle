/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui.group.options;

import ca.couchware.wezzle2d.ui.group.*;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ui.*;
import ca.couchware.wezzle2d.ui.button.*;
import ca.couchware.wezzle2d.util.Util;

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
    private Label headerLabel;
    
    /**
     * The sound on/off button.
     */
    private RectangularBooleanButton soundButton;
    
    /**
     * The sound slider bar.
     */
    private SliderBar soundSlider;
    
    /**
     * The music on/off button.
     */
    private RectangularBooleanButton musicButton;
    
    /**
     * The music slider bar.
     */
    private SliderBar musicSlider;    
    
    /**
     * The back button.
     */
    private RectangularBooleanButton backButton; 
    
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
        headerLabel = ResourceFactory.get().getLabel(400, 171);        
        headerLabel.setSize(26);
        headerLabel.setAlignment(Label.VCENTER | Label.HCENTER);
        headerLabel.setColor(Game.TEXT_COLOR);
        headerLabel.setText("Sound/Music");
        headerLabel.setVisible(false);
        layerMan.add(headerLabel, Game.LAYER_UI);
        entityList.add(headerLabel);
        
        // Create the sound on/off button.
        soundButton = new RectangularBooleanButton(window, 400, 233)
        {
            // Make it so the button text changes to resume when
            // the button is activated.
            // Kevin: Be sure to read the comment in BooleanButton before
            // using this.
            @Override
            public void onActivation()
            {
                this.setText("Sound: On");
            }
            
            // Make it so the button text changes to pause when
            // the button is deactivated.
            @Override
            public void onDeactivation()
            {
                this.setText("Sound: Off");
            }
        };
        soundButton.setNormalOpacity(70);
        soundButton.setText("Sound: Off");
        soundButton.getLabel().setSize(18);
        soundButton.setAlignment(Button.VCENTER | Button.HCENTER);
        soundButton.setVisible(false);
        layerMan.add(soundButton, Game.LAYER_UI);
        entityList.add(soundButton);
        
        // Check the properties.
        if (propertyMan.getStringProperty(PropertyManager.KEY_SOUND)
                .equals(PropertyManager.VALUE_ON))
        {
            soundButton.setActivated(true);
        }
        
        // Create the sound slider bar.
        soundSlider = new SliderBar(window, 400, 272);
        soundSlider.setAlignment(Button.VCENTER | Button.HCENTER);
        soundSlider.setVisible(false);
        soundSlider.setVirtualRange(
                propertyMan.getFloatProperty(PropertyManager.KEY_SOUND_MIN),
                propertyMan.getFloatProperty(PropertyManager.KEY_SOUND_MAX));
        soundSlider.setVirtualValue(
                propertyMan.getFloatProperty(PropertyManager.KEY_SOUND_VOLUME));
        layerMan.add(soundSlider, Game.LAYER_UI);
        entityList.add(soundSlider);        
                
        // Create the music on/off button.
        musicButton = new RectangularBooleanButton(window, 400, 321)
        {
            // Make it so the button text changes to resume when
            // the button is activated.
            // Kevin: Be sure to read the comment in BooleanButton before
            // using this.
            @Override
            public void onActivation()
            {
                this.setText("Music: On");
            }
            
            // Make it so the button text changes to pause when
            // the button is deactivated.
            @Override
            public void onDeactivation()
            {
                this.setText("Music: Off");
            }
        };
        musicButton.setNormalOpacity(70);
        musicButton.setText("Music: Off");
        musicButton.getLabel().setSize(18);
        musicButton.setAlignment(Button.VCENTER | Button.HCENTER);
        musicButton.setVisible(false);
        layerMan.add(musicButton, Game.LAYER_UI);
        entityList.add(musicButton);
        
        // Check the properties.
        if (propertyMan.getStringProperty(PropertyManager.KEY_MUSIC)
                .equals(PropertyManager.VALUE_ON))
        {
            musicButton.setActivated(true);
        }
        
        // Create the music slider bar.
        musicSlider = new SliderBar(window, 400, 359);
        musicSlider.setAlignment(Button.VCENTER | Button.HCENTER);
        musicSlider.setVisible(false);
        musicSlider.setVirtualRange(
                propertyMan.getFloatProperty(PropertyManager.KEY_MUSIC_MIN),
                propertyMan.getFloatProperty(PropertyManager.KEY_MUSIC_MAX));
        musicSlider.setVirtualValue(
                propertyMan.getFloatProperty(PropertyManager.KEY_MUSIC_VOLUME));
        layerMan.add(musicSlider, Game.LAYER_UI);
        entityList.add(musicSlider);
        
        // Create back button.
        backButton = new RectangularBooleanButton(window, 400, 408);
        backButton.setNormalOpacity(70);
        backButton.setText("Back");
        backButton.getLabel().setSize(18);
        backButton.setAlignment(Button.VCENTER | Button.HCENTER);
        backButton.setVisible(false);
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
                if (game.musicMan.isPlaying() == true)
                    game.musicMan.setPaused(false);
                else
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

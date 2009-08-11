/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.RadioGroup;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.SliderBar;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * The play now group, which holds all the configuration options for playing
 * a Wezzle game.
 * 
 * @author cdmckay
 */
public class OptionsMenu extends AbstractMenu
{
    final private static int MIN_QUALITY = 0;       
    final private static int MAX_QUALITY = 4;

    final private static int MIN_VOLUME = 0;
    final private static int MAX_VOLUME = 100;

    final private static int ON = 0;
    final private static int OFF = 1;

    private enum GraphicsQuality
    {
        LOWEST("Lowest"),
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        HIGHEST("Highest");

        GraphicsQuality(String description)
        { this.description = description; }

        private String description;

        public String getDescription()
        { return description; }
    }
        
    private ITextLabel qualityValueLabel;        
    private int qualityValue = 1;        
    private SliderBar qualityValueSlider;
            
    private RadioGroup autoPauseRadio;        
    private RadioGroup piecePreviewBoxRadio;        
    private RadioGroup piecePreviewOverlayRadio;
    
    private RadioGroup musicRadio;
    private ITextLabel musicVolumeValueLabel;    
    private int musicVolumeValue;    
    private SliderBar musicVolumeValueSlider;
    private IButton musicTestButton;

    private RadioGroup soundRadio;
    private ITextLabel soundVolumeValueLabel;    
    private int soundVolumeValue;    
    private SliderBar soundVolumeValueSlider;
    private IButton soundTestButton;

    private enum Page
    {
        GAME("Game"),
        AUDIO("Audio");

        Page(String description)
        { this.description = description; }

        private String description;

        public String getDescription()
        { return description; }
    }

    /** The current page. */
    private Page currentPage = Page.GAME;

    /** The page 1 button. */
    private IButton gameButton;

    /** The page 2 button. */
    private IButton audioButton;

    /** The page 1 entities. */
    final private List<IEntity> gamePageEntities = new ArrayList<IEntity>();

    /** The page 2 entities. */
    final private List<IEntity> audioPageEntities = new ArrayList<IEntity>();
                    
    public OptionsMenu(IMenu parentMenu, ManagerHub hub, LayerManager menuLayerMan)
    {                
        // Invoke the super.
        super(parentMenu, hub, menuLayerMan);
               
        // The colors.
        final Color LABEL_COLOR  = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);        
        final Color OPTION_COLOR = hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY);

        // Create general entities.
        createMenuEntities(hub, LABEL_COLOR, OPTION_COLOR);

        // Page 0 entities.
        createEntitiesForGamePage(hub, LABEL_COLOR, OPTION_COLOR);

        // Page 1 entities.
        createEntitiesForAudioPage(hub, LABEL_COLOR, OPTION_COLOR);
           
        // Add them all to the layer manager.
        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }

        // Add all the starting page entities.
        for ( IEntity entity : this.gamePageEntities )
        {
            this.menuLayerMan.add(entity, Layer.UI);
            this.entityList.add(entity);
        }      
    }                   

    private void switchPage(Page page)
    {
        if (this.currentPage == page) return;
        this.currentPage = page;

        switch (page)
        {
            case GAME:

                for ( IEntity entity : this.gamePageEntities )                
                    addEntity(entity);

                for ( IEntity entity : this.audioPageEntities )                
                    removeEntity(entity);

                break;

            case AUDIO:

                for ( IEntity entity : this.gamePageEntities )
                    removeEntity(entity);

                for ( IEntity entity : this.audioPageEntities )
                    addEntity(entity);

                break;

            default:
                throw new IllegalArgumentException("Invalid options page");
        }
    }

    private void addEntity(IEntity entity)
    {
        this.menuLayerMan.add(entity, Layer.UI);
        this.entityList.add(entity);
        entity.setVisible(true);
        entity.setDisabled(false);
    }

    private void removeEntity(IEntity entity)
    {
        this.menuLayerMan.remove(entity, Layer.UI);
        this.entityList.remove(entity);
        entity.setVisible(false);
        entity.setDisabled(true);
    }

    private void createMenuEntities(ManagerHub hub,
            Color labelColor, Color optionColor)
    {
        Box optionBox = new Box.Builder(68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false).build();

        this.entityList.add(optionBox);

        // <editor-fold defaultstate="collapsed" desc="Title Label">
        
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor)
                .text("Options").size(20)
                .visible(false)
                .build();
        
        this.entityList.add(titleLabel);
        
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Audio Tab Button">

        this.audioButton = new Button.Builder(
                    optionBox.getX() + optionBox.getWidth() - 6,
                    97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT))
                .color(labelColor)
                .normalOpacity(90)
                .visible(false)
                .width(80)
                .text(Page.AUDIO.getDescription()).textSize(14)
                .build();

        this.audioButton.addButtonListener(new Button.IButtonListener()
        {
            public void buttonClicked()
            { switchPage(Page.AUDIO); }
        });

        this.entityList.add(this.audioButton);

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Game Tab Button">

        this.gameButton = new Button.Builder(
                    this.audioButton.getX() - this.audioButton.getWidth() - 5,
                    this.audioButton.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT))
                .color(labelColor)
                .normalOpacity(90)
                .visible(false)
                .width(80)
                .text(Page.GAME.getDescription()).textSize(14)
                .build();

        this.gameButton.addButtonListener(new Button.IButtonListener()
        {
            public void buttonClicked()
            { switchPage(Page.GAME); }
        });

        this.entityList.add(this.gameButton);

        // </editor-fold>
    }

    private void createEntitiesForGamePage(ManagerHub hub,
            Color labelColor, Color optionColor)
    {
        // <editor-fold defaultstate="collapsed" desc="Quality Value">
        // Get the user set level and make sure it's within range.
        this.qualityValue = hub.settingsMan.getInt(Key.USER_GRAPHICS_ANTIALIASING_SAMPLES);
        this.qualityValue = Math.max(MIN_QUALITY, this.qualityValue);
        this.qualityValue = Math.min(MAX_QUALITY, this.qualityValue);

        ITextLabel qualityLabel = new LabelBuilder(110, 180).alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT)).color(labelColor).text("Graphics Quality").size(20).visible(false).build();
        this.gamePageEntities.add(qualityLabel);

        this.qualityValueLabel = new LabelBuilder(
                qualityLabel.getX() + qualityLabel.getWidth() + 20,
                qualityLabel.getY()).alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT)).color(optionColor).size(20).visible(false).text(GraphicsQuality.values()[qualityValue].getDescription()).build();
        this.gamePageEntities.add(this.qualityValueLabel);

        this.qualityValueSlider = new SliderBar.Builder(
                268,
                qualityLabel.getY() + 35).alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).width(340).virtualRange(MIN_QUALITY, MAX_QUALITY).virtualValue(this.qualityValue).visible(false).build();
        this.gamePageEntities.add(qualityValueSlider);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Auto Pause">
        ITextLabel autoPauseLabel = new LabelBuilder(
                qualityLabel.getX(),
                qualityLabel.getY() + 80)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor)
                .text("Automatic Pause").size(20)
                .visible(false)
                .build();
        this.gamePageEntities.add(autoPauseLabel);

        RadioItem autoPauseOn = new RadioItem.Builder().color(optionColor).text("On").build();
        RadioItem autoPauseOff = new RadioItem.Builder().color(optionColor).text("Off").build();

        final boolean autoPauseSetting = hub.settingsMan.getBool(Key.USER_AUTO_PAUSE);
        this.autoPauseRadio = new RadioGroup.Builder(
                268,
                autoPauseLabel.getY() + 35).alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).add(autoPauseOn, autoPauseSetting).add(autoPauseOff, !autoPauseSetting).visible(false).build();
        this.gamePageEntities.add(autoPauseRadio);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Piece Preview Box">
        ITextLabel piecePreviewBoxLabel = new LabelBuilder(
                autoPauseLabel.getX(),
                autoPauseLabel.getY() + 85).alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT)).color(labelColor).text("Piece Preview Box").size(20).visible(false).build();
        this.gamePageEntities.add(piecePreviewBoxLabel);

        // Creat the level limit radio group.
        RadioItem boxItem1 = new RadioItem.Builder().color(optionColor).text("On").build();
        RadioItem boxItem2 = new RadioItem.Builder().color(optionColor).text("Off").build();

        // Attempt to get the user's music preference.
        final boolean piecePreviewBoxSetting = hub.settingsMan.getBool(Key.USER_PIECE_PREVIEW_TRADITIONAL);

        this.piecePreviewBoxRadio = new RadioGroup.Builder(
                268,
                piecePreviewBoxLabel.getY() + 35).alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).add(boxItem1, piecePreviewBoxSetting).add(boxItem2, !piecePreviewBoxSetting).itemSpacing(20).visible(false).build();
        this.gamePageEntities.add(piecePreviewBoxRadio);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Piece Preview Overlay">
        ITextLabel piecePreviewOverlayLabel = new LabelBuilder(
                piecePreviewBoxLabel.getX(),
                piecePreviewBoxLabel.getY() + 85).alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT)).color(labelColor).text("Piece Preview Overlay").size(20).visible(false).build();
        this.gamePageEntities.add(piecePreviewOverlayLabel);

        // Creat the level limit radio group.
        RadioItem overlayItem1 = new RadioItem.Builder().color(optionColor).text("On").build();

        RadioItem overlayItem2 = new RadioItem.Builder().color(optionColor).text("Off").build();

        // Attempt to get the user's music preference.
        final boolean piecePreviewOverlaySetting = hub.settingsMan.getBool(Key.USER_PIECE_PREVIEW_OVERLAY);

        this.piecePreviewOverlayRadio = new RadioGroup.Builder(
                    268,
                    piecePreviewOverlayLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(overlayItem1, piecePreviewOverlaySetting)
                .add(overlayItem2, !piecePreviewOverlaySetting)
                .itemSpacing(20)
                .visible(false)
                .build();
        this.gamePageEntities.add(piecePreviewOverlayRadio);
        // </editor-fold>
    }

    private void createEntitiesForAudioPage(ManagerHub hub,
            Color labelColor, Color optionColor)
    {
        // <editor-fold defaultstate="collapsed" desc="Music On/Off">
        ITextLabel musicLabel = new LabelBuilder(110, 180)                
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor)
                .text("Music").size(20)
                .visible(false)
                .build();
        this.audioPageEntities.add(musicLabel);

        RadioItem musicOn = new RadioItem.Builder().color(optionColor).text("On").build();
        RadioItem musicOff = new RadioItem.Builder().color(optionColor).text("Off").build();

        final boolean musicSetting = hub.settingsMan.getBool(Key.USER_MUSIC);
        this.musicRadio = new RadioGroup.Builder(
                    310,
                    musicLabel.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(musicOn, musicSetting)
                .add(musicOff, !musicSetting)
                .visible(false)
                .build();
        this.audioPageEntities.add(musicRadio);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Music Volume">
        // Get the user set level and make sure it's within range.
        this.musicVolumeValue = hub.settingsMan.getInt(Key.USER_MUSIC_VOLUME);
        this.musicVolumeValue = Math.max(MIN_VOLUME, this.musicVolumeValue);
        this.musicVolumeValue = Math.min(MAX_VOLUME, this.musicVolumeValue);

        ITextLabel musicVolumeLabel = new LabelBuilder(
                    musicLabel.getX(),
                    musicLabel.getY() + 40)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor).text("Music Volume").size(20)
                .visible(false)
                .build();
        this.audioPageEntities.add(musicVolumeLabel);

        this.musicVolumeValueLabel = new LabelBuilder(
                    musicVolumeLabel.getX() + musicVolumeLabel.getWidth() + 20,
                    musicVolumeLabel.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(optionColor)
                .size(20).visible(false)
                .text("" + this.musicVolumeValue)
                .build();
        this.audioPageEntities.add(this.musicVolumeValueLabel);

        this.musicVolumeValueSlider = new SliderBar.Builder(
                    268,
                    musicVolumeLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .width(340)
                .virtualRange(MIN_VOLUME, MAX_VOLUME)
                .virtualValue(this.musicVolumeValue)
                .visible(false)
                .build();
        this.audioPageEntities.add(musicVolumeValueSlider);
        // </editor-fold>     

        // <editor-fold defaultstate="collapsed" desc="Sound On/Off">
        ITextLabel soundLabel = new LabelBuilder(
                    musicVolumeLabel.getX(),
                    musicVolumeValueSlider.getY() + 60)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor)
                .text("Sound").size(20)
                .visible(false)
                .build();
        this.audioPageEntities.add(soundLabel);

        RadioItem soundOn = new RadioItem.Builder().color(optionColor).text("On").build();
        RadioItem soundOff = new RadioItem.Builder().color(optionColor).text("Off").build();

        final boolean soundSetting = hub.settingsMan.getBool(Key.USER_SOUND);
        this.soundRadio = new RadioGroup.Builder(
                    310,
                    soundLabel.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(soundOn, soundSetting)
                .add(soundOff, !soundSetting)
                .visible(false)
                .build();
        this.audioPageEntities.add(soundRadio);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Sound Volume">
        // Get the user set level and make sure it's within range.
        this.soundVolumeValue = hub.settingsMan.getInt(Key.USER_SOUND_VOLUME);
        this.soundVolumeValue = Math.max(MIN_VOLUME, this.soundVolumeValue);
        this.soundVolumeValue = Math.min(MAX_VOLUME, this.soundVolumeValue);

        ITextLabel soundVolumeLabel = new LabelBuilder(
                    soundLabel.getX(),
                    soundLabel.getY() + 40)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor).text("Sound Volume").size(20)
                .visible(false)
                .build();
        this.audioPageEntities.add(soundVolumeLabel);

        this.soundVolumeValueLabel = new LabelBuilder(
                    soundVolumeLabel.getX() + soundVolumeLabel.getWidth() + 20,
                    soundVolumeLabel.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(optionColor)
                .size(20).visible(false)
                .text("" + this.soundVolumeValue)
                .build();
        this.audioPageEntities.add(this.soundVolumeValueLabel);

        this.soundVolumeValueSlider = new SliderBar.Builder(
                    268,
                    soundVolumeLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .width(340)
                .virtualRange(MIN_VOLUME, MAX_VOLUME)
                .virtualValue(this.soundVolumeValue)
                .visible(false)
                .build();
        this.audioPageEntities.add(soundVolumeValueSlider);
        // </editor-fold>

        this.musicTestButton = new Button.Builder(
                    258,
                    soundVolumeValueSlider.getY() + 65)
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT) )
                .color( labelColor )
                .normalOpacity(90)
                .visible(false)
                .text("Test Music").textSize(16)
                .width( 140 )
                .build();
        this.audioPageEntities.add(this.musicTestButton);

        this.soundTestButton = new Button.Builder(
                    278,
                    soundVolumeValueSlider.getY() + 65)
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color( labelColor )
                .normalOpacity(90)
                .visible(false)
                .text("Test Sound").textSize(16)
                .width( 140 )
                .build();
        this.audioPageEntities.add(this.soundTestButton);
    }

    public void updateLogic(Game game, ManagerHub hub)
    {
        if ( this.qualityValueSlider.changed() )
        {
            qualityValue = qualityValueSlider.getVirtualValue();
            this.qualityValueLabel.setText(
                    GraphicsQuality.values()[qualityValue].getDescription());
            hub.settingsMan.setInt(Key.USER_GRAPHICS_ANTIALIASING_SAMPLES, this.qualityValue);
        }

        if ( this.autoPauseRadio.changed() )
        {
            int selected = this.autoPauseRadio.getSelectedIndex();
            boolean isOn = selected == ON;

            hub.settingsMan.setBool(Key.USER_AUTO_PAUSE, isOn);
        }

        if ( this.piecePreviewBoxRadio.changed() )
        {
            int selected = this.piecePreviewBoxRadio.getSelectedIndex();
            boolean isOn = selected == ON;

            hub.settingsMan.setBool(Key.USER_PIECE_PREVIEW_TRADITIONAL, isOn);
            game.getUI().setTraditionalPiecePreviewVisible(isOn);
        }

        if ( this.piecePreviewOverlayRadio.changed() )
        {
            int selected = this.piecePreviewOverlayRadio.getSelectedIndex();
            boolean isOn = selected == ON;

            hub.settingsMan.setBool(Key.USER_PIECE_PREVIEW_OVERLAY, isOn);
            game.getUI().setOverlayPiecePreviewVisible(isOn);
        }

        if ( this.musicRadio.changed() )
        {
            int selected = this.musicRadio.getSelectedIndex();
            boolean isOn = selected == ON;

            hub.settingsMan.setBool(Key.USER_MUSIC, isOn);
        }

        if ( this.soundRadio.changed() )
        {
            int selected = this.soundRadio.getSelectedIndex();
            boolean isOn = selected == ON;

            hub.settingsMan.setBool(Key.USER_SOUND, isOn);
        }

        if ( this.musicVolumeValueSlider.changed() )
        {
            musicVolumeValue = musicVolumeValueSlider.getVirtualValue();
            this.musicVolumeValueLabel.setText("" + musicVolumeValue);
            hub.settingsMan.setInt(Key.USER_MUSIC_VOLUME, this.musicVolumeValue);
        }

        if ( this.soundVolumeValueSlider.changed() )
        {
            soundVolumeValue = soundVolumeValueSlider.getVirtualValue();
            this.soundVolumeValueLabel.setText("" + soundVolumeValue);
            hub.settingsMan.setInt(Key.USER_SOUND_VOLUME, this.soundVolumeValue);
        }
    }

} // end class

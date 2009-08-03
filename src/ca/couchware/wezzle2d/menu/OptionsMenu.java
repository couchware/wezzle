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
    
    /** The minimum number of FSAA samples. */
    final private static int MIN_QUALITY = 0;
    
    /** The maximum number of FSAA samples. */
    final private static int MAX_QUALITY = 4;

    /** The minimum music/sound volume value. */
    final private static int MIN_VOLUME = 0;

    /** The maximum music/sound volume value. */
    final private static int MAX_VOLUME = 100;  

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
    
    /** The quality value label. */
    private ITextLabel qualityValueLabel;
    
    /** The quality value. */
    private int qualityValue = 1;
    
    /** The quality value slider. */
    private SliderBar qualityValueSlider;
    
    /** The possibilities for the max level. */
    final private static int AUTO_PAUSE_ON = 0;
    final private static int AUTO_PAUSE_OFF = 1;
    
    /** The max level radio group. */
    private RadioGroup autoPauseRadio;

    /** The possibilities for the max level. */
    final private static int PIECE_PREVIEW_BOX_ON = 0;
    final private static int PIECE_PREVIEW_BOX_OFF = 1;

    /** The piece preview box radio group. */
    private RadioGroup piecePreviewBoxRadio;

    /** The possibilities for the max level. */
    final private static int PIECE_PREVIEW_OVERLAY_ON = 0;
    final private static int PIECE_PREVIEW_OVERLAY_OFF = 1;

    /** The piece preview overlay radio group. */
    private RadioGroup piecePreviewOverlayRadio;

    /** The music volume value label. */
    private ITextLabel musicVolumeValueLabel;

    /** The music volume value. */
    private int musicVolumeValue;

    /** The music volume slider. */
    private SliderBar musicVolumeValueSlider;

    /** The current page. */
    private int currentPage = 0;

    /** The page 1 button. */
    private IButton page0Button;

    /** The page 2 button. */
    private IButton page1Button;

    /** The page 1 entities. */
    final private List<IEntity> page0EntityList = new ArrayList<IEntity>();

    /** The page 2 entities. */
    final private List<IEntity> page1EntityList = new ArrayList<IEntity>();
                    
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
        createEntitiesForPage0(hub, LABEL_COLOR, OPTION_COLOR);

        // Page 1 entities.
        createEntitiesForPage1(hub, LABEL_COLOR, OPTION_COLOR);
           
        // Add them all to the layer manager.
        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }

        // Add all the starting page entities.
        for ( IEntity entity : this.page0EntityList )
        {
            this.menuLayerMan.add(entity, Layer.UI);
            this.entityList.add(entity);
        }      
    }                   

    private void switchPage(int page)
    {
        if (this.currentPage == page) return;
        this.currentPage = page;

        switch (page)
        {
            case 0:

                for ( IEntity entity : this.page0EntityList )
                {
                    this.menuLayerMan.add(entity, Layer.UI);
                    this.entityList.add(entity);
                    entity.setVisible(true);
                    entity.setDisabled(false);
                }

                for ( IEntity entity : this.page1EntityList )
                {
                    this.menuLayerMan.remove(entity, Layer.UI);
                    this.entityList.remove(entity);
                    entity.setDisabled(true);
                }

                break;

            case 1:

                for ( IEntity entity : this.page0EntityList )
                {
                    this.menuLayerMan.remove(entity, Layer.UI);
                    this.entityList.remove(entity);
                    entity.setDisabled(true);
                }

                for ( IEntity entity : this.page1EntityList )
                {
                    this.menuLayerMan.add(entity, Layer.UI);
                    this.entityList.add(entity);
                    entity.setVisible(true);
                    entity.setDisabled(false);
                }

                break;

            default:
                throw new IllegalArgumentException("Invalid options page number");
        }
    }

    private void createMenuEntities(ManagerHub hub, Color labelColor, Color optionColor)
    {
        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor)
                .text("Options").size(20)
                .visible(false)
                .build();
        this.entityList.add(titleLabel);

        // The page buttons.
        this.page0Button = new Button.Builder(418, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT))
                .color(labelColor)
                .normalOpacity(90)
                .visible(false)
                .width(40)
                .text("1").textSize(16)
                .build();

        this.page0Button.addButtonListener(new Button.IButtonListener()
        {
            public void buttonClicked()
            { switchPage(0); }
        });
        this.entityList.add(this.page0Button);

        this.page1Button = new Button.Builder(463, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT))
                .color(labelColor)
                .normalOpacity(90)
                .visible(false)
                .width(40)
                .text("2").textSize(16)
                .build();

        this.page1Button.addButtonListener(new Button.IButtonListener()
        {
            public void buttonClicked()
            { switchPage(1); }
        });
        this.entityList.add(this.page1Button);

        // The first box.
        Box optionBox = new Box.Builder(68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false).build();

        this.entityList.add(optionBox);
    }

    private void createEntitiesForPage0(ManagerHub hub,
            Color labelColor, Color optionColor)
    {
        // Get the user set level and make sure it's within range.
        this.qualityValue = hub.settingsMan.getInt(Key.USER_GRAPHICS_ANTIALIASING_SAMPLES);
        this.qualityValue = Math.max(MIN_QUALITY, this.qualityValue);
        this.qualityValue = Math.min(MAX_QUALITY, this.qualityValue);

        ITextLabel qualityLabel = new LabelBuilder(110, 180)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor).text("Graphics Quality").size(20)
                .visible(false)
                .build();
        this.page0EntityList.add(qualityLabel);

        this.qualityValueLabel = new LabelBuilder(
                    qualityLabel.getX() + qualityLabel.getWidth() + 20,
                    qualityLabel.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(optionColor)
                .size(20).visible(false)
                .text(GraphicsQuality.values()[qualityValue].getDescription())
                .build();
        this.page0EntityList.add(this.qualityValueLabel);

        this.qualityValueSlider = new SliderBar.Builder(
                    268,
                    qualityLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .width(340)
                .virtualRange(MIN_QUALITY, MAX_QUALITY)
                .virtualValue(this.qualityValue)
                .visible(false)
                .build();
        this.page0EntityList.add(qualityValueSlider);

        ITextLabel autoPauseLabel = new LabelBuilder(
                    qualityLabel.getX(),
                    qualityLabel.getY() + 80)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor).text("Automatic Pause").size(20)
                .visible(false)
                .build();
        this.page0EntityList.add(autoPauseLabel);

        RadioItem autoPauseOn = new RadioItem.Builder()
                .color(optionColor)
                .text("On").build();

        RadioItem autoPauseOff = new RadioItem.Builder()
                .color(optionColor)
                .text("Off").build();

        final boolean autoPauseSetting = hub.settingsMan.getBool(Key.USER_AUTO_PAUSE);
        this.autoPauseRadio = new RadioGroup.Builder(
                    268,
                    autoPauseLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(autoPauseOn,  autoPauseSetting)
                .add(autoPauseOff, !autoPauseSetting)
                .visible(false)
                .build();
        this.page0EntityList.add(autoPauseRadio);

        ITextLabel piecePreviewBoxLabel = new LabelBuilder(
                    autoPauseLabel.getX(),
                    autoPauseLabel.getY() + 85)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor).text("Piece Preview Box").size(20)
                .visible(false)
                .build();
        this.page0EntityList.add(piecePreviewBoxLabel);

        // Creat the level limit radio group.
        RadioItem boxItem1 = new RadioItem.Builder()
                .color(optionColor)
                .text("On").build();

        RadioItem boxItem2 = new RadioItem.Builder()
                .color(optionColor)
                .text("Off").build();

        // Attempt to get the user's music preference.
        final boolean piecePreviewBoxSetting = hub.settingsMan.getBool(Key.USER_PIECE_PREVIEW_TRADITIONAL);

        this.piecePreviewBoxRadio = new RadioGroup.Builder(
                    268,
                    piecePreviewBoxLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(boxItem1, piecePreviewBoxSetting)
                .add(boxItem2, !piecePreviewBoxSetting)
                .itemSpacing(20).visible(false).build();
        this.page0EntityList.add(piecePreviewBoxRadio);

        ITextLabel piecePreviewOverlayLabel = new LabelBuilder(
                    piecePreviewBoxLabel.getX(),
                    piecePreviewBoxLabel.getY() + 85)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor).text("Piece Preview Overlay").size(20)
                .visible(false)
                .build();
        this.page0EntityList.add(piecePreviewOverlayLabel);

        // Creat the level limit radio group.
        RadioItem overlayItem1 = new RadioItem.Builder()
                .color(optionColor)
                .text("On").build();

        RadioItem overlayItem2 = new RadioItem.Builder()
                .color(optionColor)
                .text("Off").build();

        // Attempt to get the user's music preference.
        final boolean piecePreviewOverlaySetting = hub.settingsMan.getBool(Key.USER_PIECE_PREVIEW_OVERLAY);

        this.piecePreviewOverlayRadio = new RadioGroup.Builder(
                    268,
                    piecePreviewOverlayLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(overlayItem1, piecePreviewOverlaySetting)
                .add(overlayItem2, !piecePreviewOverlaySetting)
                .itemSpacing(20).visible(false).build();
        this.page0EntityList.add(piecePreviewOverlayRadio);
    }

    private void createEntitiesForPage1(ManagerHub hub,
            Color labelColor, Color optionColor)
    {
        // Get the user set level and make sure it's within range.
        this.musicVolumeValue = hub.settingsMan.getInt(Key.USER_MUSIC_VOLUME);
        this.musicVolumeValue = Math.max(MIN_VOLUME, this.musicVolumeValue);
        this.musicVolumeValue = Math.min(MAX_VOLUME, this.musicVolumeValue);

        ITextLabel musicVolumeLabel = new LabelBuilder(110, 180)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor).text("Music Volume").size(20)
                .visible(false)
                .build();
        this.page1EntityList.add(musicVolumeLabel);

        this.musicVolumeValueLabel = new LabelBuilder(
                    musicVolumeLabel.getX() + musicVolumeLabel.getWidth() + 20,
                    musicVolumeLabel.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY))
                .size(20).visible(false)
                .text("" + this.musicVolumeValue)
                .build();
        this.page1EntityList.add(this.musicVolumeValueLabel);

        this.musicVolumeValueSlider = new SliderBar.Builder(
                    268,
                    musicVolumeLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .width(340)
                .virtualRange(MIN_VOLUME, MAX_VOLUME)
                .virtualValue(this.musicVolumeValue)
                .visible(false)
                .build();
        this.page1EntityList.add(musicVolumeValueSlider);
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
            boolean isOn = selected == AUTO_PAUSE_ON;

            hub.settingsMan.setBool(Key.USER_AUTO_PAUSE, isOn);
        }

        if ( this.piecePreviewBoxRadio.changed() )
        {
            int selected = this.piecePreviewBoxRadio.getSelectedIndex();
            boolean isOn = selected == PIECE_PREVIEW_BOX_ON;

            hub.settingsMan.setBool(Key.USER_PIECE_PREVIEW_TRADITIONAL, isOn);
            game.getUI().setTraditionalPiecePreviewVisible(isOn);
        }

        if ( this.piecePreviewOverlayRadio.changed() )
        {
            int selected = this.piecePreviewOverlayRadio.getSelectedIndex();
            boolean isOn = selected == PIECE_PREVIEW_OVERLAY_ON;

            hub.settingsMan.setBool(Key.USER_PIECE_PREVIEW_OVERLAY, isOn);
            game.getUI().setOverlayPiecePreviewVisible(isOn);
        }

        if ( this.musicVolumeValueSlider.changed() )
        {
            musicVolumeValue = musicVolumeValueSlider.getVirtualValue();
            this.musicVolumeValueLabel.setText("" + musicVolumeValue);
            hub.settingsMan.setInt(Key.USER_MUSIC_VOLUME, this.musicVolumeValue);
        }
    }

}

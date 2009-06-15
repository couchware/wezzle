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
import ca.couchware.wezzle2d.manager.MusicManager.Theme;
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
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * The play now group, which holds all the configuration options for playing
 * a Wezzle game.
 * 
 * @author cdmckay
 */
public class OptionsMenu extends AbstractMenu
{
    
    /** The minimum number of FSAA samples. */
    final private static int MIN_SAMPLES = 0;
    
    /** The maximum number of FSAA samples. */
    final private static int MAX_SAMPLES = 4;

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
    
    /** The level label. */
    private final ITextLabel sampleNumberLabel;
    
    /** The level. */
    private int sampleNumber = 1;
    
    /** The level slider. */
    private final SliderBar sampleSlider;
    
    /** The possibilities for the max level. */
    final private static int MAX_LEVEL_ON = 0;
    final private static int MAX_LEVEL_OFF = 1;
    
    /** The max level radio group. */
    final private RadioGroup maxLevelRadio;

    /** The possibilities for the max level. */
    final private static int PIECE_PREVIEW_BOX_ON = 0;
    final private static int PIECE_PREVIEW_BOX_OFF = 1;

    /** The piece preview box radio group. */
    final private RadioGroup piecePreviewBoxRadio;

    /** The possibilities for the max level. */
    final private static int PIECE_PREVIEW_OVERLAY_ON = 0;
    final private static int PIECE_PREVIEW_OVERLAY_OFF = 1;

    /** The piece preview overlay radio group. */
    final private RadioGroup piecePreviewOverlayRadio;

    /** The page 1 button. */
    final private IButton page1Button;

    /** The page 2 button. */
    final private IButton page2Button;
                    
    public OptionsMenu(IMenu parentMenu, ManagerHub hub, LayerManager menuLayerMan)
    {                
        // Invoke the super.
        super(parentMenu, hub, menuLayerMan);
               
        // The colors.
        final Color LABEL_COLOR  = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);        
        final Color OPTION_COLOR = hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY);                    
               
        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Options").size(20)
                .visible(false).build();
        this.entityList.add(titleLabel);

        // The page buttons.
        this.page1Button = new Button.Builder(418, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .normalOpacity(90)
                .visible(false)
                .text("1")
                .width(40)
                .textSize(16)
                .build();
        this.entityList.add(this.page1Button);

        this.page2Button = new Button.Builder(463, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .normalOpacity(90)
                .visible(false)
                .text("2")
                .width(40)
                .textSize(16)
                .build();
        this.entityList.add(this.page2Button);

        // The first box.
        Box optionBox = new Box.Builder(68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .build();
        this.entityList.add(optionBox);

        // Get the user set level and make sure it's within range.
        this.sampleNumber = hub.settingsMan.getInt(Key.USER_GRAPHICS_ANTIALIASING_SAMPLES);
        this.sampleNumber = Math.max(MIN_SAMPLES, this.sampleNumber);
        this.sampleNumber = Math.min(MAX_SAMPLES, this.sampleNumber);

        ITextLabel levelLabel = new LabelBuilder(110, 180)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Graphics Quality").size(20)
                .visible(false)
                .build();
        this.entityList.add(levelLabel);
        
        this.sampleNumberLabel = new LabelBuilder(
                    levelLabel.getX() + levelLabel.getWidth() + 20, 
                    levelLabel.getY())                
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY))
                .size(20).visible(false)
                .text(GraphicsQuality.values()[sampleNumber].getDescription())
                .build();
        this.entityList.add(this.sampleNumberLabel);
        
        this.sampleSlider = new SliderBar.Builder(
                    268, 
                    levelLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .width(340)
                .virtualRange(MIN_SAMPLES, MAX_SAMPLES)
                .virtualValue(this.sampleNumber)
                .visible(false)
                .build();
        this.entityList.add(sampleSlider);
        
        ITextLabel maxLevelLabel = new LabelBuilder(
                    levelLabel.getX(), 
                    levelLabel.getY() + 80)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Maximum Level").size(20)
                .visible(false)
                .build();
        this.entityList.add(maxLevelLabel);
        
        RadioItem maxLevelOn = new RadioItem.Builder()
                .color(OPTION_COLOR)
                .text("20").build();
         
        RadioItem maxLevelOff = new RadioItem.Builder()
                .color(OPTION_COLOR)
                .text("No Limit").build();

        final boolean maxLevelSetting = hub.settingsMan.getBool(Key.USER_MAX_LEVEL_CAPPED);
        this.maxLevelRadio = new RadioGroup.Builder(
                    268,
                    maxLevelLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .add(maxLevelOn,  maxLevelSetting)
                .add(maxLevelOff, !maxLevelSetting)
                .visible(false)
                .build();
        this.entityList.add(maxLevelRadio);
        
        ITextLabel piecePreviewBoxLabel = new LabelBuilder(
                    maxLevelLabel.getX(),
                    maxLevelLabel.getY() + 85)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Piece Preview Box").size(20)
                .visible(false)
                .build();
        this.entityList.add(piecePreviewBoxLabel);
        
        // Creat the level limit radio group.        
        RadioItem boxItem1 = new RadioItem.Builder()
                .color(OPTION_COLOR)
                .text("On").build();
        
        RadioItem boxItem2 = new RadioItem.Builder()
                .color(OPTION_COLOR)
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
        this.entityList.add(piecePreviewBoxRadio);

        ITextLabel piecePreviewOverlayLabel = new LabelBuilder(
                    piecePreviewBoxLabel.getX(),
                    piecePreviewBoxLabel.getY() + 85)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Piece Preview Overlay").size(20)
                .visible(false)
                .build();
        this.entityList.add(piecePreviewOverlayLabel);

        // Creat the level limit radio group.
        RadioItem overlayItem1 = new RadioItem.Builder()
                .color(OPTION_COLOR)
                .text("On").build();

        RadioItem overlayItem2 = new RadioItem.Builder()
                .color(OPTION_COLOR)
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
        this.entityList.add(piecePreviewOverlayRadio);
                
        // Add them all to the layer manager.
        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }
    }       
        
    public void updateLogic(Game game, ManagerHub hub)
    {      
        if ( this.sampleSlider.changed() )
        {
            sampleNumber = (int) sampleSlider.getVirtualValue();
            this.sampleNumberLabel.setText(
                    GraphicsQuality.values()[sampleNumber].getDescription());
            hub.settingsMan.setInt(Key.USER_GRAPHICS_ANTIALIASING_SAMPLES, this.sampleNumber);
        }

        if ( this.maxLevelRadio.changed() )
        {
            int selected = this.maxLevelRadio.getSelectedIndex();
            boolean isOn = selected == MAX_LEVEL_ON;

            hub.settingsMan.setBool(Key.USER_MAX_LEVEL_CAPPED, isOn);
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
    }

}

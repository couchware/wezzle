/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.HighScore;
import ca.couchware.wezzle2d.manager.HighScoreManager;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

/**
 * The high score menu, which holds all the high scores for the user to peruse
 * at his/her leisureee... so... leisurely.
 * 
 * @author cdmckay
 */
public class AboutMenu extends AbstractMenu
{

    public AboutMenu(IMenu parent, ManagerHub hub, LayerManager menuLayerMan)
    {
        // Invoke super.
        super(parent, hub, menuLayerMan);

        // The colors.
        final Color LABEL_COLOR  = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);
        final Color OPTION_COLOR = hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY);

        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR)
                .text("About").size(20)
                .visible( false ).build();

        this.entityList.add(titleLabel);               

        // The box.
        Box optionBox = new Box.Builder(68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .build();

        this.entityList.add(optionBox);

        int titleX = 110;
        int nameX  = 130;
        int startY = 182;

        // The design label.
        ITextLabel designLabel =
                new LabelBuilder( titleX, startY )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(LABEL_COLOR)
                .text("Design").size(20)
                .visible( false ).build();

        this.entityList.add(designLabel);

        ITextLabel designersLabel =
                new LabelBuilder( nameX, designLabel.getY() + 35 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(OPTION_COLOR)
                .text("Kevin Grad   Cameron McKay").size(16)
                .visible( false ).build();

        this.entityList.add(designersLabel);

        // The programming label.
        ITextLabel programmingLabel =
                new LabelBuilder( titleX, designLabel.getY() + 80 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(LABEL_COLOR)
                .text("Programming").size(20)
                .visible( false ).build();

        this.entityList.add(programmingLabel);

        ITextLabel programmersLabel =
                new LabelBuilder( nameX, programmingLabel.getY() + 35 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(OPTION_COLOR)
                .text("Cameron McKay   Kevin Grad").size(16)
                .visible( false ).build();

        this.entityList.add(programmersLabel);

        // The artwork label.
        ITextLabel artworkLabel =
                new LabelBuilder( titleX, programmingLabel.getY() + 80 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(LABEL_COLOR)
                .text("Artwork").size(20)
                .visible( false ).build();

        this.entityList.add(artworkLabel);

        ITextLabel artistsLabel =
                new LabelBuilder( nameX, artworkLabel.getY() + 35 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(OPTION_COLOR)
                .text("Renee Lung").size(16)
                .visible( false ).build();

        this.entityList.add(artistsLabel);

        // The artwork label.
        ITextLabel musicLabel =
                new LabelBuilder( titleX, artworkLabel.getY() + 80 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(LABEL_COLOR)
                .text("Music").size(20)
                .visible( false ).build();

        this.entityList.add(musicLabel);

        ITextLabel musiciansLabel =
                new LabelBuilder( nameX, musicLabel.getY() + 35 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(OPTION_COLOR)
                .text("Sam Lee (120bpm)").size(16)
                .visible( false ).build();

        this.entityList.add(musiciansLabel);

        // Add them all to the layer manager.
        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }
    }

    public void updateLogic(Game game, ManagerHub hub)
    { 
        
    }   

}

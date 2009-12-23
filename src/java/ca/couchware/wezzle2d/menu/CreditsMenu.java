/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.IAnimation;
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
import ca.couchware.wezzle2d.util.NumUtil;
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
public class CreditsMenu extends AbstractMenu
{
    private int designer1 = NumUtil.random.nextInt( 2 );
    private int designer2 = (designer1 + 1) % 2;

    private final ITextLabel designer1Label;
    private final ITextLabel designer2Label;
    private final ITextLabel designer1CreditsLabel;
    private final ITextLabel designer2CreditsLabel;

    private final String[] designers = new String[]
    {
        "Cameron McKay",
        "Kevin Grad"
    };

    private final String[] designerCredits = new String[]
    {
        "Design, Programming, Art",
        "Design, Programming"
    };

    public CreditsMenu(IMenu parent, ManagerHub hub, LayerManager menuLayerMan)
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
                .text("Credits").size(20)
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

        final int titleX = 110;
        final int nameX  = 130;
        final int startY = 175;

        final int spacing1 = 35;
        final int spacing2 = 85;

        // The design label.
        this.designer1Label =
                new LabelBuilder( titleX, startY )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(LABEL_COLOR)
                .text("").size(20)
                .visible( false ).build();

        this.entityList.add(designer1Label);

        this.designer1CreditsLabel =
                new LabelBuilder( nameX, designer1Label.getY() + spacing1 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(OPTION_COLOR)
                .text("").size(16)
                .visible( false ).build();

        this.entityList.add(designer1CreditsLabel);

        // The programming label.
        this.designer2Label =
                new LabelBuilder( titleX, designer1Label.getY() + spacing2 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(LABEL_COLOR)
                .text("").size(20)
                .visible( false ).build();

        this.entityList.add(designer2Label);

        this.designer2CreditsLabel =
                new LabelBuilder( nameX, designer2Label.getY() + spacing1 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(OPTION_COLOR)
                .text("").size(16)
                .visible( false ).build();

        this.entityList.add(designer2CreditsLabel);

        // The artwork label.
        ITextLabel artistLabel =
                new LabelBuilder( titleX, designer2Label.getY() + spacing2 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(LABEL_COLOR)
                .text("Renee Lung").size(20)
                .visible( false ).build();

        this.entityList.add(artistLabel);

        ITextLabel artistCreditsLabel =
                new LabelBuilder( nameX, artistLabel.getY() + spacing1 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(OPTION_COLOR)
                .text("Art").size(16)
                .visible( false ).build();

        this.entityList.add(artistCreditsLabel);

        // The artwork label.
        ITextLabel musicianLabel =
                new LabelBuilder( titleX, artistLabel.getY() + spacing2 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(LABEL_COLOR)
                .text("Sam Lee (120bpm)").size(20)
                .visible( false ).build();

        this.entityList.add(musicianLabel);

        ITextLabel musicianCreditsLabel =
                new LabelBuilder( nameX, musicianLabel.getY() + spacing1 )
                .alignment( EnumSet.of(Alignment.MIDDLE, Alignment.LEFT) )
                .color(OPTION_COLOR)
                .text("Music").size(16)
                .visible( false ).build();

        this.entityList.add(musicianCreditsLabel);

        // Add them all to the layer manager.
        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }
    }

    @Override
    public IAnimation animateShow()
    {
        this.designer1 = (designer1 + 1) % 2;
        this.designer2 = (designer2 + 1) % 2;

        this.designer1Label.setText( this.designers[designer1] );
        this.designer1CreditsLabel.setText( this.designerCredits[designer1] );

        this.designer2Label.setText( this.designers[designer2] );
        this.designer2CreditsLabel.setText( this.designerCredits[designer2] );

        return super.animateShow();
    }    

}

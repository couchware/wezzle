/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.util.NumUtil;
import java.awt.Color;
import java.util.EnumSet;

/**
 * The high score menu, which holds all the high scores for the user to peruse
 * at his/her leisureee... so... leisurely.
 * 
 * @author cdmckay
 */
public class NotAvailableMenu extends AbstractMenu
{   
    private final static String[] NOT_AVAILABLE_TEXT = new String[]
    {
        "You need to buy Wezzle",
        "to use this feature."
    };   

    public NotAvailableMenu(IMenu parent, String title,
            IWindow win, ManagerHub hub, LayerManager menuLayerMan)
    {
        // Invoke super.
        super(parent, win, hub, menuLayerMan);

        // The colors.
        final Color LABEL_COLOR  = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);
        final Color OPTION_COLOR = hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY);

        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR)
                .text(title).size(20)
                .visible( false ).build();

        this.entityList.add(titleLabel);               

        // The box.
        Box optionBox = new Box.Builder(win, 68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .build();

        this.entityList.add(optionBox);

       // Create the no high score label.
        ITextLabel[] notAvailableText = new ITextLabel[2];

        notAvailableText[0] = new LabelBuilder(268, 306)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text(NOT_AVAILABLE_TEXT[0])
                .visible(false).build();

        notAvailableText[1] = new LabelBuilder(268, 336)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text(NOT_AVAILABLE_TEXT[1])
                .visible(false).build();

        for (ITextLabel label : notAvailableText)
        {
            this.entityList.add(label);          
        }

        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }
    }
    
}

/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.Box;
import java.awt.Color;
import java.util.EnumSet;

/**
 * The buy now group shows a dialog convincing the user to buy Wezzle.
 * 
 * @author cdmckay
 */
public class BuyNowMenu extends AbstractMenu
{         
    /**
     * The constructor.
     * @param layerMan
     */    
    public BuyNowMenu(IMenu parent, IWindow win, ManagerHub hub, LayerManager menuLayerMan)
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
                .text("Buy Now").size(20)
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
        
        final int labelStartY = 185;
        final int labelSpacing = 35;
        final int fontSize = 20;
        final int pointX = 130;

        ITextLabel label1 = new LabelBuilder(266, labelStartY)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(LABEL_COLOR)
                .size(fontSize)
                .visible(false)
                .text("With the full version of")
                .build();

        this.entityList.add(label1);

        ITextLabel label2 = new LabelBuilder(266, label1.getY() + labelSpacing)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(LABEL_COLOR)
                .size(fontSize)
                .visible(false)
                .text("Wezzle, you get:")
                .build();

        this.entityList.add(label2);
                
        ITextLabel label3 =  new LabelBuilder(pointX, label2.getY() + 60)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(OPTION_COLOR)
                .size(fontSize)
                .visible(false)
                .text("-Four unique items")
                .build();

        this.entityList.add(label3);

        ITextLabel label4 =  new LabelBuilder(pointX, label3.getY() + labelSpacing)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(OPTION_COLOR)
                .size(fontSize)
                .visible(false)
                .text("-Three difficulty modes")
                .build();

        this.entityList.add(label4);

        ITextLabel label5 =  new LabelBuilder(pointX, label4.getY() + labelSpacing)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(OPTION_COLOR)
                .size(fontSize)
                .visible(false)
                .text("-10 exclusive songs")
                .build();

        this.entityList.add(label5);

        ITextLabel label6 =  new LabelBuilder(pointX, label5.getY() + labelSpacing)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(OPTION_COLOR)
                .size(fontSize)
                .visible(false)
                .text("-Unlimited levels")
                .build();

        this.entityList.add(label6);

        ITextLabel label7 =  new LabelBuilder(pointX, label6.getY() + labelSpacing)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(OPTION_COLOR)
                .size(fontSize)
                .visible(false)
                .text("-Fullscreen mode")
                .build();

        this.entityList.add(label7);

        ITextLabel label8 =  new LabelBuilder(pointX, label7.getY() + labelSpacing)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(OPTION_COLOR)
                .size(fontSize)
                .visible(false)
                .text("-Over 80 achievements")
                .build();

        this.entityList.add(label8);
        
        // Add them all to the layer manager.
        for (IEntity e : this.entityList)
        {
            this.menuLayerMan.add(e, Layer.UI);
        }
    }                    
};

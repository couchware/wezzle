/*
 * Wezzle
 * Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.tile.RocketTile;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.tile.TileHelper;
import ca.couchware.wezzle2d.tile.TileType;
import java.util.EnumSet;

/**
 *
 * @author Cameron McKay
 */
public class TileNotification extends AbstractNotification
{

    /** The tile type. */
    final private TileType tileType;
    
    /** The notification title. */
    final private ITextLabel title;

    /** The tile graphic. */
    final private Tile tile;
   
    private TileNotification(Builder builder)
    {
        super(builder.window, builder.x, builder.y,
                builder.opacity, builder.visible, builder.alignment);        

        // Save the reference.
        this.tileType = builder.tileType;

        // Create the title text.
        this.title = new ResourceFactory.LabelBuilder(
                    this.x + offsetX + this.width / 2,
                    this.y + offsetY + 30)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .text("New Item Available")
                .size(16)
                .build();

        // Get a new tile of that type.
        this.tile = TileHelper.makeTile(tileType, TileColor.BLACK,
                this.x + offsetX, this.y + offsetY);
        //this.tile.setOpacity(70);
        this.tile.translate(
                this.width / 2 - this.tile.getWidth() / 2,
                this.height / 2 - this.tile.getHeight() / 2 + 16);

        if (this.tile.getType() == TileType.ROCKET)
        {
            RocketTile rocket = (RocketTile) this.tile;
            rocket.setDirection(RocketTile.Direction.UP);
        }

//        Tile tileL = TileFactory.cloneTile(tile);
//        tileL.translate(-60, 0);
//        //tileL.setOpacity(40);
//
//        Tile tileR = TileFactory.cloneTile(tile);
//        tileR.translate(60, 0);
//        //tileR.setOpacity(100);
            
        this.entityList.add(this.tile);
//        this.entityList.add(tileL);
//        this.entityList.add(tileR);
        this.entityList.add(this.title);
    }

    public static class Builder implements IBuilder<TileNotification>
    {
        // Required values.
        private final IWindow window;
        private final TileType tileType;
        private int x;
        private int y;

        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        private int opacity = 100;
        private boolean visible = true;

        public Builder(int x, int y, TileType tileType)
        {
            this.window = ResourceFactory.get().getWindow();
            this.tileType = tileType;
            this.x = x;
            this.y = y;
        }

        public Builder(TileNotification notif)
        {
            this.window = notif.window;
            this.tileType = notif.tileType;
            this.x = notif.x;
            this.y = notif.y;
            this.alignment = notif.alignment.clone();
            this.opacity = notif.opacity;
            this.visible = notif.visible;
        }

        public Builder x(int val) { x = val; return this; }
        public Builder y(int val) { y = val; return this; }

        public Builder alignment(EnumSet<Alignment> val)
        { alignment = val; return this; }

        public Builder opacity(int val)
        { opacity = val; return this; }

        public Builder visible(boolean val)
        { visible = val; return this; }

        public TileNotification build()
        {
            TileNotification notif = new TileNotification(this);
            return notif;
        }
    }

}

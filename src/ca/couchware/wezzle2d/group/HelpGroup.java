/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.tile.TileHelper;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.Padding;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.util.EnumSet;
import java.util.List;

/**
 * This group implements the help function, a dialog that shows the
 * user how to play Wezzle.
 *
 * @author cdmckay
 */
public class HelpGroup extends AbstractGroup
{
    private ManagerHub hub;
    private Box groupBox;

    public HelpGroup(ManagerHub hub)
    {
        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");
        
        this.hub = hub;
        this.groupBox = createGroupBox();
        this.entityList.add( this.groupBox );

        createLineAnimation(this.entityList);

         // Add all entities to the layer manager.
        for (IEntity entity : entityList)
        {
            entity.setVisible(false);
            hub.layerMan.add(entity, Layer.UI);
        }
    }

    private static ImmutableRectangle GroupBoxRect =
            new ImmutableRectangle(
                Game.SCREEN_WIDTH / 2 - 300,
                Game.SCREEN_HEIGHT / 2 - 200,
                600, 400);

    private static Padding quadrantPad = Padding.newInstance( 20 );
    private static Padding tilePad = Padding.newInstance( 5 );

    /**
     * Creates the group box that surrounds the help dialog.
     * @return
     */
    private Box createGroupBox()
    {
        Box box = new Box.Builder( 
                    GroupBoxRect.getCenterX(),
                    GroupBoxRect.getCenterY())
                .alignment( EnumSet.of( Alignment.MIDDLE, Alignment.CENTER) )
                .width(  GroupBoxRect.getWidth()  )
                .height( GroupBoxRect.getHeight() )                
                .build();

        return box;
    }

    private final static ImmutableRectangle LineRect =
            new ImmutableRectangle(
                GroupBoxRect.getX() + quadrantPad.getLeft(),
                GroupBoxRect.getY() + quadrantPad.getTop(),
                GroupBoxRect.getWidth()  / 2 - quadrantPad.getLeft() - quadrantPad.getRight(),
                GroupBoxRect.getHeight() / 2 - quadrantPad.getTop()  - quadrantPad.getBottom());

    private final static int LineColumns = 3;
    private final static int LineRows    = 2;
    
    // (0,0) is the top-left tile and 
    // (2, 1) is the bottom-right.
    private Tile[][] lineTileGrid = new Tile[LineColumns][LineRows];

    private void createLineAnimationEntites(List<IEntity> entityList)
    {
        this.lineTileGrid[0][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE,
                LineRect.getX(), LineRect.getY());

        this.lineTileGrid[1][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toRightOf( this.lineTileGrid[0][0], this.lineTileGrid[1][0], 0);

        this.lineTileGrid[2][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toRightOf( this.lineTileGrid[1][0], this.lineTileGrid[2][0], 0);

        this.lineTileGrid[0][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toBottomOf( this.lineTileGrid[0][0], this.lineTileGrid[0][1], 0);

        this.lineTileGrid[1][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE);
        TileHelper.toRightOf( this.lineTileGrid[0][1], this.lineTileGrid[1][1], 0);

        this.lineTileGrid[2][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE);
        TileHelper.toRightOf( this.lineTileGrid[1][1], this.lineTileGrid[2][1], 0);

        for (int i = 0; i < LineRows; i++)
            for (int j = 0; j < LineColumns; j++)
                entityList.add( lineTileGrid[j][i] );
    }

    private IAnimation createLineAnimation(List<IEntity> entityList)
    {
        createLineAnimationEntites(entityList);

        return null;
    }



    @Override
    public void setActivated(final boolean activated)
    {
        if (activated)
        {
            hub.musicMan.fadeToGain(0.05);
        }
        else
        {
            int intGain = hub.settingsMan.getInt(Key.USER_MUSIC_VOLUME);
            double gain = (double) intGain / 100.0;
            hub.musicMan.fadeToGain(gain);
        }

        // Invoke super.
        super.setActivated(activated);
    }

    public void updateLogic(Game game, ManagerHub hub)
    {
        
    }

}

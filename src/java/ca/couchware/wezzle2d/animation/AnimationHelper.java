/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.MetaAnimation.FinishRule;
import ca.couchware.wezzle2d.animation.MetaAnimation.RunRule;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.tile.RocketTile;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileHelper;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import java.awt.Color;
import java.util.EnumSet;

/**
 * This class provides commonly used composite animations.
 *
 * @author cdmckay
 */
public class AnimationHelper
{

    public static  IAnimation animateLevelUp(final ManagerHub hub, int index)
    {
        final int column = hub.boardMan.asColumn(index);
        final int angle = column >= hub.boardMan.getColumns() / 2 ? 0 : 180;
        final int wait = column >= hub.boardMan.getColumns() / 2
                ? (hub.boardMan.getColumns() - 1 - column) * 100
                : column * 100;

        final Tile tile = hub.boardMan.getTile(index);

        IAnimation move = new MoveAnimation.Builder(tile)
                .wait(wait)
                .duration(hub.settingsMan.getInt(Key.ANIMATION_LEVEL_MOVE_DURATION))
                .theta(angle).speed(hub.settingsMan.getInt(Key.ANIMATION_LEVEL_MOVE_SPEED))
                .gravity(hub.settingsMan.getInt(Key.ANIMATION_LEVEL_MOVE_GRAVITY))
                .build();

        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, tile)
                .wait(wait)
                .duration(hub.settingsMan.getInt(Key.ANIMATION_LEVEL_FADE_DURATION))
                .build();

        IAnimation meta = new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL)
                .add(move).add(fade).build();
        
        return meta;
    }

    public static IAnimation animateRemove(final ManagerHub hub, final Tile t)
    {
        int minOpacity    = hub.settingsMan.getInt( Key.ANIMATION_LINE_REMOVE_PULSE_MIN_OPACITY );
        int cycleDuration = hub.settingsMan.getInt( Key.ANIMATION_LINE_REMOVE_PULSE_CYCLE_DURATION );
        int cycleCount    = hub.settingsMan.getInt( Key.ANIMATION_LINE_REMOVE_PULSE_CYCLE_COUNT );
       
        FadeAnimation.Builder pulseOutBuilder = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, t )
                .duration( cycleDuration / 2 )
                .minOpacity( minOpacity );

        FadeAnimation.Builder pulseInBuilder = new FadeAnimation
                    .Builder( FadeAnimation.Type.IN, t )
                    .duration( cycleDuration / 2 )
                    .minOpacity( minOpacity );

        MetaAnimation.Builder animationBuilder = new MetaAnimation.Builder()
                .runRule( RunRule.SEQUENCE )
                .finishRule( FinishRule.ALL );

        for (int i = 0; i < cycleCount; i++)
        {
            animationBuilder
                    .add( pulseOutBuilder.build() )
                    .add( pulseInBuilder.build() );
        }    

        IAnimation zoom = new ZoomAnimation.Builder(ZoomAnimation.Type.IN, t)
                .lateInitialization( true )
                .speed(hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_ZOOM_SPEED))
                .build();

        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_FADE_DURATION))
                .build();

        IAnimation zoomFade = new MetaAnimation.Builder()                
                .add( zoom )
                .add( fade )
                .runRule( RunRule.SIMULTANEOUS )
                .finishRule( FinishRule.ALL )
                .build();
        
        animationBuilder.add( zoomFade );
                                
        return animationBuilder.build();
    }

    public static IAnimation animateItemSct(
            final ManagerHub hub,
            final ImmutablePosition pos,
            final int deltaScore,
            final Color color)
    {
        final ITextLabel label = new LabelBuilder(pos.getX(), pos.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(color)
                .size(hub.scoreMan.determineFontSize(deltaScore))
                .text(String.valueOf(deltaScore))
                .build();

        IAnimation move = new MoveAnimation.Builder(label)
                .duration(hub.settingsMan.getInt(Key.SCT_SCORE_MOVE_DURATION))
                .speed(hub.settingsMan.getInt(Key.SCT_SCORE_MOVE_SPEED))
                .theta(hub.settingsMan.getInt(Key.SCT_SCORE_MOVE_THETA))
                .build();

        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label)
                .wait(hub.settingsMan.getInt(Key.SCT_SCORE_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.SCT_SCORE_FADE_DURATION))
                .minOpacity(hub.settingsMan.getInt(Key.SCT_SCORE_FADE_MIN_OPACITY))
                .maxOpacity(hub.settingsMan.getInt(Key.SCT_SCORE_FADE_MAX_OPACITY))
                .build();
        
        move.addAnimationListener(new AnimationAdapter()
        {
            @Override
            public void animationStarted()
            {
                hub.layerMan.add(label, Layer.EFFECT);
            }

            @Override
            public void animationFinished()
            {
                hub.layerMan.remove(label, Layer.EFFECT);
            }
        });

        return new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL)
                .add(move).add(fade).build();
    }

    /**
     * Create the item activation animation.
     *
     * @param hub
     * @param tile
     * @param layer The layer to place the activation graphics.
     * @return
     */
    public static  IAnimation animateItemActivation(
            final ManagerHub hub, final Tile tile, final Layer layer)
    {
        // Sanity check.
        if (hub == null)
            throw new IllegalArgumentException("Hub cannot be null");

        if (tile == null)
            throw new IllegalArgumentException("Tile cannot be null");

        // The clone of tile, used to make the effect.
        final Tile clone = TileHelper.cloneTile(tile);
        
        // Make the animation.
        IAnimation zoom = new ZoomAnimation.Builder(ZoomAnimation.Type.OUT, clone)
                .minWidth(clone.getWidth())
                .maxWidth(Integer.MAX_VALUE)
                .speed(hub.settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_ZOOM_SPEED))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_ZOOM_DURATION))
                .build();

        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, clone)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_FADE_DURATION))
                .build();

        MetaAnimation meta = new MetaAnimation.Builder()
                .add(zoom)
                .add(fade)
                .build();

        meta.addAnimationListener( new AnimationAdapter()
        {

            @Override
            public void animationStarted()
            {
                hub.layerMan.add(clone, layer);
            }

            @Override
            public void animationFinished()
            {
                if (hub.layerMan.contains(clone, layer))
                    hub.layerMan.remove(clone, layer);
            }

        });

        clone.setAnimation(meta);

        return meta;
    }

    public static  IAnimation animateJump(final ManagerHub hub, final Tile tile, int angle)
    {
        IAnimation move = new MoveAnimation.Builder(tile)
                .duration(hub.settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_DURATION))
                .theta(angle)
                .speed(hub.settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_SPEED))
                .gravity(hub.settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_GRAVITY))
                .build();

        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, tile)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_JUMP_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_DURATION))
                .build();

        return new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL)
                .add(move).add(fade).build();
    }

    public static  IAnimation animateRocket(final ManagerHub hub, final RocketTile rocketTile)
    {
        IAnimation move = new MoveAnimation.Builder(rocketTile)
                .duration(hub.settingsMan.getInt(Key.ANIMATION_ROCKET_MOVE_DURATION))
                .theta(rocketTile.getDirection().toDegrees())
                .speed(hub.settingsMan.getInt(Key.ANIMATION_ROCKET_MOVE_SPEED))
                .gravity(hub.settingsMan.getInt(Key.ANIMATION_ROCKET_MOVE_GRAVITY))
                .build();

        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, rocketTile)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_ROCKET_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_ROCKET_FADE_DURATION))
                .build();

        MetaAnimation meta = new MetaAnimation.Builder()
                .finishRule(FinishRule.ALL)
                .add(move).add(fade).build();

        return meta;
    }

    public static IAnimation animateRocketJump(final ManagerHub hub, final Tile tile, int angle)
    {
        return animateJump(hub, tile, angle);
    }

    public static IAnimation animateExplosion(final ManagerHub hub, final Tile t)
    {
        final GraphicEntity explosion = new GraphicEntity.Builder(
                t.getCenterX() - 1, t.getCenterY() - 1,
                Settings.getSpriteResourcesPath() + "/Explosion.png")
                .build();

        explosion.setWidth(2);
        explosion.setHeight(2);

        // Add the clone to the layer man.
        hub.layerMan.add(explosion, Layer.EFFECT);

        // Make the animation.
        IAnimation boomZoom = new ZoomAnimation.Builder(ZoomAnimation.Type.OUT, explosion)
                .minWidth(2).maxWidth(Integer.MAX_VALUE)
                .speed(hub.settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_ZOOM_SPEED))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_ZOOM_DURATION))
                .build();

        IAnimation boomFade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, explosion)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_FADE_DURATION))
                .build();

        IAnimation tileFade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_BOMB_TILE_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_BOMB_TILE_FADE_DURATION))
                .build();

        MetaAnimation meta = new MetaAnimation.Builder()
                .add(boomZoom).add(boomFade).add(tileFade).build();

        meta.addAnimationListener(new AnimationAdapter()
        {
            @Override
            public void animationFinished()
            {
                hub.layerMan.remove(explosion, Layer.EFFECT);
            }
        });

        return meta;
    }

    public static IAnimation animateShrapnel(final ManagerHub hub, final Tile tile, int index, int bombIndex)
    {
        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, tile)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_FADE_DURATION))
                .build();

        int h = hub.boardMan.relativeColumnPosition(index, bombIndex).asInteger();
        int v = hub.boardMan.relativeRowPosition(index, bombIndex).asInteger() * -1;
        int theta = 0;

        if (h == 0)
        {
            theta = 90 * v;
        }
        else if (v == 0)
        {
            theta = h == 1 ? 0 : 180;
        }
        else
        {
            theta = (int) Math.toDegrees(Math.atan(h / v));
            if (h == -1)
            {
                theta -= 180;
            }
        }

        tile.setRotationAnchor(tile.getWidth() / 2, tile.getHeight() / 2);
        IAnimation move = new MoveAnimation.Builder(tile)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_DURATION))
                .speed(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_SPEED))
                .gravity(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_GRAVITY))
                .theta(theta)
                .omega(hub.settingsMan.getDouble(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_OMEGA))
                .build();

        IAnimation meta = new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL)
                .add(fade).add(move).build();

        return meta;
    }

}

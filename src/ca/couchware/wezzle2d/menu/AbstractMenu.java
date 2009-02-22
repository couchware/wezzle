package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.animation.AnimationAdapter;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;
import java.util.EnumSet;

/**
 * An abstract class to aide in creating menus.
 *
 * @author Cameron McKay
 */
public abstract class AbstractMenu extends AbstractGroup
{

    /** The manager hub. */
    private final ManagerHub hub;
    
    /** The layer manager used by the menu. */
    protected LayerManager menuLayerMan;

    /** The background window that encloses the menu. */
    protected Box menuBox;

    public AbstractMenu(IMenu parentMenu, ManagerHub hub, LayerManager menuLayerMan)
    {
        // Run the group constructor.
        super(parentMenu);

        // Sanity checks and assigments.
        if(hub == null || menuLayerMan == null)
        {
           throw new IllegalArgumentException("hub and menuLayerMan must not be null.");
        }

        this.hub = hub;
        this.menuLayerMan = menuLayerMan;

        // Create the menu box.        
        this.menuBox = new Box.Builder(268, 300).width(430).height(470)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_OPACITY))
                .visible(false).end();
        this.menuLayerMan.add(this.menuBox, Layer.UI);
    }

    /**
     * The menu logic updater.
     * @param game
     * @param hub
     */
    public abstract void updateLogic(Game game, ManagerHub hub);

    @Override
    public IAnimation animateShow()
    {
        menuBox.setPosition(268, -300);
        menuBox.setVisible(true);

        IAnimation anim = new MoveAnimation.Builder(menuBox).theta(-90).maxY(300)
                .speed(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_SPEED))
                .end();

        anim.addAnimationListener(new AnimationAdapter()
        {
            @Override
            public void animationFinished()
            { setVisible(true); }
        });

        return anim;
    }

    @Override
    public IAnimation animateHide()
    {
        IAnimation anim = new MoveAnimation.Builder(menuBox).theta(-90)
                .maxY(Game.SCREEN_HEIGHT + 300)
                .speed(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_SPEED))
                .end();

        anim.addAnimationListener(new AnimationAdapter()
        {
            @Override
            public void animationStarted()
            { setVisible(false); }
        });

        return anim;
    }

}

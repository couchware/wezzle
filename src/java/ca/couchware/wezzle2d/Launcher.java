/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.dialog.AgreementDialog;
import ca.couchware.wezzle2d.dialog.LicenseDialog;
import ca.couchware.wezzle2d.dialog.TrialLauncherDialog;
import ca.couchware.wezzle2d.manager.Achievement;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.ProgressBar;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.SpeechBubble;
import ca.couchware.wezzle2d.util.CouchLogger;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

/**
 * Launches the Wezzle applet or app, depending on how it is called.
 *
 * @author kgrad
 * @author cdmckay
 */
public class Launcher extends Applet
{
    private Game game;
    private Canvas displayParent;
    private Thread thread;

    public void initThread()
    {
        if (thread != null) return;

        CouchLogger.get().recordMessage(getClass(), "Init thread");

        thread = new Thread()
        {
            @Override
            public void run()
            {
                startWezzle(displayParent);
            }
        };

        thread.start();
    }

    public void destroyThread()
    {
        CouchLogger.get().recordMessage(getClass(), "Destroy thread");

        stopWezzle();

        try
        {
            thread.join();
            CouchLogger.get().recordMessage(getClass(), "Thread joined");
        }
        catch (InterruptedException e)
        {
            CouchLogger.get().recordException(getClass(), e, true /* Fatal */);
        }
    }

    @Override
    public void init()
    {
        CouchLogger.get().recordMessage(getClass(), "Applet init");

        removeAll();
        setLayout(new BorderLayout());
        setIgnoreRepaint(true);

        try
        {            
            displayParent = new Canvas()
            {
                @Override
                public final void addNotify()
                {
                    super.addNotify();
                    initThread();
                }

                @Override
                public final void removeNotify()
                {
                    destroyThread();
                    super.removeNotify();
                }
            };

            displayParent.setSize(getWidth(), getHeight());
            add(displayParent);
            displayParent.setFocusable(true);
            displayParent.requestFocus();
            displayParent.setIgnoreRepaint(true);
            setVisible(true);            
        }
        catch (Exception e)
        {
            CouchLogger.get().recordException(this.getClass(), e, true /* Fatal */);            
        }
    }

    @Override
    public void destroy()
    {
        CouchLogger.get().recordMessage(this.getClass(), "Applet destroy started");

        if (displayParent != null)
        {
            remove(displayParent);
        }

        super.destroy();
        CouchLogger.get().recordMessage(this.getClass(), "Applet destroy completed");
    }
    
    public void startWezzle(Canvas parent)
    {
        // Make sure the setting manager is loaded.
        SettingsManager settingsMan = SettingsManager.get();

        // Send a reference to the resource manager.
        ResourceFactory.get().setSettingsManager(settingsMan);

        // Set the default color scheme.
        ResourceFactory.setDefaultLabelColor(settingsMan.getColor(Key.GAME_COLOR_PRIMARY));
        ProgressBar.setDefaultColor(settingsMan.getColor(Key.GAME_COLOR_PRIMARY));
        RadioItem.setDefaultColor(settingsMan.getColor(Key.GAME_COLOR_PRIMARY));
        SpeechBubble.setDefaultColor(settingsMan.getColor(Key.GAME_COLOR_PRIMARY));
        Button.setDefaultColor(settingsMan.getColor(Key.GAME_COLOR_PRIMARY));
        Achievement.Level.initializeAchievementColorMap(settingsMan);

        // Set up log level.
        CouchLogger.get().setLogLevel(settingsMan.getString(Key.DEBUG_LOG_LEVEL));

        try
        {
            final String serialNumber = settingsMan.getString(Key.USER_SERIAL_NUMBER);
            final String licenseKey = settingsMan.getString(Key.USER_LICENSE_KEY);
            final boolean validated = Game.validateLicenseInformation(serialNumber, licenseKey);

            if (!validated)
            {
                LicenseDialog.run();

                final String enteredSerialNumber = settingsMan.getString(Key.USER_SERIAL_NUMBER);
                final String enteredLicenseKey = settingsMan.getString(Key.USER_LICENSE_KEY);
                final boolean enteredValidated =
                        Game.validateLicenseInformation(enteredSerialNumber, enteredLicenseKey);

                if (enteredValidated)
                {
                    CouchLogger.get().recordMessage( Game.class,
                            "License information verified");
                }
                else
                {
                    CouchLogger.get().recordException( Game.class,
                            new Exception("Invalid license information"),
                            true /* Fatal */);
                }
            }            

            final boolean allowed = TrialLauncherDialog.run();
            if (allowed)
            {
                game = new Game(parent, ResourceFactory.Renderer.LWJGL);
                game.start();
            }
        }
        catch (Throwable t)
        {
            CouchLogger.get().recordException(Game.class, t);
        }        
    }

    public void stopWezzle()
    {
        game.stop();
    }

    /**
     * The entry point into the game. We'll simply create an instance of class
     * which will start the display and game loop.
     *
     * @param argv
     *            The arguments that are passed into our game
     */
    public static void main(String argv[])
    {
        Launcher launcher = new Launcher();
        launcher.startWezzle(null);
//        System.exit(0);
    }
}

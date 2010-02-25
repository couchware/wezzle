/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.dialog.*;
import ca.couchware.wezzle2d.manager.*;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.*;
import ca.couchware.wezzle2d.util.CouchLogger;
import java.applet.Applet;

/**
 *
 * @author kgrad
 */
public class Launcher extends Applet
{


    //--------------------------------------------------------------------------
    // Main method
    //--------------------------------------------------------------------------

    @Override
    public void init()
    {
        start();
    }

    @Override
    public void start()
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


            Game game = new Game(ResourceFactory.Renderer.LWJGL);

            this.add(game);
            game.start();


            // THis is to make sure everything is dead.
            //System.exit(0);
        }
        catch (Exception e)
        {
            CouchLogger.get().recordException(Game.class, e);
        }
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
        Launcher l = new Launcher();
        l.init();
    }
}

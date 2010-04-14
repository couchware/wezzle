/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.dialog;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.Trial;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.PartialMaskFormatter;
import edu.stanford.ejalbert.BrowserLauncher;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.ParseException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

/**
 * A launcher dialog ...
 *
 * @author cdmckay
 */
public class TrialLauncherDialog extends JFrame
{
    private static final int SERIAL_NUMBER_MIN = 8;
    private static final int LICENSE_KEY_MIN = 32;

    final private static String FONT_PATH =
            Settings.getFontResourcesPath() + "/" + "Bubbleboy-2.ttf";

    final private static String MAIN_PANE_BACKGROUND =
            Settings.getSpriteResourcesPath() + "/" + "TrialLauncherBackground.png";

    final private static String PLAY_NOW_PATH =
            Settings.getSpriteResourcesPath() + "/" + "TrialPlayNow.png";

    final private static String BUY_NOW_PATH =
            Settings.getSpriteResourcesPath() + "/" + "TrialBuyNow.png";

    final private static String LICENSE_PANE_BACKGROUND =
            Settings.getSpriteResourcesPath() + "/" + "TrialLauncherLicenseBackground.png";

    final private static String OK_PATH =
            Settings.getSpriteResourcesPath() + "/" + "TrialOK.png";

    final private static String CANCEL_PATH =
            Settings.getSpriteResourcesPath() + "/" + "TrialCancel.png";
    
    final private static String ICON_32_PATH =
            Settings.getResourcesPath() + "/" + "Icon_32x32.png";    

    final private static Color HEADER_COLOR = new Color(238, 46, 62);
    final private static Color TEXT_COLOR = new Color(51, 51, 51);

    private Font baseFont;

    final private JPanel mainPane;
    final private JPanel licensePane;

    private AtomicBoolean allowed = new AtomicBoolean(false);   

    public TrialLauncherDialog()
    {        
        try
        {
            final InputStream fontStream = TrialLauncherDialog.class.getClassLoader().getResourceAsStream(FONT_PATH);
            baseFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        }
        catch (Exception ex)
        {            
            CouchLogger.get().recordException(getClass(), ex, true);
        }

        mainPane = createMainPane();
        licensePane = createLicensePane();
        initComponents();

        try
        {
            URL icon32Url = TrialLauncherDialog.class.getClassLoader().getResource(ICON_32_PATH);
            setIconImage(ImageIO.read(icon32Url));
        }
        catch (IOException ex)
        {
            CouchLogger.get().recordException(this.getClass(), ex);
        }          
    }

    private void initComponents()
    {
        setTitle("Buy Wezzle");

        add(mainPane);
       
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createMainPane()
    {
        final JPanel pane = new JPanel();
        pane.setLayout(null);
        pane.setPreferredSize(new Dimension(640, 480));

        final int timeLeft = 60 - Trial.getTimePlayedInMinutes();
        final String timeNoun = timeLeft == 1 ? "Minute" : "Minutes";

        JLabel timeLeftLabel = new JLabel(String.format("%d %s of Free Gameplay Left", timeLeft, timeNoun));
        timeLeftLabel.setForeground(HEADER_COLOR);
        timeLeftLabel.setFont(baseFont.deriveFont(18f));
        timeLeftLabel.setHorizontalAlignment(JLabel.CENTER);
        timeLeftLabel.setBounds(207, 184, 387, 49);
        pane.add(timeLeftLabel);

        try
        {
            final InputStream playNowImageStream = TrialLauncherDialog.class
                    .getClassLoader()
                    .getResourceAsStream(PLAY_NOW_PATH);
            final Image playNowImage = ImageIO.read(playNowImageStream);
            final JLabel playNow = new JLabel(new ImageIcon(playNowImage));
            playNow.setBounds(207, 372, 181, 49);
            playNow.addMouseListener(new HandCursorMouseAdapter(playNow) {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    if (hasTrialExpired())
                    {
                        JOptionPane.showMessageDialog(
                            pane,
                            "We're sorry but your 60 minutes of free gameplay have been used up!\n" +
                            "Click the Buy Now button to buy Wezzle and keep playing.",
                            "Trial Expired",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        allowed.set(true);
                        closeWindow();
                    }
                }
            });
            pane.add(playNow);

            final InputStream buyNowImageStream = TrialLauncherDialog.class
                    .getClassLoader()
                    .getResourceAsStream(BUY_NOW_PATH);
            final Image buyNowImage = ImageIO.read(buyNowImageStream);
            final JLabel buyNow = new JLabel(new ImageIcon(buyNowImage));
            buyNow.setBounds(414, 372, 181, 49);
            buyNow.addMouseListener(new HandCursorMouseAdapter(buyNow) {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    final int result = JOptionPane.showConfirmDialog(pane,
                            "<html>" +
                            "You will now be sent to Couchware's VeriSign\u00AE secured order form.<br/>" +
                            "<br/>" +
                            "After you purchase of Wezzle, you will receive an e-mail with your<br/>" +
                            "serial number and license key.  Return to this window and follow the<br/>" +
                            "on-screen instructions to enter it and register Wezzle.<br/>" +
                            "</html>",
                            "Instructions",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);

                    if (result == JOptionPane.OK_OPTION)
                    {
                        changeToLicensePane();

                        try
                        {
                            BrowserLauncher launcher = new BrowserLauncher();
                            launcher.openURLinBrowser(Settings.getUpgradeUrl());
                        } catch (Exception ex)
                        {
                            CouchLogger.get().recordException(Game.class, ex);
                        }
                    }
                }
            });
            pane.add(buyNow);
        }
        catch (IOException ex)
        {
            CouchLogger.get().recordException(getClass(), ex, true);
        }       

        final JLabel enterLicense = new JLabel("<html><u>Already own it?</u></html>");
        enterLicense.setBounds(207, 430, 181, 25);
        enterLicense.setHorizontalAlignment(JLabel.CENTER);
        enterLicense.setFont(baseFont.deriveFont(14f));
        enterLicense.setForeground(TEXT_COLOR);
        enterLicense.addMouseListener(new HandCursorMouseAdapter(enterLicense) {
            @Override
            public void mouseClicked(MouseEvent e)
            {                
                changeToLicensePane();
            }
        });

        pane.add(enterLicense);

        JLabel security = new JLabel("<html><u>Security</u></html>");
        security.setBounds(414, 430, 181, 25);
        security.setHorizontalAlignment(JLabel.CENTER);
        security.setFont(baseFont.deriveFont(14f));
        security.setForeground(TEXT_COLOR);
        security.addMouseListener(new HandCursorMouseAdapter(security) {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                JOptionPane.showMessageDialog(pane,
                        "<html>" +
                        "When you order Wezzle from our VeriSign\u00AE secured order form,<br/>" +
                        "your credit card information is sent over a SSL-encrypted connection.<br/>" +
                        "<br/>" +
                        "When you buy Wezzle, you receive a serial number and license key by<br/>" +
                        "e-mail.  If you ever need to re-install or install on another computer<br/>" +
                        "you simply re-enter your license information.  There's no risk of ever<br/>" +
                        "losing your game!<br/>" +
                        "<br/>" +
                        "Couchware Inc. guarantees a refund if you are the victim of fraud or<br/>" +
                        "computer error, or within 30 days if you are dissatisfied with your<br/>" +
                        "purchase.<br/>" +
                        "</html>",
                        "Security",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        pane.add(security);

        try
        {
            final InputStream backgroundImageStream = TrialLauncherDialog.class
                    .getClassLoader()
                    .getResourceAsStream(MAIN_PANE_BACKGROUND);
            final Image backgroundImage = ImageIO.read(backgroundImageStream);
            final JLabel background = new JLabel(new ImageIcon(backgroundImage));
            background.setBounds(0, 0, 640, 480);
            pane.add(background);
        }
        catch (IOException ex)
        {
            CouchLogger.get().recordException(getClass(), ex, true);
        }
        
        return pane;
    }

    private JPanel createLicensePane()
    {
        final JPanel pane = new JPanel();
        pane.setLayout(null);
        pane.setPreferredSize(new Dimension(640, 480));       

        try
        {
            final MaskFormatter serialNumberFormatter =
                    new PartialMaskFormatter("HHHHHHHHHHHHHHHH");
            serialNumberFormatter.setValueContainsLiteralCharacters(false);

            final JFormattedTextField serialNumberField = new JFormattedTextField();
            serialNumberField.setFormatterFactory(
                    new DefaultFormatterFactory(serialNumberFormatter));
            serialNumberField.setBounds(173, 282, 424, 39);
            serialNumberField.setFont(serialNumberField.getFont().deriveFont(20f));            

            final MaskFormatter licenseKeyFormatter =
                    new PartialMaskFormatter("HHHHHHHH-HHHHHHHH-HHHHHHHH-HHHHHHHH");
            licenseKeyFormatter.setValueContainsLiteralCharacters(false);            

            final JFormattedTextField licenseKeyField = new JFormattedTextField();
            licenseKeyField.setFormatterFactory(
                    new DefaultFormatterFactory(licenseKeyFormatter));
            licenseKeyField.setBounds(173, 332, 424, 39);
            licenseKeyField.setFont(licenseKeyField.getFont().deriveFont(20f));

            pane.add(serialNumberField);
            pane.add(licenseKeyField);
       
            final InputStream okImageStream = TrialLauncherDialog.class
                    .getClassLoader()
                    .getResourceAsStream(OK_PATH);
            final Image okImage = ImageIO.read(okImageStream);
            final JLabel ok = new JLabel(new ImageIcon(okImage));
            ok.setBounds(191, 390, 181, 49);
            ok.addMouseListener(new HandCursorMouseAdapter(ok) {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    try
                    {
                        // This code is needed because the JLabel "buttons"
                        // don't take focus when clicked and thus don't force
                        // the formatted text fields to commit.
                        serialNumberField.commitEdit();
                        licenseKeyField.commitEdit();
                    }
                    catch (ParseException ex)
                    {
                        CouchLogger.get().recordException(getClass(), ex);
                    }

                    final boolean confirmed =
                            checkSerialNumberAndLicenseKey(pane, serialNumberField, licenseKeyField);

                    if (confirmed)
                    {
                        allowed.set(true);
                        closeWindow();
                    }
                }
            });
            pane.add(ok);

            final InputStream cancelImageStream = TrialLauncherDialog.class
                    .getClassLoader()
                    .getResourceAsStream(CANCEL_PATH);
            final Image cancelImage = ImageIO.read(cancelImageStream);
            final JLabel cancel = new JLabel(new ImageIcon(cancelImage));
            cancel.setBounds(398, 390, 181, 49);
            cancel.addMouseListener(new HandCursorMouseAdapter(cancel) {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    changeToMainPane();
                }
            });
            pane.add(cancel);
       
            final InputStream backgroundImageStream = TrialLauncherDialog.class
                    .getClassLoader()
                    .getResourceAsStream(LICENSE_PANE_BACKGROUND);
            final Image backgroundImage = ImageIO.read(backgroundImageStream);
            final JLabel background = new JLabel(new ImageIcon(backgroundImage));
            background.setBounds(0, 0, 640, 480);
            pane.add(background);
        }
        catch (Exception ex)
        {
            CouchLogger.get().recordException(getClass(), ex, true);
        }

        return pane;
    }

    public synchronized void changeToLicensePane()
    {
        remove(mainPane);
        add(licensePane);
        pack();
        repaint();
    }

    public synchronized void changeToMainPane()
    {
        remove(licensePane);
        add(mainPane);
        pack();
        repaint();
    }

    private boolean checkSerialNumberAndLicenseKey(
            JPanel pane,
            JFormattedTextField serialNumberField,
            JFormattedTextField licenseKeyField)
    {
        boolean problem = false;
        String title = "";
        String text = "";

        final String serialNumber = (String) serialNumberField.getValue();
        final String licenseKey = (String) licenseKeyField.getValue();

        if (serialNumber == null || serialNumber.length() < SERIAL_NUMBER_MIN)
        {
            problem = true;
            title = "Serial Number Problem";
            text = "Serial number must be between 8 and 16 characters in length.";
            serialNumberField.requestFocus();
        }
        else if (licenseKey == null || licenseKey.length() < LICENSE_KEY_MIN)
        {
            problem = true;
            title = "License Key Problem";
            text = "The license key must be 4 groups of 8 characters (32 characters total).";
            licenseKeyField.requestFocus();
        }

        if (!problem)
        {
            // Try to validate.
            if (!Game.validateLicenseInformation(serialNumber, licenseKey))
            {
                problem = true;
                title = "Invalid License Key";
                text = "The license key is not valid for the given serial number.";
            }
            else
            {
                SettingsManager.get().setString(Key.USER_SERIAL_NUMBER, serialNumber);
                SettingsManager.get().setString(Key.USER_LICENSE_KEY, licenseKey);                
            }
        }

        if (problem)
        {
            JOptionPane.showMessageDialog(
                    pane, text, title, JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        else
        {
            JOptionPane.showMessageDialog(pane, 
                    "Thank you for supporting Wezzle!", "Success!",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
    }

    private boolean hasTrialExpired()
    {
        return Trial.getTimePlayedInMinutes() >= 60;
    }

    public boolean isAllowed()
    {
        return allowed.get();
    }

    private void closeWindow()
    {
        processWindowEvent( new WindowEvent(this, WindowEvent.WINDOW_CLOSING) );
    }

    public static boolean run() throws InterruptedException, InvocationTargetException
    {
        final AtomicBoolean allowed = new AtomicBoolean(false);
        final CountDownLatch finished = new CountDownLatch(1);

        java.awt.EventQueue.invokeAndWait(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch (Exception e)
                {
                    CouchLogger.get().recordException(TrialLauncherDialog.class, e, true /* Fatal */);
                }

                //final JFrame frame = new JFrame();
                final TrialLauncherDialog dialog = new TrialLauncherDialog();

                //dialog.setLocationRelativeTo(null);
                dialog.addWindowListener(new WindowAdapter()
                {
                    @Override
                    public void windowClosing(WindowEvent e)
                    {
                        finished.countDown();
                        allowed.set(dialog.isAllowed());
                        dialog.setVisible(false);
                        dialog.dispose();
                    }
                });

                dialog.setVisible(true);
            }
        });

        finished.await();
        return allowed.get();
    }

    /**
     * A variation on the mouse adapter that automatically turns the
     * cursor into a hand when moused over.
     */
    private static class HandCursorMouseAdapter extends MouseAdapter
    {
        private final Component component;

        public HandCursorMouseAdapter(Component component)
        {
            this.component = component;
        }

        @Override
        public void mouseEntered(MouseEvent me)
        {
            component.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent me)
        {
            component.setCursor(Cursor.getDefaultCursor());
        }
    }
}

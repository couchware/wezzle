/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.dialog;

import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.CouchLogger;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.UIManager;

/**
 * A launcher dialog ...
 *
 * @author cdmckay
 */
public class TrialLauncherDialog extends JFrame
{
    final private static String FONT_PATH =
            Settings.getFontResourcesPath() + "/" + "Bubbleboy-2.ttf";

    final private static String BACKGROUND_PATH =
            Settings.getSpriteResourcesPath() + "/" + "TrialLauncherBackground.png";
    
    final private static String ICON_32_PATH =
            Settings.getResourcesPath() + "/" + "Icon_32x32.png";    

    final private static Color HEADER_COLOR = new Color(238, 46, 62);
    final private static Color TEXT_COLOR = new Color(51, 51, 51);

    private Font baseFont;

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

        JLayeredPane pane = new JLayeredPane();
        pane.setLayout(null);
        pane.setPreferredSize(new Dimension(640, 480));        
        add(pane);        

        JLabel label = new JLabel("59 Minutes of Free Gameplay Left");
        label.setForeground(HEADER_COLOR);
        label.setFont(baseFont.deriveFont(18f));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBounds(207, 180, 387, 49);
        pane.add(label);
        
        JButton playNow = new JButton("Play Now");
        //playNow.setFont(font20);
        playNow.setBounds(207, 374, 181, 49);
        pane.add(playNow);

        JButton buyNow = new JButton("Buy Now");        
        //buyNow.setFont(font20);
        buyNow.setBounds(414, 374, 181, 49);
        pane.add(buyNow);

        try
        {            
            final InputStream backgroundImageStream = TrialLauncherDialog.class.getClassLoader().getResourceAsStream(BACKGROUND_PATH);
            final Image backgroundImage = ImageIO.read(backgroundImageStream);
            final JLabel background = new JLabel(new ImageIcon(backgroundImage));            
            background.setBounds(0, 0, 640, 480);
            pane.add(background);
        }
        catch (IOException ex)
        {
            CouchLogger.get().recordException(getClass(), ex, true);
        }
       
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    public static void run() throws InterruptedException, InvocationTargetException
    {
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
                        dialog.setVisible(false);
                        dialog.dispose();
                    }
                });

                dialog.setVisible(true);
            }
        });

        finished.await();
    }
}

/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.dialog;

import java.text.ParseException;
import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.PartialMaskFormatter;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import org.jdesktop.layout.LayoutStyle;
import org.jdesktop.layout.GroupLayout;

/**
 * A standalone dialog for entering the serial number and license key.
 *
 * @author Cameron McKay
 */
public class LicenseDialog extends JFrame
{
    private static final int SERIAL_NUMBER_MIN = 8;
    private static final int LICENSE_KEY_MIN = 32;

    final private static String ICON_32_PATH =
            Settings.getResourcesPath() + "/" + "Icon_32x32.png";

    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel instructionsLabel;
    private JPanel licensePanel;
    private JLabel serialNumberLabel;
    private JFormattedTextField serialNumberField;
    private JLabel licenseKeyLabel;
    private JFormattedTextField licenseKeyField;
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;

    public LicenseDialog()
    {        
        initComponents();

        try
        {
            URL icon32Url = LicenseDialog.class.getClassLoader().getResource(ICON_32_PATH);
            setIconImage(ImageIO.read(icon32Url));
        }
        catch (IOException ex)
        {
            CouchLogger.get().recordException(this.getClass(), ex);
        }

        try
        {
            MaskFormatter serialNumberFormatter =
                    new PartialMaskFormatter("HHHHHHHHHHHHHHHH");
            serialNumberFormatter.setValueContainsLiteralCharacters(false);

            serialNumberField.setFormatterFactory(
                    new DefaultFormatterFactory(serialNumberFormatter));

            MaskFormatter licenseKeyFormatter =
                    new PartialMaskFormatter("HHHHHHHH-HHHHHHHH-HHHHHHHH-HHHHHHHH");
            licenseKeyFormatter.setValueContainsLiteralCharacters(false);

            licenseKeyField.setFormatterFactory(
                    new DefaultFormatterFactory(licenseKeyFormatter));
        }
        catch (ParseException ex)
        {
            CouchLogger.get().recordException(getClass(), ex, true);
        }

        okButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                okButtonMouseClicked(evt);
            }
        });

        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                cancelButtonMouseClicked(evt);
            }
        });
    }

    private void okButtonMouseClicked(java.awt.event.MouseEvent evt)
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
                processWindowEvent( new WindowEvent(this, WindowEvent.WINDOW_CLOSING) );
            }
        }

        if (problem)
        {
            JOptionPane.showMessageDialog(
                    rootPane, text, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt)
    {
        processWindowEvent( new WindowEvent(this, WindowEvent.WINDOW_CLOSING) );
    }

    public static void run() throws InterruptedException, InvocationTargetException
    {
        final CountDownLatch finished = new CountDownLatch(1);

        java.awt.EventQueue.invokeAndWait(new Runnable()
        {
            public void run()
            {
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch (Exception e)
                {
                    CouchLogger.get().recordException(LicenseDialog.class, e, true /* Fatal */);
                }
                
                final LicenseDialog dialog = new LicenseDialog();

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

    private void initComponents()
    {
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        instructionsLabel = new JLabel();
        licensePanel = new JPanel();
        serialNumberLabel = new JLabel();
        serialNumberField = new JFormattedTextField();
        licenseKeyLabel = new JLabel();
        licenseKeyField = new JFormattedTextField();
        buttonPanel = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        
        setTitle("Wezzle");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
               
        dialogPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialogPane.setLayout(new BorderLayout());

        instructionsLabel.setText("<html>Please enter your serial number and license key.<br/>\n<br/>\nRemember to enter your serial number and license key exactly as it appears in the e-mail you received from Couchware Inc.\n</html>");
        instructionsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        licensePanel.setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION));

        serialNumberLabel.setText("Serial Number");
        serialNumberField.setFont(new Font("monospaced", Font.PLAIN, 12));

        licenseKeyLabel.setText("License Key");
        licenseKeyField.setFont(new Font("monospaced", Font.PLAIN, 12));

        GroupLayout licensePanelLayout = new GroupLayout(licensePanel);
        licensePanel.setLayout(licensePanelLayout);
        licensePanelLayout.setHorizontalGroup(
                licensePanelLayout.createParallelGroup()
                .add(GroupLayout.TRAILING, licensePanelLayout.createSequentialGroup().addContainerGap()
                    .add(licensePanelLayout.createParallelGroup()
                        .add(licenseKeyLabel, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .add(serialNumberLabel, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(licensePanelLayout.createParallelGroup(GroupLayout.LEADING, false)
                            .add(GroupLayout.TRAILING, licenseKeyField)
                            .add(GroupLayout.TRAILING, serialNumberField, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
                        .addContainerGap()));
        
        licensePanelLayout.setVerticalGroup(
                licensePanelLayout.createParallelGroup()
                .add(licensePanelLayout.createSequentialGroup().addContainerGap()
                    .add(licensePanelLayout.createParallelGroup(GroupLayout.BASELINE)
                        .add(serialNumberField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .add(serialNumberLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.RELATED)
                    .add(licensePanelLayout.createParallelGroup(GroupLayout.BASELINE)
                        .add(licenseKeyField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .add(licenseKeyLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)).addContainerGap()));

        okButton.setText("OK");
        cancelButton.setText("Cancel");

        GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);

        buttonPanelLayout.setHorizontalGroup(
                buttonPanelLayout.createParallelGroup()
                    .add(GroupLayout.TRAILING, buttonPanelLayout.createSequentialGroup()
                        .addContainerGap(223, Short.MAX_VALUE)
                        .add(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.UNRELATED)
                        .add(cancelButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(9, 9)));

        buttonPanelLayout.setVerticalGroup(
                buttonPanelLayout.createParallelGroup().add(GroupLayout.TRAILING,
                buttonPanelLayout.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(buttonPanelLayout.createParallelGroup(GroupLayout.BASELINE)
                        .add(cancelButton).add(okButton))
                    .addContainerGap()));


        GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
                contentPanelLayout.createParallelGroup()
                    .add(licensePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(instructionsLabel, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE));

        contentPanelLayout.setVerticalGroup(
                contentPanelLayout.createParallelGroup()
                    .add(GroupLayout.TRAILING, contentPanelLayout.createSequentialGroup()
                        .add(instructionsLabel, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.UNRELATED)
                        .add(licensePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));

        dialogPane.add(contentPanel, BorderLayout.CENTER);
        contentPane.add(dialogPane, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
    }       
}

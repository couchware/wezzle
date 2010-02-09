/*
 * LicenseDialog.java
 *
 * Created on Feb 8, 2010, 12:15:09 AM
 */
package ca.couchware.wezzle2d.dialog;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.util.CouchLogger;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author cdmckay
 */
public class LicenseDialog extends javax.swing.JDialog
{

    private static final int SERIAL_NUMBER_MIN = 8;
    private static final int SERIAL_NUMBER_MAX = 16;    

    private static final int LICENSE_KEY_MIN = 8;
    private static final int LICENSE_KEY_MAX = 8;

    final private static String ICON_16_PATH = Settings.getResourcesPath() + "/" + "Icon_16x16.png";
    final private static String ICON_32_PATH = Settings.getResourcesPath() + "/" + "Icon_32x32.png";

    /** Creates new form LicenseDialog */
    public LicenseDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();

        try
        {
            URL icon16Url = LicenseDialog.class.getClassLoader().getResource(ICON_16_PATH);
            URL icon32Url = LicenseDialog.class.getClassLoader().getResource(ICON_32_PATH);
            List<Image> icons = new ArrayList<Image>();
            icons.add(ImageIO.read(icon16Url));
            icons.add(ImageIO.read(icon32Url));
            this.setIconImages(icons);
        }
        catch (IOException ex)
        {
            CouchLogger.get().recordException(this.getClass(), ex);
        }

        serialNumberField.setDocument(new HexadecimalDocument(SERIAL_NUMBER_MAX));
        licenseKeyField1.setDocument(new HexadecimalDocument(LICENSE_KEY_MAX));
        licenseKeyField2.setDocument(new HexadecimalDocument(LICENSE_KEY_MAX));
        licenseKeyField3.setDocument(new HexadecimalDocument(LICENSE_KEY_MAX));
        licenseKeyField4.setDocument(new HexadecimalDocument(LICENSE_KEY_MAX));
    }   

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        instructionsLabel = new javax.swing.JLabel();
        licensePanel = new javax.swing.JPanel();
        serialNumberLabel = new javax.swing.JLabel();
        licenseKeyLabel = new javax.swing.JLabel();
        licenseKeyField1 = new javax.swing.JTextField();
        licenseKeyField2 = new javax.swing.JTextField();
        licenseKeyField3 = new javax.swing.JTextField();
        licenseKeyField4 = new javax.swing.JTextField();
        serialNumberField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Wezzle");
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setIconImage(null);
        setMinimumSize(new java.awt.Dimension(432, 263));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 0, 10));

        okButton.setText("OK");
        okButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                okButtonMouseClicked(evt);
            }
        });

        cancelButton.setText("Exit");
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(224, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(okButton)
                .addComponent(cancelButton))
        );

        instructionsLabel.setText("<html>Please enter your serial number and license key.<br/><br/>Remember to enter your serial number and license key exactly as it appears in the e-mail you received from Couchware Inc.</html>");
        instructionsLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        instructionsLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        instructionsLabel.setPreferredSize(new java.awt.Dimension(300, 14));

        licensePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        serialNumberLabel.setText("Serial Number");

        licenseKeyLabel.setText("License Key");

        licenseKeyField1.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        licenseKeyField2.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        licenseKeyField3.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        licenseKeyField4.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        serialNumberField.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        javax.swing.GroupLayout licensePanelLayout = new javax.swing.GroupLayout(licensePanel);
        licensePanel.setLayout(licensePanelLayout);
        licensePanelLayout.setHorizontalGroup(
            licensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(licensePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(licensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(licenseKeyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(serialNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(licensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(licensePanelLayout.createSequentialGroup()
                        .addComponent(licenseKeyField1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(licenseKeyField2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(serialNumberField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(licenseKeyField3, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(licenseKeyField4, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        licensePanelLayout.setVerticalGroup(
            licensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(licensePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(licensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serialNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serialNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(licensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(licenseKeyField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(licenseKeyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(licenseKeyField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(licenseKeyField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(licenseKeyField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(instructionsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(licensePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(instructionsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addGap(11, 11, 11)
                .addComponent(licensePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_okButtonMouseClicked
    {//GEN-HEADEREND:event_okButtonMouseClicked
        boolean problem = false;
        String title = "";
        String text  = "";

        final String format = "The %s part of the license key must be 8 characters in length.";

        if (serialNumberField.getText().length() < SERIAL_NUMBER_MIN)
        {
            problem = true;
            title = "Serial Number Problem";
            text  = "Serial number must be between 8 and 16 characters in length.";
            serialNumberField.requestFocus();
        }
        else if (licenseKeyField1.getText().length() < LICENSE_KEY_MIN)
        {
            problem = true;
            title = "License Key Problem";
            text  = String.format(format, "first");
            licenseKeyField1.requestFocus();
        }
        else if (licenseKeyField2.getText().length() < LICENSE_KEY_MIN)
        {
            problem = true;
            title = "License Key Problem";
            text  = String.format(format, "second");
            licenseKeyField2.requestFocus();
        }
        else if (licenseKeyField3.getText().length() < LICENSE_KEY_MIN)
        {
            problem = true;
            title = "License Key Problem";
            text  = String.format(format, "third");
            licenseKeyField3.requestFocus();
        }
        else if (licenseKeyField4.getText().length() < LICENSE_KEY_MIN)
        {
            problem = true;
            title = "License Key Problem";
            text  = String.format(format, "fourth");
            licenseKeyField4.requestFocus();
        }

        if (!problem)
        {
            // Try to validate.
            final String serialNumber = serialNumberField.getText();
            final String licenseKey =
                    licenseKeyField1.getText() +
                    licenseKeyField2.getText() +
                    licenseKeyField3.getText() +
                    licenseKeyField4.getText();

            if (!Game.validateLicenseInformation(serialNumber, licenseKey))
            {
                problem = true;
                title = "Invalid License Key";
                text  = "The license key does not match this serial number.";
            }
            else
            {
                SettingsManager.get().setString(Key.USER_SERIAL_NUMBER, serialNumber);
                SettingsManager.get().setString(Key.USER_LICENSE_KEY, licenseKey);
                setVisible(false);
                dispose();
            }
        }

        if (problem)
        {
            JOptionPane.showMessageDialog(
                    rootPane, text, title, JOptionPane.INFORMATION_MESSAGE);
        }
}//GEN-LAST:event_okButtonMouseClicked

    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_cancelButtonMouseClicked
    {//GEN-HEADEREND:event_cancelButtonMouseClicked
        setVisible(false);
        dispose();
}//GEN-LAST:event_cancelButtonMouseClicked
    
    /**
     * @param args the command line arguments
     */
    public static void run() throws InterruptedException, InvocationTargetException
    {
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

                LicenseDialog dialog = new LicenseDialog(new javax.swing.JFrame(), true);

                dialog.setLocationRelativeTo(null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e)
                    {
                        
                    }
                });

                dialog.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel instructionsLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField licenseKeyField1;
    private javax.swing.JTextField licenseKeyField2;
    private javax.swing.JTextField licenseKeyField3;
    private javax.swing.JTextField licenseKeyField4;
    private javax.swing.JLabel licenseKeyLabel;
    private javax.swing.JPanel licensePanel;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField serialNumberField;
    private javax.swing.JLabel serialNumberLabel;
    // End of variables declaration//GEN-END:variables

}

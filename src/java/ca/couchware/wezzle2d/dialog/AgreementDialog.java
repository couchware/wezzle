package ca.couchware.wezzle2d.dialog;

import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.StringUtil;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/*
 * Created by JFormDesigner on Sat Feb 13 20:01:39 EST 2010
 */

/**
 * @author Cameron McKay
 */
public class AgreementDialog extends JDialog
{

    final private static String AGREEMENT_PATH = Settings.getResourcesPath() + "/" + "license.txt";
    final private static String ICON_16_PATH = Settings.getResourcesPath() + "/" + "Icon_16x16.png";
    final private static String ICON_32_PATH = Settings.getResourcesPath() + "/" + "Icon_32x32.png";

    public AgreementDialog(Frame owner)
    {
        super(owner, true);
        initComponents();

        try
        {
            URL icon16Url = AgreementDialog.class.getClassLoader().getResource(ICON_16_PATH);
            URL icon32Url = AgreementDialog.class.getClassLoader().getResource(ICON_32_PATH);
            java.util.List<Image> icons = new ArrayList<Image>();
            icons.add(ImageIO.read(icon16Url));
            icons.add(ImageIO.read(icon32Url));
            this.setIconImages(icons);

            // Load in the license.
            URL agreementUrl = AgreementDialog.class.getClassLoader().getResource(AGREEMENT_PATH);
            String agreementStr = StringUtil.readFileIntoString(agreementUrl);
            agreementArea.setText(agreementStr);
            agreementArea.setCaretPosition(0);
        }
        catch (IOException ex)
        {
            CouchLogger.get().recordException(this.getClass(), ex, true);
        }

        okButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                okButtonMouseClicked(evt);
            }
        });

        cancelButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                cancelButtonMouseClicked(evt);
            }
        });
    }

    private void okButtonMouseClicked(java.awt.event.MouseEvent evt)
    {
        SettingsManager.get().setBool(Settings.Key.USER_AGREEMENT_ACCEPTED, true);

        setVisible(false);
        dispose();
    }

    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt)
    {
        setVisible(false);
        dispose();
    }

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
                    CouchLogger.get().recordException(AgreementDialog.class, e, true /* Fatal */);
                }

                AgreementDialog dialog = new AgreementDialog(new javax.swing.JFrame());

                //dialog.setLocationRelativeTo(null);
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

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Cameron McKay
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        instructionsLabel = new JLabel();
        buttonPanel = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        scrollPane = new JScrollPane();
        agreementArea = new JTextArea();

        //======== this ========
        setTitle("Wezzle");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(20, 20, 20, 20));           
            dialogPane.setLayout(new BorderLayout());
            //======== contentPanel ========
            {

                //---- instructionsLabel ----
                instructionsLabel.setText("<html>Please review the license terms before playing Wezzle.<br/> <br/>If you accept the terms of the agreement, click I Agree.  You must accept the terms of the agreement to play Wezzle.</html>");
                instructionsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                //======== buttonPanel ========
                {

                    //---- okButton ----
                    okButton.setText("I Agree");

                    //---- cancelButton ----
                    cancelButton.setText("Cancel");

                    GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
                    buttonPanel.setLayout(buttonPanelLayout);
                    buttonPanelLayout.setHorizontalGroup(
                            buttonPanelLayout.createParallelGroup().addGroup(GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup().addContainerGap(223, Short.MAX_VALUE).addComponent(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE).addGap(9, 9, 9)));
                    buttonPanelLayout.setVerticalGroup(
                            buttonPanelLayout.createParallelGroup().addGroup(GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(cancelButton).addComponent(okButton)).addContainerGap()));
                }
                //======== scrollPane1 ========
                {
                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                    //---- textArea1 ----
                    agreementArea.setEditable(false);
                    agreementArea.setLineWrap(true);
                    agreementArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
                    agreementArea.setWrapStyleWord(true);
                    scrollPane.setViewportView(agreementArea);
                }

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup().addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(instructionsLabel, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE).addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 402, GroupLayout.PREFERRED_SIZE));
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup().addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup().addComponent(instructionsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(18, 18, 18).addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
    
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Cameron McKay
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel instructionsLabel;
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;
    private JScrollPane scrollPane;
    private JTextArea agreementArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

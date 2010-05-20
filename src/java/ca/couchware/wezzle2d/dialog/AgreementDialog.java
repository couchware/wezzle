/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.dialog;

import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.StringUtil;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import org.jdesktop.layout.LayoutStyle;
import org.jdesktop.layout.GroupLayout;

/**
 * Displays the Wezzle EULA click-through and sets a setting if it is approved.
 *
 * @author Cameron McKay
 */
public class AgreementDialog extends JFrame
{
    final private static String AGREEMENT_PATH = Settings.getResourcesPath() + "/" + "license.txt";
    final private static String ICON_32_PATH = Settings.getResourcesPath() + "/" + "Icon_32x32.png";

    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel instructionsLabel;
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;
    private JScrollPane scrollPane;
    private JTextArea agreementArea;

    public AgreementDialog()
    {      
        initComponents();

        try
        {
            URL icon32Url = AgreementDialog.class.getClassLoader().getResource(ICON_32_PATH);
            setIconImage(ImageIO.read(icon32Url));

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

        processWindowEvent( new WindowEvent(this, WindowEvent.WINDOW_CLOSING) );
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
                    CouchLogger.get().recordException(AgreementDialog.class, e, true /* Fatal */);
                }

                final AgreementDialog dialog = new AgreementDialog();

                //dialog.setLocationRelativeTo(null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e)
                    {
                        finished.countDown();
                        dialog.dispose();
                        dialog.setVisible(false);
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
        buttonPanel = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        scrollPane = new JScrollPane();
        agreementArea = new JTextArea();

        setTitle("Wezzle");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        dialogPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialogPane.setLayout(new BorderLayout());

        instructionsLabel.setText("<html>Please review the license terms before playing Wezzle.<br/> <br/>If you accept the terms of the agreement, click I Agree.  You must accept the terms of the agreement to play Wezzle.</html>");
        instructionsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        okButton.setText("I Agree");
        cancelButton.setText("Cancel");

        GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
                buttonPanelLayout.createParallelGroup().add(GroupLayout.TRAILING, buttonPanelLayout.createSequentialGroup().addContainerGap(223, Short.MAX_VALUE).add(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.UNRELATED).add(cancelButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE).addContainerGap(9, 9)));
        buttonPanelLayout.setVerticalGroup(
                buttonPanelLayout.createParallelGroup().add(GroupLayout.TRAILING, buttonPanelLayout.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(buttonPanelLayout.createParallelGroup(GroupLayout.BASELINE).add(cancelButton).add(okButton)).addContainerGap()));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //---- textArea1 ----
        agreementArea.setEditable(false);
        agreementArea.setLineWrap(true);
        agreementArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
        agreementArea.setWrapStyleWord(true);
        scrollPane.setViewportView(agreementArea);


        GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
                contentPanelLayout.createParallelGroup().add(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(instructionsLabel, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE).add(scrollPane, GroupLayout.PREFERRED_SIZE, 402, GroupLayout.PREFERRED_SIZE));
        contentPanelLayout.setVerticalGroup(
                contentPanelLayout.createParallelGroup().add(GroupLayout.TRAILING, contentPanelLayout.createSequentialGroup().add(instructionsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap(18, 18).add(scrollPane, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.RELATED).add(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));

        dialogPane.add(contentPanel, BorderLayout.CENTER);

        contentPane.add(dialogPane, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(null);
    }
}

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Tue Feb 09 01:03:32 EST 2010
 */



/**
 * @author Cameron McKay
 */
public class LicenseDialog extends JDialog {
	public LicenseDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public LicenseDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Cameron McKay
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		instructionsLabel = new JLabel();
		licensePanel = new JPanel();
		serialNumberLabel = new JLabel();
		serialNumberField = new JTextField();
		licenseKeyLabel = new JLabel();
		licenseKeyField = new JTextField();
		buttonPanel = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle("Wezzle");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(20, 20, 20, 20));

			// JFormDesigner evaluation mark
			dialogPane.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), dialogPane.getBorder())); dialogPane.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{

				//---- instructionsLabel ----
				instructionsLabel.setText("<html>Please enter your serial number and license key.<br/>\n<br/>\nRemember to enter your serial number and license key exactly as it appears in the e-mail you received from Couchware Inc.\n</html>");
				instructionsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

				//======== licensePanel ========
				{
					licensePanel.setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION));

					//---- serialNumberLabel ----
					serialNumberLabel.setText("Serial Number");

					//---- serialNumberField ----
					serialNumberField.setFont(new Font("Courier New", Font.PLAIN, 12));

					//---- licenseKeyLabel ----
					licenseKeyLabel.setText("License Key");

					//---- licenseKeyField ----
					licenseKeyField.setFont(new Font("Courier New", Font.PLAIN, 12));

					GroupLayout licensePanelLayout = new GroupLayout(licensePanel);
					licensePanel.setLayout(licensePanelLayout);
					licensePanelLayout.setHorizontalGroup(
						licensePanelLayout.createParallelGroup()
							.addGroup(GroupLayout.Alignment.TRAILING, licensePanelLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(licensePanelLayout.createParallelGroup()
									.addComponent(licenseKeyLabel, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
									.addComponent(serialNumberLabel, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(licensePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
									.addComponent(licenseKeyField, GroupLayout.Alignment.TRAILING)
									.addComponent(serialNumberField, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
								.addContainerGap())
					);
					licensePanelLayout.setVerticalGroup(
						licensePanelLayout.createParallelGroup()
							.addGroup(licensePanelLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(licensePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(serialNumberField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(serialNumberLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(licensePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(licenseKeyField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(licenseKeyLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
								.addContainerGap())
					);
				}

				//======== buttonPanel ========
				{

					//---- okButton ----
					okButton.setText("OK");

					//---- cancelButton ----
					cancelButton.setText("Cancel");

					GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
					buttonPanel.setLayout(buttonPanelLayout);
					buttonPanelLayout.setHorizontalGroup(
						buttonPanelLayout.createParallelGroup()
							.addGroup(GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
								.addContainerGap(223, Short.MAX_VALUE)
								.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
								.addGap(9, 9, 9))
					);
					buttonPanelLayout.setVerticalGroup(
						buttonPanelLayout.createParallelGroup()
							.addGroup(GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(cancelButton)
									.addComponent(okButton))
								.addContainerGap())
					);
				}

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(
					contentPanelLayout.createParallelGroup()
						.addComponent(licensePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(instructionsLabel, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
				);
				contentPanelLayout.setVerticalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
							.addComponent(instructionsLabel, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addComponent(licensePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Cameron McKay
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel instructionsLabel;
	private JPanel licensePanel;
	private JLabel serialNumberLabel;
	private JTextField serialNumberField;
	private JLabel licenseKeyLabel;
	private JTextField licenseKeyField;
	private JPanel buttonPanel;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}

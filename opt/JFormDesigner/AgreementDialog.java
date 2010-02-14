import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Sat Feb 13 20:01:39 EST 2010
 */



/**
 * @author Cameron McKay
 */
public class AgreementDialog extends JDialog {
	public AgreementDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public AgreementDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Cameron McKay
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		instructionsLabel = new JLabel();
		buttonPanel = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();
		scrollPane1 = new JScrollPane();
		textArea1 = new JTextArea();

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

				//======== scrollPane1 ========
				{
					scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

					//---- textArea1 ----
					textArea1.setEditable(false);
					textArea1.setLineWrap(true);
					textArea1.setFont(new Font("Tahoma", Font.PLAIN, 11));
					textArea1.setWrapStyleWord(true);
					scrollPane1.setViewportView(textArea1);
				}

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(
					contentPanelLayout.createParallelGroup()
						.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(instructionsLabel, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
						.addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 402, GroupLayout.PREFERRED_SIZE)
				);
				contentPanelLayout.setVerticalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
							.addComponent(instructionsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(18, 18, 18)
							.addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				);
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
	private JScrollPane scrollPane1;
	private JTextArea textArea1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}

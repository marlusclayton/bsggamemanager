/*
 * Created by JFormDesigner on Thu Mar 22 21:17:58 PDT 2012
 */

package com.bsg.view;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Benjamin Schwartz
 */
public class TextWindow extends JFrame implements ClipboardOwner {


	private static final long serialVersionUID = -7096638467694736801L;
	public TextWindow(String text) {
		initComponents();
		
		this.text.setText(text);
	}

	private void copyButtonActionPerformed(ActionEvent e) {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.setContents(new StringSelection(text.getText()), this);
		copyLabel.setVisible(true);
	}
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		//nop
	}

	private void okButtonActionPerformed(ActionEvent e) {
		this.dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		scrollPane1 = new JScrollPane();
		text = new JTextPane();
		buttonBar = new JPanel();
		copyLabel = new JLabel();
		copyButton = new JButton();
		okButton = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"161dlu, $lcgap, 181dlu",
					"2*(default, $lgap), 147dlu"));

				//======== scrollPane1 ========
				{

					//---- text ----
					text.setEditable(false);
					scrollPane1.setViewportView(text);
				}
				contentPanel.add(scrollPane1, cc.xywh(1, 1, 3, 5));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setLayout(new FormLayout(
					"$lcgap, 66dlu, $glue, $button, $lcgap, $button",
					"pref"));

				//---- copyLabel ----
				copyLabel.setText("Copied!");
				copyLabel.setFont(copyLabel.getFont().deriveFont(copyLabel.getFont().getStyle() | Font.BOLD));
				copyLabel.setVisible(false);
				buttonBar.add(copyLabel, cc.xy(2, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

				//---- copyButton ----
				copyButton.setText("Copy");
				copyButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						copyButtonActionPerformed(e);
					}
				});
				buttonBar.add(copyButton, cc.xy(4, 1));

				//---- okButton ----
				okButton.setText("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});
				buttonBar.add(okButton, cc.xy(6, 1));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JScrollPane scrollPane1;
	private JTextPane text;
	private JPanel buttonBar;
	private JLabel copyLabel;
	private JButton copyButton;
	private JButton okButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

}

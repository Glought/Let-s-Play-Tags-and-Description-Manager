/*
    Let's Play Tags and Description Manager and  Used to manage  video tags and descriptions for easy  copying.
    Copyright (C) 2012  ThoughtsOfGlought

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */

package org.thoughtsofglought.letsplaytagsdescriptionmanager;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DesAddDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8457484045443560032L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	/**
	 * Create the dialog.
	 */
	public DesAddDialog() {

		setTitle("Adding Description");
		setResizable(false);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 442, 240);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);

		JLabel lblName = new JLabel("Let's Play Name:");
		lblName.setBounds(10, 11, 182, 14);
		contentPanel.add(lblName);

		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.BOLD, 11));
		textField.setBounds(10, 26, 422, 20);
		contentPanel.add(textField);
		textField.setColumns(10);

		JLabel lblTags = new JLabel("Description:");
		lblTags.setBounds(10, 57, 100, 14);
		contentPanel.add(lblTags);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 76, 422, 153);
		contentPanel.add(scrollPane);
		

		final JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.BOLD, 13));
		textArea.setLineWrap(true);
		textArea.setBounds(10, 76, 422, 153);
		scrollPane.setViewportView(textArea);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 240, 442, 33);
			getContentPane().add(buttonPane);
			buttonPane.setLayout(null);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						try {
							String lpName = textField.getText();
							String lpText = textArea.getText();
							Connection conn = DriverManager
									.getConnection("jdbc:sqlite:lpTags.db");
							if (textArea.getText().equals("")
									&& textField.getText().equals("")) {
								JOptionPane.showMessageDialog(null,
										"Missing Name and Description",
										"Missing Info!",
										JOptionPane.ERROR_MESSAGE);
							} else {
								PreparedStatement prep = conn
										.prepareStatement("Insert into letsplaydes (name2,lpdes) values (?,?);");
								prep.setString(1, lpName);
								prep.setString(2, lpText);
								prep.addBatch();
								conn.setAutoCommit(false);
								prep.executeBatch();
								conn.setAutoCommit(true);
								textField.setText(null);
								textArea.setText(null);
								dispose();
							}

						} catch (SQLException e) {
							e.printStackTrace();
						}

					}
				});
				okButton.setBounds(117, 5, 82, 23);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						textField.setText(null);
						textArea.setText(null);
						dispose();
					}
				});
				cancelButton.setBounds(232, 5, 103, 23);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}

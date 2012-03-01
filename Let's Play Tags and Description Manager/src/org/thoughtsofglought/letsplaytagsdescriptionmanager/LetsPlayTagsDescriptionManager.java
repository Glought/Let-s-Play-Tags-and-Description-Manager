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

import java.awt.EventQueue;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class LetsPlayTagsDescriptionManager {

	private JFrame frmLetsplaytagsmanager;
	public Connection conn = DriverManager
			.getConnection("jdbc:sqlite:lpTags.db");
	private ResultSet res;

	/**
	 * Launch the application.
	 */
	private TagsAddDialog aTagsDialog = new TagsAddDialog();
	private DesAddDialog aDesDialog = new DesAddDialog();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Class.forName("org.sqlite.JDBC");
					LetsPlayTagsDescriptionManager window = new LetsPlayTagsDescriptionManager();
					window.frmLetsplaytagsmanager.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws SQLException
	 */
	public LetsPlayTagsDescriptionManager() throws SQLException {
		initialize();
		Statement statTags = conn.createStatement();
		String sql = "create TABLE IF NOT EXISTS letsplaytags (name varchar(80), lptags text(5000))";
		statTags.executeUpdate(sql);
		Statement statDes = conn.createStatement();
		String sqlDes = "create TABLE IF NOT EXISTS letsplaydes (name2 varchar(80), lpdes text(5000))";
		statDes.executeUpdate(sqlDes);
	}

	protected void initialize() throws SQLException {
		frmLetsplaytagsmanager = new JFrame();
		frmLetsplaytagsmanager.setResizable(false);
		frmLetsplaytagsmanager.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(
						LetsPlayTagsDescriptionManager.class
								.getResource("/resources/reload.png")));
		frmLetsplaytagsmanager
				.setTitle("Lets Play Tags And Description  Manager");
		frmLetsplaytagsmanager.setBounds(100, 100, 561, 507);
		frmLetsplaytagsmanager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLetsplaytagsmanager.getContentPane().setLayout(null);
		DefaultListModel lpTagsModel = new DefaultListModel();
		DefaultListModel lpDesModel = new DefaultListModel();

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 548, 475);
		frmLetsplaytagsmanager.getContentPane().add(tabbedPane);

		JPanel tagsPanel = new JPanel();
		tabbedPane.addTab("Tags", null, tagsPanel, null);
		tagsPanel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 527, 385);
		tagsPanel.add(scrollPane);

		final JList lpNameTagsList = new JList(lpTagsModel);
		scrollPane.setViewportView(lpNameTagsList);
		lpNameTagsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				populateTagsList(lpNameTagsList);
			}
		});

		populateTagsList(lpNameTagsList);

		lpNameTagsList.getInsets();
		lpNameTagsList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JButton btnNewButton = new JButton("Add");
		btnNewButton.setBounds(112, 418, 97, 23);
		tagsPanel.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aTagsDialog.setVisible(true);
			}
		});

		JButton btnNewButton_1 = new JButton("Edit");
		btnNewButton_1.setBounds(223, 418, 91, 23);
		tagsPanel.add(btnNewButton_1);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ec) {
				try {
					String lpName = (String) lpNameTagsList.getSelectedValue();
					Statement Stat;
					Stat = conn.createStatement();
					res = Stat
							.executeQuery("SELECT lptags FROM letsplaytags WHERE name='"
									+ lpName + "'");
					TagsEditDialog eDialog = new TagsEditDialog(
							(String) lpNameTagsList.getSelectedValue(), res
									.getString("lptags"));
					res.close();
					eDialog.setVisible(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		JButton btnNewButton_2 = new JButton("Copy");
		btnNewButton_2.setBounds(323, 418, 91, 23);
		tagsPanel.add(btnNewButton_2);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyTags(lpNameTagsList);
			}
		});

		JButton btnNewButton_3 = new JButton("Delete");
		btnNewButton_3.setBounds(425, 418, 91, 23);
		tagsPanel.add(btnNewButton_3);
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String lpName = (String) lpNameTagsList.getSelectedValue();
				try {
					Statement Stat;
					Stat = conn.createStatement();
					Stat.executeUpdate("DELETE FROM letsplaytags WHERE name='"
							+ lpName + "'");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				populateTagsList(lpNameTagsList);
			}
		});

		JButton btnNewButton_4 = new JButton("");
		btnNewButton_4.setBounds(30, 418, 43, 22);
		tagsPanel.add(btnNewButton_4);
		btnNewButton_4.setToolTipText("Reloads the list.");
		btnNewButton_4.setIcon(new ImageIcon(
				LetsPlayTagsDescriptionManager.class
						.getResource("/resources/reload.png")));
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				populateTagsList(lpNameTagsList);

			}
		});

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 401, 527, 4);
		tagsPanel.add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(90, 405, 2, 36);
		tagsPanel.add(separator_1);
		separator_1.setOrientation(SwingConstants.VERTICAL);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Descriptions", null, panel, null);
		panel.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 11, 527, 385);
		panel.add(scrollPane_1);

		final JList lpNameDesList = new JList(lpDesModel);
		scrollPane_1.setViewportView(lpNameDesList);
		lpNameDesList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				populateDesList(lpNameDesList);
			}
		});
		lpNameDesList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		populateDesList(lpNameDesList);

		JButton desAddButton = new JButton("Add");
		desAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aDesDialog.setVisible(true);
			}
		});
		desAddButton.setBounds(112, 418, 97, 23);
		panel.add(desAddButton);

		JButton desEditbutton = new JButton("Edit");
		desEditbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String lpName2 = (String) lpNameDesList.getSelectedValue();
					Statement Stat;
					Stat = conn.createStatement();
					res = Stat
							.executeQuery("SELECT lpdes FROM letsplaydes WHERE name2='"
									+ lpName2 + "'");
					DesEditDialog eDesDialog = new DesEditDialog(
							(String) lpNameDesList.getSelectedValue(), res
									.getString("lpdes"));
					res.close();
					eDesDialog.setVisible(true);
				} catch (SQLException ed) {
					ed.printStackTrace();
				}

			}
		});
		desEditbutton.setBounds(223, 418, 91, 23);
		panel.add(desEditbutton);

		JButton desCopyButton = new JButton("Copy");
		desCopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyDes(lpNameDesList);

			}
		});
		desCopyButton.setBounds(323, 418, 91, 23);
		panel.add(desCopyButton);

		JButton desDeleteButton = new JButton("Delete");
		desDeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					String lpName = (String) lpNameDesList.getSelectedValue();
					Statement Stat;
					Stat = conn.createStatement();
					Stat.executeUpdate("DELETE FROM letsplaydes WHERE name2='"
							+ lpName + "'");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				populateDesList(lpNameDesList);
			}
		});
		desDeleteButton.setBounds(425, 418, 91, 23);
		panel.add(desDeleteButton);

		JButton desReloadButton = new JButton("");
		desReloadButton.setIcon(new ImageIcon(
				LetsPlayTagsDescriptionManager.class
						.getResource("/resources/reload.png")));

		desReloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				populateDesList(lpNameDesList);

			}
		});
		desReloadButton.setToolTipText("Reloads the list.");
		desReloadButton.setBounds(30, 418, 43, 22);
		panel.add(desReloadButton);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 401, 527, 4);
		panel.add(separator_2);

		JSeparator separator_3 = new JSeparator();
		separator_3.setOrientation(SwingConstants.VERTICAL);
		separator_3.setBounds(90, 405, 2, 36);
		panel.add(separator_3);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("About", null, panel_1, null);
		panel_1.setLayout(null);
		
		JTextArea txtrGgggg = new JTextArea();
		txtrGgggg.setText("Let's play Tags and Description Manager\r\nAuthor:Thoughts Of Glought.\r\nYear:2012\r\nLicense: GNU GENERAL PUBLIC LICENSE");
		txtrGgggg.setEditable(false);
		txtrGgggg.setBounds(10, 137, 523, 113);
		panel_1.add(txtrGgggg);

	}

	public void populateTagsList(JList lpNameTagsList) {
		try {

			DefaultListModel model = (DefaultListModel) lpNameTagsList
					.getModel();
			Statement Stat = conn.createStatement();
			res = Stat.executeQuery("SELECT name FROM letsplaytags");
			model.removeAllElements();// but it is already empty
			while (res.next()) {// getting all event names
				model.addElement(res.getString("name"));
			}// end while
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateDesList(JList lpNameDesList) {
		try {

			DefaultListModel model = (DefaultListModel) lpNameDesList
					.getModel();
			Statement Stat = conn.createStatement();
			res = Stat.executeQuery("SELECT name2 FROM letsplaydes");
			model.removeAllElements();// but it is already empty
			while (res.next()) {// getting all event names
				model.addElement(res.getString("name2"));
			}// end while
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void copyTags(JList lpNameTagsList) {

		try {
			String lpName = (String) lpNameTagsList.getSelectedValue();
			Statement Stat;
			Stat = conn.createStatement();
			res = Stat
					.executeQuery("SELECT lptags FROM letsplaytags WHERE name='"
							+ lpName + "'");

			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			StringSelection testSel = new StringSelection(
					res.getString("lptags"));
			clipboard.setContents(testSel, null);
			res.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public void copyDes(JList lpNameDesList) {

		try {
			String lpName = (String) lpNameDesList.getSelectedValue();
			Statement Stat;
			Stat = conn.createStatement();
			res = Stat
					.executeQuery("SELECT lpdes FROM letsplaydes WHERE name2='"
							+ lpName + "'");

			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			StringSelection testSel = new StringSelection(
					res.getString("lpdes"));
			clipboard.setContents(testSel, null);
			res.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
}

/*******************************************************************************
 * PswGen - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGen on your mobile  
 *
 *     Copyright (C) 2005, 2016 Uwe Damken
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.dknapps.pswgen.gui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.dknapps.pswgen.gui.base.BaseCtl;
import de.dknapps.pswgen.gui.base.BaseDialog;
import de.dknapps.pswgen.gui.base.GridBagConstraintsFactory;
import de.dknapps.pswgen.gui.base.WidgetFactory;

public class PasswordDialog extends BaseDialog {

	/** Version für die Serialisierung */
	private static final long serialVersionUID = 9134115360449417530L;

	private JTextField password;

	private JTextArea passwordExplanation;

	/** Hey, it's me ... für die Listener */
	private PasswordDialog me = this;

	/**
	 * Konstruiert diese View mit einer Referenz auf den zugehörigen Controller.
	 */
	public PasswordDialog(BaseCtl ctl) {
		super(ctl);
	}

	/**
	 * This method initializes this
	 */
	@Override
	public void initialize() {
		super.initialize();
		this.setSize(100, 50);
		this.setLocationByPlatform(true);
	}

	/**
	 * This method initializes mContentPane
	 */
	@Override
	public JPanel createContentPane() {
		// Fabriken holen
		GridBagConstraintsFactory gbcf = GridBagConstraintsFactory.getInstance();
		WidgetFactory wf = WidgetFactory.getInstance();
		// Widgets erzeugen
		JPanel panel = wf.getContentPane("PasswordFrame", true);
		panel.add(createPanelPassword(), gbcf.getLabelConstraints(0, 0));
		JButton buttonPasswordOk = wf.getButton("ButtonPasswordOk");
		buttonPasswordOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedPasswordOk(me);
			}
		});
		getRootPane().setDefaultButton(buttonPasswordOk);
		// Widgets zufügen, nächste Zeile
		panel.add(buttonPasswordOk, gbcf.getLabelConstraints(0, 1));

		return panel;
	}

	/**
	 * Panel zum "Password" anlegen
	 */
	private JPanel createPanelPassword() {
		// Fabriken holen
		GridBagConstraintsFactory gbcf = GridBagConstraintsFactory.getInstance();
		WidgetFactory wf = WidgetFactory.getInstance();
		// Widgets erzeugen
		JPanel panel = wf.getPanel("PanelPassword");
		JLabel labelPassword = wf.getLabel("LabelPassword");
		password = wf.getTextField("FieldPassword");
		password.setEditable(false);
		JLabel labelPasswordExplanation = wf.getLabel("LabelPasswordExplanation");
		passwordExplanation = wf.getTextArea("FieldPasswordExplanation");
		passwordExplanation.setEditable(false);
		passwordExplanation.setLineWrap(true);
		passwordExplanation.setWrapStyleWord(true);
		JScrollPane passwordExplanationScrollPane = wf.getScrollPane("ScrollPanePasswordExplanation",
				passwordExplanation);
		passwordExplanationScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// Widgets zufügen, erste Zeile
		int row = 0;
		panel.add(labelPassword, gbcf.getLabelConstraints(0, row));
		panel.add(password, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelPasswordExplanation, gbcf.getLabelConstraints(0, row));
		panel.add(passwordExplanationScrollPane,
				gbcf.getTableConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		// Panel zurückgeben
		return panel;
	}

	/**
	 * @param password
	 *            The password to set.
	 */
	public void setPassword(final String password) {
		this.password.setText(password);
	}

	/**
	 * @param passwordExplanation
	 *            The passwordExplanation to set.
	 */
	public void setPasswordExplanation(final String passwordExplanation) {
		this.passwordExplanation.setText(passwordExplanation);
	}

}
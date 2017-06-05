/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2017 Uwe Damken
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
package de.dknapps.pswgendesktop.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import de.dknapps.pswgendesktop.gui.base.BaseCtl;
import de.dknapps.pswgendesktop.gui.base.BaseDialog;
import de.dknapps.pswgendesktop.gui.base.GridBagConstraintsFactory;
import de.dknapps.pswgendesktop.gui.base.WidgetFactory;

/**
 * <p>
 * Dies ist die View des Startfensters von PswGenDesktop.
 * </p>
 */
public class ChangePassphraseDialog extends BaseDialog {

	/** Version für die Serialisierung */
	private static final long serialVersionUID = 2071974457622476245L;

	/** Farbe für Label-Elemente, deren Felder das generierte Passwort beeinflussen */
	private static final Color COLOR_INFLUENCE = Color.BLUE;

	private JPasswordField newPassphrase;

	private JPasswordField newPassphraseRepeated;

	private JCheckBox makePassphraseVisible;

	/** Hey, it's me ... für die Listener */
	private ChangePassphraseDialog me = this;

	/**
	 * Konstruiert diese View mit einer Referenz auf den zugehörigen Controller.
	 */
	public ChangePassphraseDialog(BaseCtl ctl) {
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
		JPanel panel = wf.getContentPane("ChangePassphraseFrame", true);
		panel.add(createPanelPassphrase(), gbcf.getLabelConstraints(0, 0));
		return panel;
	}

	/**
	 * Panel zum "Passphrase" anlegen
	 */
	private JPanel createPanelPassphrase() {
		// Fabriken holen
		GridBagConstraintsFactory gbcf = GridBagConstraintsFactory.getInstance();
		WidgetFactory wf = WidgetFactory.getInstance();
		// Widgets erzeugen
		JPanel panel = wf.getPanel("PanelNewPassphrase");
		JLabel labelNewPassphrase = wf.getLabel("LabelNewPassphrase");
		labelNewPassphrase.setForeground(COLOR_INFLUENCE);
		newPassphrase = wf.getPasswordField("FieldNewPassphrase");
		JLabel labelNewPassphraseRepeated = wf.getLabel("LabelNewPassphraseRepeated");
		newPassphraseRepeated = wf.getPasswordField("FieldNewPassphraseRepeated");
		makePassphraseVisible = wf.getCheckBox("CheckBoxMakePasswordVisible");
		makePassphraseVisible.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (makePassphraseVisible.isSelected()) {
					newPassphrase.setEchoChar((char) 0);
					newPassphraseRepeated.setEchoChar((char) 0);
				} else {
					newPassphrase.setEchoChar('*');
					newPassphraseRepeated.setEchoChar('*');
				}
			}

		});
		JButton buttonStoreServices = wf.getButton("ButtonStoreServices");
		buttonStoreServices.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedStoreServices(me);
			}
		});
		getRootPane().setDefaultButton(buttonStoreServices);
		// Widgets zufügen, erste Zeile
		int row = 0;
		panel.add(labelNewPassphrase, gbcf.getLabelConstraints(0, row));
		panel.add(newPassphrase, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelNewPassphraseRepeated, gbcf.getLabelConstraints(0, row));
		panel.add(newPassphraseRepeated, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(makePassphraseVisible, gbcf.getFieldConstraints(1, row, 4, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(buttonStoreServices, gbcf.getLabelConstraints(1, row));
		// Panel zurückgeben
		return panel;
	}

	public String getNewPassphrase() {
		return new String(newPassphrase.getPassword());
	}

	public void setNewPassphrase(final String passphrase) {
		this.newPassphrase.setText(passphrase);
	}

	public String getNewPassphraseRepeated() {
		return new String(newPassphraseRepeated.getPassword());
	}

	public void setNewPassphraseRepeated(final String passphraseRepeated) {
		this.newPassphraseRepeated.setText(passphraseRepeated);
	}

}
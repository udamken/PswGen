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
public class StartupDialog extends BaseDialog {

	/** Version für die Serialisierung */
	private static final long serialVersionUID = 8851338816847637163L;

	/** Farbe für Label-Elemente, deren Felder das generierte Passwort beeinflussen */
	private static final Color COLOR_INFLUENCE = Color.BLUE;

	private JPasswordField passphrase;

	private JPasswordField passphraseRepeated;

	private JCheckBox makePassphraseVisible;

	private JPasswordField oldPassphrase;

	private JButton buttonChangePassphrase;

	/** Hey, it's me ... für die Listener */
	private StartupDialog me = this;

	/**
	 * Konstruiert diese View mit einer Referenz auf den zugehörigen Controller.
	 */
	public StartupDialog(BaseCtl ctl) {
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
		JPanel panel = wf.getContentPane("StartupFrame", true);
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
		JPanel panel = wf.getPanel("PanelPassphrase");
		JLabel labelPassphrase = wf.getLabel("LabelPassphrase");
		labelPassphrase.setForeground(COLOR_INFLUENCE);
		passphrase = wf.getPasswordField("FieldPassphrase");
		JLabel labelPassphraseRepeated = wf.getLabel("LabelPassphraseRepeated");
		passphraseRepeated = wf.getPasswordField("FieldPassphraseRepeated");
		JLabel labelOldPassphrase = wf.getLabel("LabelOldPassphrase");
		labelOldPassphrase.setForeground(COLOR_INFLUENCE);
		oldPassphrase = wf.getPasswordField("FieldOldPassphrase");
		makePassphraseVisible = wf.getCheckBox("CheckBoxMakePasswordVisible");
		makePassphraseVisible.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (makePassphraseVisible.isSelected()) {
					passphrase.setEchoChar((char) 0);
					passphraseRepeated.setEchoChar((char) 0);
					oldPassphrase.setEchoChar((char) 0);
				} else {
					passphrase.setEchoChar('*');
					passphraseRepeated.setEchoChar('*');
					oldPassphrase.setEchoChar('*');
				}
			}

		});
		buttonChangePassphrase = wf.getButton("ButtonChangePassphrase");
		// FIXME Rethink rephrasing with two files ...
		// ... probably rephrase first file
		// ... try to load second file with current passphrase first, on failure with old passphrase
		buttonChangePassphrase.setEnabled(false);
		// buttonChangePassphrase.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// ((PswGenCtl) ctl).actionPerformedChangePassphrase(me);
		// }
		// });
		JButton buttonOpenServices = wf.getButton("ButtonOpenServices");
		buttonOpenServices.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedOpenServices(me);
			}
		});
		getRootPane().setDefaultButton(buttonOpenServices);
		// Widgets zufügen, erste Zeile
		int row = 0;
		panel.add(labelPassphrase, gbcf.getLabelConstraints(0, row));
		panel.add(passphrase, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelPassphraseRepeated, gbcf.getLabelConstraints(0, row));
		panel.add(passphraseRepeated, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelOldPassphrase, gbcf.getLabelConstraints(0, row));
		panel.add(oldPassphrase, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(makePassphraseVisible, gbcf.getFieldConstraints(1, row, 4, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(buttonChangePassphrase, gbcf.getLabelConstraints(1, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(buttonOpenServices, gbcf.getLabelConstraints(1, row));
		// Panel zurückgeben
		return panel;
	}

	/**
	 * Sperrt die wiederholte Passphrase-Eingabe.
	 */
	public void disablePassphraseRepeated() {
		passphraseRepeated.setEnabled(false);
	}

	public String getPassphrase() {
		return new String(passphrase.getPassword());
	}

	public void setPassphrase(final String passphrase) {
		this.passphrase.setText(passphrase);
	}

	public String getPassphraseRepeated() {
		return new String(passphraseRepeated.getPassword());
	}

	public void setPassphraseRepeated(final String passphraseRepeated) {
		this.passphraseRepeated.setText(passphraseRepeated);
	}

	public String getOldPassphrase() {
		return new String(oldPassphrase.getPassword());
	}

	public void setOldPassphrase(final String oldPassphrase) {
		this.oldPassphrase.setText(oldPassphrase);
	}

}
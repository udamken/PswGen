package net.sf.pswgen.gui;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2015 Uwe Damken

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
 *****************************************************************************/

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

import net.sf.pswgen.gui.base.BaseCtl;
import net.sf.pswgen.gui.base.BaseDialog;
import net.sf.pswgen.gui.base.GridBagConstraintsFactory;
import net.sf.pswgen.gui.base.WidgetFactory;

/**
 * <p>
 * Dies ist die View des Startfensters von PswGen.
 * </p>
 * <p>
 * Copyright (C) 2005-2013 Uwe Damken
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
		makePassphraseVisible = wf.getCheckBox("CheckBoxMakePasswordVisible");
		makePassphraseVisible.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (makePassphraseVisible.isSelected()) {
					passphrase.setEchoChar((char) 0);
					passphraseRepeated.setEchoChar((char) 0);
				} else {
					passphrase.setEchoChar('*');
					passphraseRepeated.setEchoChar('*');
				}
			}

		});
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
		panel.add(makePassphraseVisible, gbcf.getFieldConstraints(1, row, 4, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(buttonOpenServices, gbcf.getLabelConstraints(1, row));
		// Panel zurückgeben
		return panel;
	}

	/**
	 * @return Returns the passphrase.
	 */
	public String getPassphrase() {
		return new String(passphrase.getPassword());
	}

	/**
	 * @return Returns the passphraseRepeated.
	 */
	public String getPassphraseRepeated() {
		return new String(passphraseRepeated.getPassword());
	}

	/**
	 * @param passphrase
	 *            The passphrase to set.
	 */
	public void setPassphrase(final String passphrase) {
		this.passphrase.setText(passphrase);
	}

	/**
	 * @param passphraseRepeated
	 *            The passphraseRepeated to set.
	 */
	public void setPassphraseRepeated(final String passphraseRepeated) {
		this.passphraseRepeated.setText(passphraseRepeated);
	}

	/**
	 * Sperrt die wiederholte Passphrase-Eingabe.
	 */
	public void disablePassphraseRepeated() {
		passphraseRepeated.setEnabled(false);
	}

}
/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2016 Uwe Damken
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
package de.dknapps.pswgendesktop.gui.base;

import java.awt.GridBagConstraints;

/**
 * <p>
 * Erzeugt GridBagConstraints-Objekte auf vereinfachte und für von mir geschriebene Anwendungen
 * standardisierte Weise.
 * </p>
 */
public class GridBagConstraintsFactory {

	/** Die eine und einzige Instanz dieser Klasse */
	private static GridBagConstraintsFactory instance = new GridBagConstraintsFactory();

	/** Konstruktor ist nicht öffentlich zugreifbar => getInstance() nutzen */
	private GridBagConstraintsFactory() {
	}

	/**
	 * Liefert die eine und einzige Instanz dieser Klasse.
	 */
	public static GridBagConstraintsFactory getInstance() {
		return instance;
	}

	/**
	 * Liefert ein GridBagConstraint mit den Defaults für meine Anwendungen.
	 */
	private GridBagConstraints getConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		return gbc;
	}

	/**
	 * Liefert ein GridBagConstraint mit gridx und gridy mit einer geringen x- und y-Gewichtung. Dies wird
	 * typischerweise für Labels und Buttons verwendet, die sich weniger ausdehnen sollen als Felder und
	 * Tabellen.
	 */
	public GridBagConstraints getLabelConstraints(final int gridx, final int gridy) {
		GridBagConstraints gbc = getConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = 0.1;
		gbc.weighty = 0.0;
		return gbc;
	}

	/**
	 * Liefert ein GridBagConstraint mit gridx und gridy mit einer geringen x- und y-Gewichtung. Dies wird
	 * typischerweise für Labels und Buttons verwendet, die sich weniger ausdehnen sollen als Felder und
	 * Tabellen.
	 */
	public GridBagConstraints getLabelConstraints(final int gridx, final int gridy, final int gridwidth,
			final int gridheight) {
		GridBagConstraints gbc = getLabelConstraints(gridx, gridy);
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		return gbc;
	}

	/**
	 * Liefert ein GridBagConstraint mit gridx, gridy, gridwidth und gridheight mit einer mittleren x- und
	 * y-Gewichtung. Dies wird typischerweise für Felder verwendet, die sich in der Breite mehr als Labels und
	 * in der Höhe weniger als Tabellen ausdehnen sollen.
	 */
	public GridBagConstraints getFieldConstraints(final int gridx, final int gridy, final int gridwidth,
			final int gridheight) {
		GridBagConstraints gbc = getLabelConstraints(gridx, gridy);
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = 0.5;
		gbc.weighty = 0.0;
		return gbc;
	}

	/**
	 * Liefert ein GridBagConstraint mit gridx, gridy, gridwidth und gridheight mit einer mittleren x- und
	 * hohen y-Gewichtung. Dies wird typischerweise für Tabellen verwendet, die sich in der Breite wie Felder
	 * und in der Höhe am meisten von allen Komponenten ausdehnen sollen.
	 */
	public GridBagConstraints getTableConstraints(final int gridx, final int gridy, final int gridwidth,
			final int gridheight) {
		GridBagConstraints gbc = getLabelConstraints(gridx, gridy);
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = 0.5;
		gbc.weighty = 1.0;
		return gbc;
	}

}
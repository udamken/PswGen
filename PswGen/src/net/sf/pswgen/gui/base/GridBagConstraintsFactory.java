package net.sf.pswgen.gui.base;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them

 Copyright (C) 2005-2013  Uwe Damken

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *****************************************************************************/

import java.awt.GridBagConstraints;

/**
 * <p>
 * Erzeugt GridBagConstraints-Objekte auf vereinfachte und für von mir geschriebene Anwendungen
 * standardisierte Weise.
 * </p>
 * <p>
 * (c) 2005, by Uwe Damken
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
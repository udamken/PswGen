package net.sf.pswgen.gui.base;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them

 Copyright (C) 2005-2012  Uwe Damken

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
	public GridBagConstraints getConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		return gbc;
	}

	/**
	 * Liefert ein GridBagConstraint mit gridx und gridy.
	 */
	public GridBagConstraints getConstraints(final int gridx, final int gridy) {
		GridBagConstraints gbc = getConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		return gbc;
	}

	/**
	 * Liefert ein GridBagConstraint mit gridx, gridy, gridwidth und gridheight.
	 */
	public GridBagConstraints getConstraints(final int gridx, final int gridy, final int gridwidth,
			final int gridheight) {
		GridBagConstraints gbc = getConstraints(gridx, gridy);
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		return gbc;
	}

}
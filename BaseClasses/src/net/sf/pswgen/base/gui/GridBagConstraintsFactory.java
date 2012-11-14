package net.sf.pswgen.base.gui;

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
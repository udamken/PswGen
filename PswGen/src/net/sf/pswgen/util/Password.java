package net.sf.pswgen.util;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them

 Copyright (C) 2005-2013 Uwe Damken

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

import java.util.Vector;

/**
 * <p>
 * Stellt ein Passwort einer angegebenen Länge dar.
 * </p>
 * <p>
 * Copyright (C) 2005-2013 Uwe Damken
 * </p>
 */
public class Password {

	/** Ersatzzeichen für ein nicht gesetztes Zeichen */
	private static final char NOT_SET_SURROGATE = ' ';

	/** Die Länges des Passworts */
	private int length;

	/** Die Zeichen des Passworts */
	private Vector<Character> characters;

	/**
	 * Konstruiert ein leeres Passwort in der gewünschten Länge.
	 */
	public Password(final int length) {
		super();
		this.length = length;
		characters = new Vector<Character>();
	}

	/**
	 * Liefert true, wenn das Zeichen in diesem Passwort an der angegebenen Stelle gesetzt ist, wobei 0 die
	 * erste Stelle angibt.
	 */
	public boolean isCharacterSet(final int position) {
		boolean isCharacterSet = false;
		try {
			isCharacterSet = characters.elementAt(position) != null;
		} catch (ArrayIndexOutOfBoundsException e) {
			// Das Zeichen gibt es wohl noch nicht => false
		}
		return isCharacterSet;
	}

	/**
	 * Liefert die Anzahl der Zeichen, die in dem Passwort innerhalb des angegebenen Positionsbereiches
	 * (einschließlich) bereits gesetzt sind.
	 */
	public int charactersSet(final int leftmost, final int rightmost) {
		int charactersSet = 0;
		for (int i = leftmost; i <= rightmost; i++) {
			if (isCharacterSet(i)) {
				charactersSet++;
			}
		}
		return charactersSet;
	}

	/**
	 * Liefert die Anzahl der Zeichen, die in dem Passwort bereits gesetzt sind.
	 */
	public int charactersSet() {
		return charactersSet(0, length - 1);
	}

	/**
	 * Liefert true, wenn alle Zeichen des Passworts in der gewünschten Länge gesetzt sind.
	 */
	public boolean areCharactersSet() {
		return charactersSet() == length;
	}

	/**
	 * Setzt das Zeichen an der angegebenen Stelle in diesem Passwort, wobei 0 die erste Stelle angibt.
	 */
	public void setCharacterAt(final Character c, final int position) {
		// Sicher stellen, dass der Vector groß genug ist
		for (int i = characters.size(); i <= position; i++) {
			characters.add(null);
		}
		// Jetzt das Zeichen an der gewünschten Stelle setzen
		characters.setElementAt(c, position);
	}

	/**
	 * Liefert das Zeichen an der angegebenen Stelle, wenn es gesetzt ist, ansonsten ein Blank.
	 */
	public char getCharacterAt(final int i) {
		if (isCharacterSet(i)) {
			return characters.elementAt(i).charValue();
		} else {
			return NOT_SET_SURROGATE;
		}
	}

	/**
	 * Liefert das Passwort als String. Zeichen, die nicht gesetzt wurden, werden durch ein Blank ersetzt.
	 */
	public String getPassword() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(getCharacterAt(i));
		}
		return sb.toString();
	}

	/**
	 * @return Returns the length.
	 */
	public int getLength() {
		return length;
	}
}
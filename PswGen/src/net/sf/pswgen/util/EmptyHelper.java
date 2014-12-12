package net.sf.pswgen.util;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2014 Uwe Damken

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

/**
 * <p>
 * Hilft bei der Behandlung von Leerwerten in Wertefelder, wie z.B. Zahlenfeldern.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid identisch, sprich kopiert.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class EmptyHelper {

	/** Der Leerwert für einen Integer */
	public static final int EMPTY_INT = Integer.MIN_VALUE;

	/** Der Leerwert für einen String */
	public static final String EMPTY_STRING = "";

	/**
	 * Liefert den defaultValue, wenn der übergebene value der Leerwert ist.
	 */
	public static int getValue(final int value, final int defaultValue) {
		return (isEmpty(value)) ? defaultValue : value;
	}

	/**
	 * Liefert true, wenn der übergebene Zahlenwert der Leerwert ist.
	 */
	public static boolean isEmpty(int value) {
		return (value == EMPTY_INT);
	}

}

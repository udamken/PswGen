package net.sf.pswgen.util;

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

/**
 * <p>
 * Konvertiert Werte vom Textformat in ein anderes und zurück.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
public class ConverterHelper {

	/**
	 * Liefert den Zahlenwert eines Strings oder einen Nullwert, wenn der String keinen Zahlenwert darstellt,
	 * und damit insbesondere auch, wenn er leer ist.
	 */
	public static int toInt(final String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return EmptyHelper.EMPTY_INT;
		}
	}

	/**
	 * Liefert den String eines Zahlenwertes oder einen Leerstring, wenn der Zahlenwert ein Leerwert ist.
	 */
	public static String toString(final int i) {
		if (EmptyHelper.isEmpty(i)) {
			return EmptyHelper.EMPTY_STRING;
		} else {
			return Integer.toString(i);
		}
	}

}

/*******************************************************************************
 * PswGen - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGen on your mobile  
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
package de.dknapps.pswgen.util;

/**
 * <p>
 * Konvertiert Werte vom Textformat in ein anderes und zurück.
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
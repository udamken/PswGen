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
package de.dknapps.pswgencore.util;

/**
 * <p>
 * Hilft bei der Behandlung von Leerwerten in Wertefelder, wie z.B. Zahlenfeldern.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid identisch, sprich kopiert.
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

	/**
	 * Liefert true, wenn der String null ist oder die Länge 0 hat.
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}

}

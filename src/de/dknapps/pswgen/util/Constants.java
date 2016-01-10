package de.dknapps.pswgen.util;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.dknapps.pswgen.ApplicationPackageNameMarker;

/**
 * <p>
 * Hält alle Konstanten von PswGen.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid identisch, sprich kopiert.
 * </p>
 * <p>
 * Copyright (C) 2005-2015 Uwe Damken
 * </p>
 */
public class Constants {

	/** Der angezeigte Name dieser Anwendung */
	public static final String APPLICATION_NAME = "PswGen";

	/** Die Version dieser Anwendung */
	public static final String APPLICATION_VERSION = "1.7.6";

	/** Die Version dieser Anwendung, ab der ein neues Dateiformat benutzt wird */
	public static final String ADVANCED_FILE_FORMAT_VERSION = "1.7.4";

	/** Die Version dieser Anwendung, ab der das Dateiformat (mit Upgrade) unterstützt wird */
	public static final String LOWEST_SUPPORTED_FILE_FORMAT_VERSION = "1.7.0";

	/** Die Version dieser Anwendung */
	public static final String APPLICATION_VERIFIER = "PswGen - Manages your websites and repeatably generates passwords for them, Copyright (C) Uwe Damken";

	/** Das Package dieser Anwendung */
	public static final String APPLICATION_PACKAGE_NAME = ApplicationPackageNameMarker.class.getPackage()
			.getName();

	/** Resource Name des Icons der Anwendung */
	public static final String APPLICATION_IMAGE_RESOURCE_NAME = "de/dknapps/pswgen/PswGen.png";

	/** Resource Name des Images für den About-Dialog */
	public static final String ABOUT_IMAGE_RESOURCE_NAME = "de/dknapps/pswgen/PswGenSplash.png";

	/** Texte der anwendungsspezifischen Meldungen */

	public static final String MSG_INVALID_WIDGET_INFO = "Invalid WidgetInfo definition <{1}> for <{0}>";

	public static final String MSG_NO_WIDGET_INFO = "No WidgetInfo defined for <{0}>";

	public static final String MSG_EXCP_SERVICES = "Exception occured while loading services:";

	public static final String MSG_EXCP_LOOK_AND_FEEL = "LookAndFeel could not be set";

	public static final String MSG_PASSPHRASE_INVALID = "Passphrase given as parameter to the upgrade option is not valid: ";

	/** Für die generierten Passworte verwendbare Zeichen */

	public static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

	public static final String LETTERS = UPPERCASE_LETTERS + LOWERCASE_LETTERS;

	public static final String DIGITS = "0123456789";

	public static final String ALPHANUMERICS = LETTERS + DIGITS;

	public static final String SPECIAL_CHARS = ",.;:!$&()=?+-*/#";

	private static final DateFormat DATE_FORMAT_de_DE = new SimpleDateFormat("dd.MM.yyyy");

	private static final DateFormat DATE_FORMAT_NON_GERMAN = DateFormat.getDateInstance(DateFormat.SHORT);

	public static final DateFormat DATE_FORMAT = (Locale.getDefault().getLanguage().equals("de")
			&& Locale.getDefault().getCountry().equals("DE")) ? DATE_FORMAT_de_DE : DATE_FORMAT_NON_GERMAN;

	public static final String SERVICES_FILENAME = "services.json";

	public static final String CHARSET_NAME = "UTF-8";

}

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import net.sf.pswgen.ApplicationPackageNameMarker;

/**
 * <p>
 * Hält alle Konstanten von PswGen.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid identisch, sprich kopiert.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class Constants {

	/** Der angezeigte Name dieser Anwendung */
	public static final String APPLICATION_NAME = "PswGen";

	/** Die Version dieser Anwendung */
	public static final String APPLICATION_VERSION = "1.7.2";

	/** Die Version dieser Anwendung, ab der ein neues Dateiformat benutzt wird */
	public static final String ADVANCED_FILE_FORMAT_VERSION = "1.7.0";

	/** Die Version dieser Anwendung, bis zu der das Dateiformat nicht mehr unterstützt wird */
	public static final String UNSUPPORTED_FILE_FORMAT_VERSION = "1.5.9";

	/** Die Version dieser Anwendung */
	public static final String APPLICATION_VERIFIER = "PswGen - Manages your websites and repeatably generates passwords for them, Copyright (C) Uwe Damken";

	/** Das Package dieser Anwendung */
	public static final String APPLICATION_PACKAGE_NAME = ApplicationPackageNameMarker.class.getPackage()
			.getName();

	/** Resource Name des Icons der Anwendung */
	public static final String APPLICATION_IMAGE_RESOURCE_NAME = "net/sf/pswgen/PswGen.png";

	/** Resource Name des Images für den About-Dialog */
	public static final String ABOUT_IMAGE_RESOURCE_NAME = "net/sf/pswgen/PswGenSplash.png";

	/** Texte der anwendungsspezifischen Meldungen */

	public static final String MSG_INVALID_WIDGET_INFO = "Invalid WidgetInfo definition <{1}> for <{0}>";

	public static final String MSG_NO_WIDGET_INFO = "No WidgetInfo defined for <{0}>";

	public static final String MSG_EXCP_SERVICES = "Exception occured while loading services:";

	public static final String MSG_EXCP_LOOK_AND_FEEL = "LookAndFeel could not be set";

	public static final String MSG_UNSUPPORTED_FILE_FORMAT_VERSION = "File format of PswGen < 1.6 is no longer supported: Use any PswGen 1.6.x to convert file to newer format. Use current PswGen afterwards to convert to current format.";

	public static final String MSG_TO_BE_CONVERTED_FILE_FORMAT_VERSION = "File format of PswGen < 1.7 has to be converted: Use command line option -upgrade to convert the file.";

	public static final String MSG_ALREADY_CONVERTED_FILE_FORMAT_VERSION = "File format of PswGen >= 1.7 doesn't need to be converted: Start PswGen without option -upgrade.";

	public static final String MSG_EMPTY_FILE_NOT_UPGRADABLE = "File format cannot be converted: File does not exist or is empty.";

	public static final String MSG_UNKNOWN_FILE_FORMAT_VERSION = "File format is unknown and not usable with PswGen.";

	/** Für die generierten Passworte verwendbare Zeichen */

	public static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

	public static final String LETTERS = UPPERCASE_LETTERS + LOWERCASE_LETTERS;

	public static final String DIGITS = "0123456789";

	public static final String ALPHANUMERICS = LETTERS + DIGITS;

	public static final String SPECIAL_CHARS = ",.;:!$&()=?+-*/#";

	private static final DateFormat DATE_FORMAT_de_DE = new SimpleDateFormat("dd.MM.yyyy");

	private static final DateFormat DATE_FORMAT_NON_GERMAN = DateFormat.getDateInstance(DateFormat.SHORT);

	public static final DateFormat DATE_FORMAT = (Locale.getDefault().getLanguage().equals("de") && Locale
			.getDefault().getCountry().equals("DE")) ? DATE_FORMAT_de_DE : DATE_FORMAT_NON_GERMAN;

	public static final String SERVICES_FILENAME = "services.json";

}

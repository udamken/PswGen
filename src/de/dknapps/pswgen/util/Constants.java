/*******************************************************************************
 * PswGen - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGen on your mobile  
 *
 *     Copyright (C) 2005, 2016 Uwe Damken
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
	public static final String APPLICATION_VERIFIER = "PswGen - Manages your websites and repeatably generates passwords for them, Copy"
			+ "right (C) Uwe Damken"; // String splitted "against" Eclipse Releng

	/** Das Package dieser Anwendung */
	public static final String APPLICATION_PACKAGE_NAME = ApplicationPackageNameMarker.class.getPackage()
			.getName();

	/** Resource Name des Icons der Anwendung */
	public static final String APPLICATION_IMAGE_RESOURCE_NAME = "de/dknapps/pswgen/PswGen.png";

	/** URL zur HTML-Datei für den About-Dialog */
	public static final String ABOUT_DIALOG_HTML_FILENAME = "about.html";

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

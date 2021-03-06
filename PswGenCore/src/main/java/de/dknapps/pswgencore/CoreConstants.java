/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2018 Uwe Damken
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
package de.dknapps.pswgencore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * <p>
 * Hält alle Konstanten und markiert das Top-Level-Package von PswGenCore.
 * </p>
 */
public class CoreConstants {

	/** Die Version von PswGen, ab der das neueste Dateiformat erzeugt wird (zum Mischen mehrerer Dateien) */
	public static final String NEWEST_FILE_FORMAT_VERSION = "2.0.0";

	/** Die älteste Version von PswGen, deren Dateiformat (mit Upgrade) unterstützt wird */
	public static final String LOWEST_SUPPORTED_FILE_FORMAT_VERSION = "1.7.8";

	/** Die Version aller Teile von PswGen */
	public static final String APPLICATION_VERSION = "2.0.1";

	/** Das Top-Level-Package von PswGenCore */
	public static final String APPLICATION_PACKAGE_NAME = CoreConstants.class.getPackage().getName();

	/** Der Name des Loggers von PswGenCore */
	public static final String LOGGER_NAME = APPLICATION_PACKAGE_NAME + ".Logger";

	/** Texte der Meldungen für das Logging */

	public static final String MSG_INVALID_WIDGET_INFO = "Invalid WidgetInfo definition <{1}> for <{0}>";

	public static final String MSG_NO_WIDGET_INFO = "No WidgetInfo defined for <{0}>";

	public static final String MSG_EXCP_LOOK_AND_FEEL = "LookAndFeel could not be set";

	/** Für die generierten Passworte verwendbare Zeichen */

	public static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

	public static final String LETTERS = UPPERCASE_LETTERS + LOWERCASE_LETTERS;

	public static final String DIGITS = "0123456789";

	public static final String ALPHANUMERICS = LETTERS + DIGITS;

	public static final String SPECIAL_CHARS = ",.;:!$&()=?+-*/#";

	/** Zum Sortieren verwendetes Alternativformat für das Datum in den Zusatzinformationen */
	public static final DateFormat DATE_FORMAT_de_DE = new SimpleDateFormat("dd.MM.yyyy");

	/** Das für alle Teile von PswGen verwendete Datumsformat */
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");;

	/** Das für alle Teile von PswGen verwendete Timestampformat */
	public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	/** Der Default-Name für die Dienstedatei */
	public static final String SERVICES_FILENAME = "services.json";

	/** Der Default-Name für die andere Dienstedatei */
	public static final String OTHER_SERVICES_FILENAME = "services-mobile.json";

	/** Das Encoding für die Dienstedatei */
	public static final String CHARSET_NAME = "UTF-8";

}

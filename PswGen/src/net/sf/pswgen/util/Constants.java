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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import net.sf.pswgen.ApplicationPackageNameMarker;

/**
 * <p>
 * Hält alle Konstanten dieser Anwendung. Das Interface ConstantsProvider dient dazu, dass
 * net.sf.pswgen.base.Services auf auf diese Konstanten zugreifen kann, ohne die konkrete Klasse kennen zu
 * müssen. Dies ist sinnvoll, um die Base-Klassen später mal extrahieren zu können.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class Constants {

	/** Der angezeigte Name dieser Anwendung */
	public static final String APPLICATION_NAME = "PswGen";

	/** Die Version dieser Anwendung */
	public static final String APPLICATION_VERSION = "1.7.0";

	/** Die Version dieser Anwendung, ab der ein neues Dateiformat benutzt wird */
	public static final String ADVANCED_FILE_FORMAT_VERSION = "1.7.0";

	/** Die Version dieser Anwendung, bis zu der das Dateiformat nicht mehr unterstützt wird */
	public static final String UNSUPPORTED_FILE_FORMAT_VERSION = "1.5.9";

	/** Die Version dieser Anwendung */
	public static final String APPLICATION_VERIFIER = "PswGen - Manages your websites and repeatably generates passwords for them, Copyright (C) Uwe Damken";

	/** Die URL zur deutschsprachigen Hilfe */
	public static final String HELP_URL_DE = "http://mind-and-useful.de/de/software/pswgen.html";

	/** Die URL zur englischsprachigen Hilfe */
	public static final String HELP_URL_EN = "http://mind-and-useful.de/en/software/pswgen.html";

	/** Das Package dieser Anwendung */
	public static final String APPLICATION_PACKAGE_NAME = ApplicationPackageNameMarker.class.getPackage()
			.getName();

	/** Resource Name des Icons der Anwendung */
	public static final String APPLICATION_IMAGE_RESOURCE_NAME = "net/sf/pswgen/PswGen.png";

	/** Resource Name des Images für den About-Dialog */
	public static final String ABOUT_IMAGE_RESOURCE_NAME = "net/sf/pswgen/PswGenSplash.png";

	/** Nummern der anwendungsspezifischen Meldungen, Texte dazu in Messages.properties */

	public static final String MSG_INVALID_WIDGET_INFO = "000";

	public static final String MSG_NO_WIDGET_INFO = "001";

	public static final String MSG_EXCP_SERVICES = "100";

	public static final String MSG_EXCP_LOOK_AND_FEEL = "101";

	public static final String MSG_UNSUPPORTED_FILE_FORMAT_VERSION = "102";

	public static final String MSG_TO_BE_CONVERTED_FILE_FORMAT_VERSION = "103";

	public static final String MSG_ALREADY_CONVERTED_FILE_FORMAT_VERSION = "104";

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

}

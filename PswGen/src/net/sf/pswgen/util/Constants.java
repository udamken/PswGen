package net.sf.pswgen.util;

import net.sf.pswgen.ApplicationPackageNameMarker;

/**
 * <p>
 * Hält alle Konstanten dieser Anwendung. Das Interface ConstantsProvider dient dazu, dass
 * net.sf.pswgen.base.Services auf auf diese Konstanten zugreifen kann, ohne die konkrete Klasse kennen zu
 * müssen. Dies ist sinnvoll, um die Base-Klassen später mal extrahieren zu können.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
public class Constants {

	/** Der angezeigte Name dieser Anwendung */
	public static final String APPLICATION_NAME = "PswGen";

	/** Die Version dieser Anwendung */
	public static final String APPLICATION_VERSION = "1.5.3";

	/** Das Package dieser Anwendung */
	public static final String APPLICATION_PACKAGE_NAME = ApplicationPackageNameMarker.class.getPackage()
			.getName();

	/** Resource Name des Icons der Anwendung */
	public static final String APPLICATION_ICON_RESOURCE_NAME = "net/sf/pswgen/PswGen.png";

	/** Nummern der anwendungsspezifischen Meldungen, Texte dazu in Messages.properties */

	public static final String MSG_INVALID_WIDGET_INFO = "000";

	public static final String MSG_NO_WIDGET_INFO = "001";

	public static final String MSG_EXCP_SERVICES = "100";

	public static final String MSG_EXCP_LOOK_AND_FEEL = "101";

	/** Für die generierten Passworte verwendbare Zeichen */

	public static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

	public static final String LETTERS = UPPERCASE_LETTERS + LOWERCASE_LETTERS;

	public static final String DIGITS = "0123456789";

	public static final String ALPHANUMERICS = LETTERS + DIGITS;

	public static final String SPECIAL_CHARS = ",.;:!$&()=?+-*/#";

	public static final String CHARACTERS = ALPHANUMERICS + SPECIAL_CHARS;

}

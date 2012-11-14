package net.sf.pswgen.base.util;

/**
 * <p>
 * <code>ConstantsProvider</code> ermöglicht den Basisklassen einen Zugriff auf konstante Informationen der
 * konkreten Anwendung ohne den Namen der jeweiligen Klasse kennen zu müssen. Typischerweise implementiert
 * <code>Constants</code> in der Anwendung dieses Interface, während <code>Services</code> in den Basisklassen
 * diese Informationen verwendet.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
public interface ConstantsProvider {

	/** Nummern der Meldungen der BaseClasses, Texte dazu in Messages.properties der Anwendung */

	public static final String MSG_INVALID_WIDGET_INFO = "000";

	public static final String MSG_NO_WIDGET_INFO = "001";

	/**
	 * Liefert den Basisnamen der Anwendungspackages.
	 */
	public String getApplicationPackageName();

	/**
	 * Liefert den Dateinamen des Anwendungs-Icons.
	 */
	public String getApplicationIconResourceName();

	/**
	 * Lierfert den Namen der Anwendung, um ihn z.B. im Fenstertitel anzuzeigen.
	 */
	public String getApplicationName();

	/**
	 * Liefert die Version der Anwendung.
	 */
	public String getApplicationVersion();

}

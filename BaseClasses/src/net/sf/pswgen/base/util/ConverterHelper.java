package net.sf.pswgen.base.util;

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

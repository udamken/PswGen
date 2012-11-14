package net.sf.pswgen.base.util;

/**
 * <p>
 * Hilft bei der Behandlung von Leerwerten in Wertefelder, wie z.B. Zahlenfeldern.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
public class EmptyHelper {

	/** Der Leerwert für einen Integer */
	protected static final int EMPTY_INT = Integer.MIN_VALUE;

	/** Der Leerwert für einen String */
	protected static final String EMPTY_STRING = "";

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

}

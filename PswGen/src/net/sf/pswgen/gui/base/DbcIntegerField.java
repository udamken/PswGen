package net.sf.pswgen.gui.base;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import net.sf.pswgen.util.ConverterHelper;


/**
 * <p>
 * Implementiert ein Feld auf der GUI, das nur Ziffern annimmt, aber auch leer sein kann. Mit JDK 1.5 und
 * Boxing könnte das Leerwerthandling auf null-Werte umgestellt werden.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
public class DbcIntegerField extends JTextField {

	/** Version für die Serialisierung */
	private static final long serialVersionUID = 2684552023967835750L;

	/** Maximale Anzahl von Ziffern für dieses Feld */
	protected int maxDigits;

	/**
	 * Erzeugt ein IntegerField, in das maximal 5 Ziffern eingegeben werden können.
	 */
	public DbcIntegerField() {
		this(5);
	}

	/**
	 * Erzeugt ein IntegerField, in das maximal maxDigits Ziffern eingegeben werden können.
	 */
	public DbcIntegerField(int maxDigits) {
		super(maxDigits); // Vermutliche Breite berücksichtigen
		this.maxDigits = maxDigits;
	}

	/**
	 * Siehe Java-API-Dokumentation unter JTextField wie Eingaben modifiziert werden können. In dem Beispiel
	 * werden alle eingegebenen Buchstaben in Großbuchstaben umgesetzt. Das habe ich hier geändert und
	 * ausgenutzt.
	 */
	@Override
	protected Document createDefaultModel() {
		return new DigitsOnlyDocument();
	}

	/**
	 * Gibt den Inhalt des Feldes als Integer zurück. Dieser Integer-Wert kann auch ein Leerwert sein.
	 */
	public int getIntValue() {
		return ConverterHelper.toInt(getText());
	}

	/**
	 * Setzt den Inhalt des Feldes auf einen übergebenen int-Wert. Dieser kann auch ein Leerwert sein.
	 */
	public void setIntValue(final int value) {
		setText(ConverterHelper.toString(value));
	}

	/**
	 * Diese Klasse beschränkt die Eingaben in diesem Feld auf Ziffern.
	 */
	class DigitsOnlyDocument extends PlainDocument {

		/** Version für die Serialisierung */
		private static final long serialVersionUID = 3028753861206518678L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.text.Document#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
		 */
		@Override
		public void insertString(final int offset, final String str, final AttributeSet attr)
				throws BadLocationException {
			if (str == null) {
				return;
			}
			String digitsOnly = "";
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (Character.isDigit(c)) {
					if (getLength() + digitsOnly.length() < maxDigits) { // Passt?
						digitsOnly += c; // ja, geht noch rein
					} else {
						break; // Der Rest überschreitet ohnehin die Gesamtlänge
					}
				}
			}
			super.insertString(offset, digitsOnly, attr);
		}
	}

}

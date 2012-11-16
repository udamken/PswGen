package net.sf.pswgen.util;

import java.util.Random;

import net.sf.pswgen.base.util.DomainException;

/**
 * <p>
 * Eine Instanz dieser Klasse generiert ein Passwort. Zum Holen eines neuen Passworts sollte daher eine neue
 * Instanz erzeugt werden.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
public class PasswordFactory {

	/** Der vom PasswordGenerator verwendete Zufallszahlengenerator */
	private static Random random = new Random();

	/** Das von dieser Instanz der PasswordFactory zu erzeugende Passwort */
	private Password password = null;

	/**
	 * Konstruktor für eine PasswordFactory, die genau ein Passwort erzeugt. Nach dem Konstruktor sollt null-
	 * bis mehrfach distributeCharacters() unter Verwendung der Konstanten dieser Klasse aufgerufen werden.
	 * Abschließend kann das Passwort mit getPassword abgeholt werden.
	 */
	public PasswordFactory(final int length) {
		password = new Password(length);
	}

	/**
	 * Führt setSeed auf dem verwendeten Zufallsgenerator aus und damit in der Folge zu wiederholbaren
	 * Ergebnisse und sollte daher nur für JUnit-Tests verwendet werden, dann aber auch direkt nach dem
	 * Konstruktor.
	 */
	public void setSeedForRandomToEnforceReproducableResults(final long seedForRandom) {
		random.setSeed(seedForRandom);
	}

	/**
	 * Setzt ein zufällig aus dem übergebenen Satz von Zeichen ausgewähltes Zeichen an die angegebene Position
	 * in dem übergebenen Passwort, wenn die Position bisher frei war. Wenn die Position bisher frei war und
	 * das Zeichen neu besetzt wurde, wird true zurück geliefert, sonst false.
	 */
	private boolean setRandomCharacter(final String characters, final int position) {
		if (password.isCharacterSet(position)) { // Position bereits besetzt?
			return false;
		} else {
			final int pos = random.nextInt(characters.length());
			password.setCharacterAt(new Character(characters.charAt(pos)), position);
			return true;
		}
	}

	/**
	 * Verteilt die angegebene Anzahl von Zeichen aus dem übergebenen Satz von Zeichen auf den gewünschten
	 * Positionsbereich im übergebenen Passwort.
	 */
	public void distributeCharacters(final int count, final String characters, final int leftmost,
			final int rightmost) {
		if (rightmost > password.getLength() - 1) {
			throw new DomainException("TotalCharacterCountExceededMsg");
		}
		int possiblePositions = rightmost - leftmost + 1;
		if (password.charactersSet(leftmost, rightmost) + count > possiblePositions) {
			throw new DomainException("InvalidCharacterCountMsg");
		}
		for (int distributed = 0; distributed < count; distributed++) {
			int positionInRange;
			do { // Bis eine frisch zu besetzende Position besetzt wurde
				positionInRange = random.nextInt(possiblePositions); // Position ermitteln
			} while (!setRandomCharacter(characters, leftmost + positionInRange));
		}
	}

	/**
	 * Liefert das Password als Ergebnis des Fabrikationsprozesses, aufgefüllt mit Zeichen aus characters.
	 */
	public String getPassword(final String characters) {
		if (characters.length() == 0 || password.getLength() == 0) {
			throw new DomainException("CharacterOrTotalCharacterCountMissingMsg");
		} else {
			for (int i = 0; i < password.getLength(); i++) {
				setRandomCharacter(characters, i);
			}
			return password.getPassword();
		}
	}

}
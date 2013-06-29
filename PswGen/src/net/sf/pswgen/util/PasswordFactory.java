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

import java.util.Random;

/**
 * <p>
 * Eine Instanz dieser Klasse generiert ein Passwort. Zum Holen eines neuen Passworts sollte daher eine neue
 * Instanz erzeugt werden.
 * </p>
 * <p>
 * Copyright (C) 2005-2013 Uwe Damken
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
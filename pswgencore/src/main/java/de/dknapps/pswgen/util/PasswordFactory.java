/*
 * #%L
 * PswGen
 * %%
 * Copyright (C) 2005 - 2016 Uwe Damken
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.dknapps.pswgen.util;

import java.util.Random;

import de.dknapps.pswgen.model.ServiceInfo;

/**
 * <p>
 * Eine Instanz dieser Klasse generiert ein Passwort. Zum Holen eines neuen Passworts sollte daher eine neue
 * Instanz erzeugt werden.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid identisch, sprich kopiert.
 * </p>
 */
public class PasswordFactory {

	/** Der vom PasswordGenerator verwendete Zufallszahlengenerator */
	private static Random random = new Random();

	/** Das von dieser Instanz der PasswordFactory zu erzeugende Passwort */
	private Password password = null;

	/**
	 * Konstruktor für eine PasswordFactory, die genau ein Passwort erzeugt. Nach dem Konstruktor sollte null-
	 * bis mehrfach distributeCharacters() unter Verwendung der Konstanten dieser Klasse aufgerufen werden.
	 * Abschließend kann das Passwort mit getPassword abgeholt werden.
	 *
	 * Diese Methode ist package-private, um im Test, aber nirgendwo sonst zugreifbar zu sein.
	 */
	PasswordFactory(final int length) {
		password = new Password(length);
	}

	/**
	 * Führt setSeed auf dem verwendeten Zufallsgenerator aus und damit in der Folge zu wiederholbaren
	 * Ergebnisse und sollte daher nur für JUnit-Tests verwendet werden, dann aber auch direkt nach dem
	 * Konstruktor.
	 *
	 * Diese Methode ist package-private, um im Test, aber nirgendwo sonst zugreifbar zu sein.
	 */
	void setSeedForRandomToEnforceReproducableResults(final long seedForRandom) {
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
		}
		final int pos = random.nextInt(characters.length());
		password.setCharacterAt(new Character(characters.charAt(pos)), position);
		return true;
	}

	/**
	 * Verteilt die angegebene Anzahl von Zeichen aus dem übergebenen Satz von Zeichen auf den gewünschten
	 * Positionsbereich im übergebenen Passwort.
	 *
	 * Diese Methode ist package-private, um im Test, aber nirgendwo sonst zugreifbar zu sein.
	 */
	void distributeCharacters(final int count, final String characters, final int leftmost,
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
	 *
	 * Diese Methode ist package-private, um im Test, aber nirgendwo sonst zugreifbar zu sein.
	 */
	String getPassword(final String characters) {
		if (password.charactersSet(0, password.getLength() - 1) == password.getLength()) {
			// Wenn schon alle Zeichen gesetzt sind, muss nichts mehr aufgefüllt werden
		} else if (characters.length() == 0 || password.getLength() == 0) {
			throw new DomainException("CharacterOrTotalCharacterCountMissingMsg");
		} else {
			for (int i = 0; i < password.getLength(); i++) {
				setRandomCharacter(characters, i);
			}
		}
		return password.getPassword();
	}

	/**
	 * Liefert das im Dienst hinterlegte oder ein generiertes Passwort. Sobald entweder das Passwort oder das
	 * wiederholte Passwort im Dienst hinterlegt sind, müssen sie übereinstimmen, sonst wird eine Exception
	 * geworfen. Ein hinterlegtes Passwort hat also in jedem Fall Vorrang vor der Generierung.
	 */
	public static String getPassword(ServiceInfo si, String passphrase) {
		String password = si.getPassword();
		if (EmptyHelper.isEmpty(si.getPassword())) {
			password = getGeneratedPassword(si, passphrase);
		} else if (!password.equals(si.getPasswordRepeated())) { // Mismatch?
			throw new DomainException("PasswordMismatchMsg");
		}
		return password;
	}

	/**
	 * Generiert mit der übergebenen Passphrase ein Password gemäß den übergebenen Diensteinformationen. Für
	 * die Generierung wird zwingend ein Dienstekürzel zur Initialisierung des Zufallsgenerators benötigt.
	 * Wenn es nicht gesetzt ist, wird eine Exception geworfen.
	 */
	private static String getGeneratedPassword(ServiceInfo si, String passphrase) {
		String characters = ""; // Zeichen für den Rest des Passworts
		final String serviceAbbreviation = si.getServiceAbbreviation();
		if (EmptyHelper.isEmpty(serviceAbbreviation)) {
			throw new DomainException("ServiceAbbreviationEmptyMsg");
		}
		long seed = passphrase.hashCode() + serviceAbbreviation.hashCode();
		final String additionalInfo = si.getAdditionalInfo();
		if (!EmptyHelper.isEmpty(additionalInfo)) { // Zusatzinfos vorhanden?
			seed += additionalInfo.hashCode(); // => Zur Saat dazunehmen
		}
		final int pswLength = EmptyHelper.getValue(si.getTotalCharacterCount(), 0);
		PasswordFactory pg = new PasswordFactory(pswLength);
		pg.setSeedForRandomToEnforceReproducableResults(seed);
		if (si.isUseSmallLetters()) {
			int count = EmptyHelper.getValue(si.getSmallLettersCount(), 0);
			int start = EmptyHelper.getValue(si.getSmallLettersStartIndex(), 0);
			int end = EmptyHelper.getValue(si.getSmallLettersEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, Constants.LOWERCASE_LETTERS, start, end);
			} else {
				characters += Constants.LOWERCASE_LETTERS;
			}
		}
		if (si.isUseCapitalLetters()) {
			int count = EmptyHelper.getValue(si.getCapitalLettersCount(), 0);
			int start = EmptyHelper.getValue(si.getCapitalLettersStartIndex(), 0);
			int end = EmptyHelper.getValue(si.getCapitalLettersEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, Constants.UPPERCASE_LETTERS, start, end);
			} else {
				characters += Constants.UPPERCASE_LETTERS;
			}
		}
		if (si.isUseDigits()) {
			int count = EmptyHelper.getValue(si.getDigitsCount(), 0);
			int start = EmptyHelper.getValue(si.getDigitsStartIndex(), 0);
			int end = EmptyHelper.getValue(si.getDigitsEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, Constants.DIGITS, start, end);
			} else {
				characters += Constants.DIGITS;
			}
		}
		if (si.isUseSpecialCharacters()) {
			int count = EmptyHelper.getValue(si.getSpecialCharactersCount(), 0);
			int start = EmptyHelper.getValue(si.getSpecialCharactersStartIndex(), 0);
			int end = EmptyHelper.getValue(si.getSpecialCharactersEndIndex(), pswLength - 1);
			String specialCharacters = si.getSpecialCharacters();
			if (EmptyHelper.isEmpty(specialCharacters)) {
				specialCharacters = Constants.SPECIAL_CHARS;
			}
			if (count != 0) {
				pg.distributeCharacters(count, specialCharacters, start, end);
			} else {
				characters += specialCharacters;
			}
		}
		return pg.getPassword(characters); // Rest auffüllen
	}

	/**
	 * Liefert eine immer lesbare Erläuterung zum übergebenen Passwort, also ein String mit allen Zeichen,
	 * denen gegebenenfalls das jeweilige übergebene Präfix vorangestellt wird.
	 */
	public static String getPasswordExplanation(String password, String prefixLowercaseLetters,
			String prefixUppercaseLetters, String prefixDigits, String prefixSpecialChars) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < password.length(); i++) {
			if (i > 0) {
				sb.append(" ");
			}
			char c = password.charAt(i);
			String prefix;
			if (Constants.LOWERCASE_LETTERS.indexOf(c) >= 0) {
				prefix = prefixLowercaseLetters;
			} else if (Constants.UPPERCASE_LETTERS.indexOf(c) >= 0) {
				prefix = prefixUppercaseLetters;
			} else if (Constants.DIGITS.indexOf(c) >= 0) {
				prefix = prefixDigits;
			} else { // Sonderzeichen, muss nicht Constants.SPECIAL_CHARS sein, da einggebbar
				prefix = prefixSpecialChars;
			}
			if (prefix != null && !prefix.isEmpty()) {
				sb.append(prefix).append("-");
			}
			sb.append(c);
		}
		return sb.toString();
	}

}

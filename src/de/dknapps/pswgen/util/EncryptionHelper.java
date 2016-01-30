/*******************************************************************************
 * PswGen - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGen on your mobile  
 *
 *     Copyright (C) 2005, 2016 Uwe Damken
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
package de.dknapps.pswgen.util;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>
 * Verschlüsselt und entschlüsselt Strings auf Basis einer Passphrase.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid identisch, sprich kopiert.
 * </p>
 */
public class EncryptionHelper {

	/** Länge für das Salz für die Erzeugung eines Schlüssels aus der übergebenen Passphrase */
	private static final int SALT_LENGTH = 16;

	/** Anzahl der Durchläufe zum Erzeugen eines Schlüssels aus der Passphrase */
	private static final int KEY_ITERATION_COUNT = 1024;

	/** Verschlüsselungsalgorithmus/Ausgabemodus/Schlüsselverlängerungsmethode in einem String */
	private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

	/**
	 * Länge des Schlüssels, > 128 Bit müssen in der Oracle JRE die "Java Cryptography Extension (JCE)
	 * Unlimited Strength Jurisdiction Policy Files" installiert werden. Um dies zu vermeiden und weil das BSI
	 * auch AES-128 noch empfiehlt, belasse ich die PswGen-Verschlüsselung bei 128 Bit. Infos dazu:
	 * 
	 * http://stackoverflow.com/questions/6481627/java-security-illegal-key-size-or-default-parameters
	 * 
	 * https://www.bsi.bund.de/cae/servlet/contentblob/477256/publicationFile/30924/BSI-TR-02102_V1_0_pdf.pdf
	 */
	private static final int KEY_LENGTH = 128;

	/** Algorithmus zum Generieren eines Schlüssels aus der Passphrase */
	private static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";

	/** Algorithmus zum Verschlüsseln und Entschlüsseln der Daten */
	private static final String CIPHER_ALGORITHM = "AES";

	/** Für ältere Dateien zu verwendender Algorithmus für PBE (Password Based Encryption) */
	private static final String PREVIOUS_ENCRYPTION_ALGORITHM = "PBEWithMD5AndDES";

	/** Für ältere Dateien das Salz für die Erzeugung eines Schlüssels aus der Passphrase */
	private static final byte[] PREVIOUS_ENCRYPTION_SALT = { (byte) 0xc7, (byte) 0x73, (byte) 0x21,
			(byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };

	/** Für ältere Dateien die Anzahl der Durchläufe zum Erzeugen eines Schlüssels aus der Passphrase */
	private static final int PREVIOUS_KEY_ITERATION_COUNT = 20;

	/** Salz für das Erzeugen eines Schlüssels aus der übergebenen Passphrase */
	private byte[] salt;

	/** Initialisierungsvektor der Verschlüsselung oder für die Entschlüsselung */
	private byte[] initializer;

	/** Klasse für die Verschlüsselung oder Entschlüsselung */
	private Cipher cipher;

	/**
	 * Liefert einen EncryptionHelper zur Verschlüsselung
	 */
	public EncryptionHelper(final char[] passphrase) {
		try {
			SecureRandom secureRandom = new SecureRandom();
			salt = new byte[SALT_LENGTH];
			secureRandom.nextBytes(salt);
			KeySpec keySpec = new PBEKeySpec(passphrase, salt, KEY_ITERATION_COUNT, KEY_LENGTH);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), CIPHER_ALGORITHM));
			initializer = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
		} catch (Exception e) {
			throw new RuntimeException(
					"Exception beim Erzeugen einer ExceptionHelper-Instanz zum Verschlüsseln: "
							+ e.getMessage(),
					e);
		}
	}

	/**
	 * Liefert einen EncryptionHelper für die Entschlüsselung
	 */
	public EncryptionHelper(final char[] passphrase, final String saltAsHexString,
			final String initializerAsHexString) {
		try {
			if (saltAsHexString == null && initializerAsHexString == null) {
				initAsPreviouEncryptionHelper(passphrase);
				return;
			}
			salt = EncryptionHelper.toByteArray(saltAsHexString);
			initializer = EncryptionHelper.toByteArray(initializerAsHexString);
			KeySpec keySpec = new PBEKeySpec(passphrase, salt, KEY_ITERATION_COUNT, KEY_LENGTH);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(initializer));
		} catch (Exception e) {
			throw new RuntimeException(
					"Exception beim Erzeugen einer ExceptionHelper-Instanz zum Verschlüsseln: "
							+ e.getMessage(),
					e);
		}
	}

	/**
	 * @deprecated Liefert einen EncryptionHelper für die bisherige Entschlüsselung
	 */
	public void initAsPreviouEncryptionHelper(final char[] passphrase) throws Exception {
		PBEKeySpec keySpec = new PBEKeySpec(passphrase);
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PREVIOUS_ENCRYPTION_ALGORITHM);
		SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(PREVIOUS_ENCRYPTION_SALT,
				PREVIOUS_KEY_ITERATION_COUNT);
		cipher = Cipher.getInstance(PREVIOUS_ENCRYPTION_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParamSpec);
	}

	/**
	 * Verschlüsselt den String s und liefert das Ergebnis als Hex-String zurück.
	 */
	public String encrypt(final String s) {
		try {
			if (EmptyHelper.isEmpty(s)) { // Leer bleibt leer ...
				return "";
			}
			final byte[] encryptedByteArray = cipher.doFinal(s.getBytes(Constants.CHARSET_NAME));
			final String encrypted = EncryptionHelper.toHexString(encryptedByteArray);
			return encrypted;
		} catch (Exception e) {
			throw new RuntimeException("Exception beim Verschlüsseln: " + e.getMessage(), e);
		}
	}

	/**
	 * Entschlüsselt den übergebenen Hex-String sEncrypted und liefert das Ergebnis zurück.
	 */
	public String decrypt(final String sEncrypted) {
		try {
			if (EmptyHelper.isEmpty(sEncrypted)) { // Leer bleibt leer ...
				return "";
			}
			final byte[] sByteArray = cipher.doFinal(EncryptionHelper.toByteArray(sEncrypted));
			final String s = new String(sByteArray, Constants.CHARSET_NAME);
			return s;
		} catch (Exception e) {
			throw new DomainException("PassphraseInvalidMsg");
		}
	}

	/**
	 * Konvertiert ein Byte-Array in einen Hex-String mit zwei Hex-Zeichen je Byte.
	 */
	public static String toHexString(byte[] ba) {
		StringBuffer sb = new StringBuffer(ba.length * 2);
		for (byte element : ba) {
			sb.append(toHexString(element));
		}
		return sb.toString();
	}

	/**
	 * Konvertiert ein Byte in einen Hex-String mit zwei Hex-Zeichen (ggf. mit führender Null).
	 */
	public static String toHexString(byte a) {
		String s = "0" + Integer.toHexString((Byte.valueOf(a)).intValue());
		return s.substring(s.length() - 2).toUpperCase();
	}

	/**
	 * Konvertiert einen Hex-String mit zwei Hex-Zeichen je Byte in ein Byte-Array.
	 */
	public static byte[] toByteArray(String s) {
		byte[] ba = new byte[s.length() / 2];
		for (int i = 0; i < ba.length; i++) {
			ba[i] = toByte(s.substring(2 * i, 2 * i + 2));
		}
		return ba;
	}

	/**
	 * Konvertiert einen Hex-String mit zwei Hex-Zeichen (evtl. mit führender Null) in ein Byte.
	 */
	public static byte toByte(String s) {
		return (Integer.valueOf(Integer.parseInt(s, 16))).byteValue();
	}

	/**
	 * Liefert das Salz für die Erzeugung eines Schlüssels aus der Passphrase als Hex-String
	 */
	public String getSaltAsHexString() {
		return EncryptionHelper.toHexString(salt);
	}

	/**
	 * Liefert den Initialisierungsvektor der Verschlüsselung als Hex-String
	 */
	public String getInitializerAsHexString() {
		return EncryptionHelper.toHexString(initializer);
	}

}

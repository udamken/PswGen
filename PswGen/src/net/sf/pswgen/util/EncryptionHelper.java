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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * <p>
 * Verschlüsselt und entschlüsselt Strings auf Basis einer Passphrase.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid identisch, sprich kopiert.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class EncryptionHelper {

	/** Zu verwendender Algorithmus für PBE (Password Based Encryption) */
	private static final String ENCRYPTION_ALGORITHM = "PBEWithMD5AndDES";

	/** Salz für das Erzeugen eines Keys aus dem übergebenen Passwort */
	private static final byte[] ENCRYPTION_SALT = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
			(byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };

	/**
	 * Verschlüsselt den String s anhand der übergebenen Passphrase und liefert das Ergebnis als Hex-String
	 * zurück.
	 */
	public static String encrypt(final String passphrase, final String s) {
		try {
			if (s == null || s.length() == 0) { // Leer bleibt leer ...
				return "";
			}
			PBEKeySpec keySpec = new PBEKeySpec(passphrase.toCharArray());
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM);
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			PBEParameterSpec pbeParamSpec = new PBEParameterSpec(ENCRYPTION_SALT, 20);
			Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParamSpec);
			final byte[] encryptedByteArray = cipher.doFinal(s.getBytes());
			final String encrypted = EncryptionHelper.toHexString(encryptedByteArray);
			return encrypted;
		} catch (Exception e) {
			throw new RuntimeException("Exception beim Verschlüsseln: " + e.getMessage(), e);
		}
	}

	/**
	 * Entschlüsselt den übergebenen Hex-String sEncrypted anhand der übergebenen Passphrase.
	 */
	public static String decrypt(final String passphrase, final String sEncrypted) {
		try {
			if (sEncrypted == null || sEncrypted.length() == 0) { // Leer bleibt leer ...
				return "";
			}
			PBEKeySpec keySpec = new PBEKeySpec(passphrase.toCharArray());
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM);
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			PBEParameterSpec pbeParamSpec = new PBEParameterSpec(ENCRYPTION_SALT, 20);
			Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParamSpec);
			final byte[] pswByteArray = cipher.doFinal(EncryptionHelper.toByteArray(sEncrypted));
			final String psw = new String(pswByteArray);
			return psw;
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
		String s = "0" + Integer.toHexString((new Byte(a)).intValue());
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
		return (new Integer(Integer.parseInt(s, 16))).byteValue();
	}

}

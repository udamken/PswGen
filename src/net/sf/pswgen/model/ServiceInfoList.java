package net.sf.pswgen.model;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2015 Uwe Damken

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.pswgen.util.Constants;
import net.sf.pswgen.util.DomainException;
import net.sf.pswgen.util.EmptyHelper;
import net.sf.pswgen.util.EncryptionHelper;

/**
 * <p>
 * Hält eine Zuordnung von Dienstekürzeln zu zugehörigen Informationen, die zum Generieren eines Passworts
 * nötig sind.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid fast (letzteres ohne JAXB) identisch, sprich kopiert.
 * </p>
 * <p>
 * Copyright (C) 2005-2015 Uwe Damken
 * </p>
 */
public class ServiceInfoList {

	/** Version von PswGen, mit der die Dienstedatei erstellt wurde */
	private String version;

	/** Verlüsselter String zur Verifizierung der eingegebenen Passphrase */
	private String encryptedVerifier;

	/** Salz für die Erzeugung eines Schlüssels aus der Passphrase als Hex-String */
	private String saltAsHexString;

	/** Initialisierungsvektor für die Verschlüsselung als Hex-String */
	private String initializerAsHexString;

	/** Die Werte aus der zu Grunde liegenden Map als Collection */
	private Collection<ServiceInfo> encryptedServices;

	/** Die hinter dieser ServiceInfoList liegende Map */
	private SortedMap<String, ServiceInfo> services;

	/**
	 * Erzeugt eine neue ServiceInfoList ohne Dienste
	 */
	public ServiceInfoList() {
		services = new TreeMap<String, ServiceInfo>();
	}

	/**
	 * Ein Dienstekürzel und die zugehörigen Informationen zufügen.
	 */
	public ServiceInfo putServiceInfo(final ServiceInfo si) {
		return services.put(si.getServiceAbbreviation(), si);
	}

	/**
	 * Liefert eine Kopie der übergebenen Dienstbeschreibung mit verschlüsselten Strings zurück.
	 */
	private ServiceInfo encrypt(EncryptionHelper encryptionHelper, final ServiceInfo d) {
		ServiceInfo e = new ServiceInfo(); // d(ecrypt) --encrypt--> e(ncrypt)
		e.setServiceAbbreviation(encryptionHelper.encrypt(d.getServiceAbbreviation()));
		e.setAdditionalInfo(encryptionHelper.encrypt(d.getAdditionalInfo()));
		e.setLoginUrl(encryptionHelper.encrypt(d.getLoginUrl()));
		e.setLoginInfo(encryptionHelper.encrypt(d.getLoginInfo()));
		e.setAdditionalLoginInfo(encryptionHelper.encrypt(d.getAdditionalLoginInfo()));
		e.setUseSmallLetters(d.isUseSmallLetters());
		e.setUseCapitalLetters(d.isUseCapitalLetters());
		e.setUseDigits(d.isUseDigits());
		e.setUseSpecialCharacters(d.isUseSpecialCharacters());
		e.setSpecialCharacters(encryptionHelper.encrypt(d.getSpecialCharacters()));
		e.setSmallLettersCount(d.getSmallLettersCount());
		e.setSmallLettersStartIndex(d.getSmallLettersStartIndex());
		e.setSmallLettersEndIndex(d.getSmallLettersEndIndex());
		e.setCapitalLettersCount(d.getCapitalLettersCount());
		e.setCapitalLettersStartIndex(d.getCapitalLettersStartIndex());
		e.setCapitalLettersEndIndex(d.getCapitalLettersEndIndex());
		e.setDigitsCount(d.getDigitsCount());
		e.setSpecialCharactersCount(d.getSpecialCharactersCount());
		e.setDigitsStartIndex(d.getDigitsStartIndex());
		e.setDigitsEndIndex(d.getDigitsEndIndex());
		e.setSpecialCharactersStartIndex(d.getSpecialCharactersStartIndex());
		e.setSpecialCharactersEndIndex(d.getSpecialCharactersEndIndex());
		e.setTotalCharacterCount(d.getTotalCharacterCount());
		e.setPassword(encryptionHelper.encrypt(d.getPassword()));
		e.setPasswordRepeated(encryptionHelper.encrypt(d.getPasswordRepeated()));
		return e;
	}

	/**
	 * Liefert die Informationen zu einem Dienstekürzel.
	 */
	public ServiceInfo getServiceInfo(final String serviceAbbreviation) {
		return services.get(serviceAbbreviation);
	}

	/**
	 * Liefert eine Kopie der übergebenen Dienstbeschreibung mit entschlüsselten Strings zurück.
	 */
	private ServiceInfo decrypt(EncryptionHelper encryptionHelper, final ServiceInfo e) {
		ServiceInfo d = new ServiceInfo(); // e(ncrypt) --decrypt--> d(ecrypt)
		d.setServiceAbbreviation(encryptionHelper.decrypt(e.getServiceAbbreviation()));
		d.setAdditionalInfo(encryptionHelper.decrypt(e.getAdditionalInfo()));
		d.setLoginUrl(encryptionHelper.decrypt(e.getLoginUrl()));
		d.setLoginInfo(encryptionHelper.decrypt(e.getLoginInfo()));
		d.setAdditionalLoginInfo(encryptionHelper.decrypt(e.getAdditionalLoginInfo()));
		d.setUseSmallLetters(e.isUseSmallLetters());
		d.setUseCapitalLetters(e.isUseCapitalLetters());
		d.setUseDigits(e.isUseDigits());
		d.setUseSpecialCharacters(e.isUseSpecialCharacters());
		d.setSpecialCharacters(encryptionHelper.decrypt(e.getSpecialCharacters()));
		d.setSmallLettersCount(e.getSmallLettersCount());
		d.setSmallLettersStartIndex(e.getSmallLettersStartIndex());
		d.setSmallLettersEndIndex(e.getSmallLettersEndIndex());
		d.setCapitalLettersCount(e.getCapitalLettersCount());
		d.setCapitalLettersStartIndex(e.getCapitalLettersStartIndex());
		d.setCapitalLettersEndIndex(e.getCapitalLettersEndIndex());
		d.setDigitsCount(e.getDigitsCount());
		d.setSpecialCharactersCount(e.getSpecialCharactersCount());
		d.setDigitsStartIndex(e.getDigitsStartIndex());
		d.setDigitsEndIndex(e.getDigitsEndIndex());
		d.setSpecialCharactersStartIndex(e.getSpecialCharactersStartIndex());
		d.setSpecialCharactersEndIndex(e.getSpecialCharactersEndIndex());
		d.setTotalCharacterCount(e.getTotalCharacterCount());
		d.setPassword(encryptionHelper.decrypt(e.getPassword()));
		d.setPasswordRepeated(encryptionHelper.decrypt(e.getPasswordRepeated()));
		return d;
	}

	/**
	 * Löscht die Informationen zu einem Dienstekürzel.
	 */
	public ServiceInfo removeServiceInfo(final String serviceAbbreviation) {
		return services.remove(serviceAbbreviation);
	}

	/**
	 * Liefert die Informationen zu allen Dienstekürzeln.
	 */
	public Collection<ServiceInfo> getServices() {
		return services.values();
	}

	/**
	 * Bereitet das Speichern dieser ServiceInfoList in eine Datei vor, indem die Werte aus der Map in eine
	 * Collection gestellt werden. Die Collection enthält nur verschlüsselte Daten und die Map nur
	 * enstschlüsselte Daten. Die Umwandlung geschieht beim Befüllen der Collection aus der Map beim Speichern
	 * und beim Befüllen der Map aus der Collection beim Lesen.
	 */
	public void encrypt(EncryptionHelper encryptionHelper) {
		encryptedVerifier = encryptionHelper.encrypt(Constants.APPLICATION_VERIFIER);
		encryptedServices = new ArrayList<ServiceInfo>();
		if (services != null) {
			for (ServiceInfo si : services.values()) {
				encryptedServices.add(encrypt(encryptionHelper, si));
			}
		}
	}

	/**
	 * Fügt der Collection verschlüsselter Dienste einen hinzu.
	 */
	public void addEncryptedService(ServiceInfo si) {
		if (encryptedServices == null) {
			encryptedServices = new ArrayList<ServiceInfo>();
		}
		encryptedServices.add(si);
	}

	/**
	 * Bereitet das Laden dieser ServiceInfoList aus einer Datei nach, indem die Werte aus der Collection
	 * wieder in die Map gestellt werden. Die Collection enthält nur verschlüsselte Daten und die Map nur
	 * enstschlüsselte Daten, die Umwandlung geschieht beim Befüllen der Collection aus der Map beim Speichern
	 * und beim Befüllen der Map aus der Collection beim Lesen.
	 */
	public void decrypt(EncryptionHelper encryptionHelper) {
		String decryptedVerifier = encryptionHelper.decrypt(encryptedVerifier);
		if (!decryptedVerifier.equals(Constants.APPLICATION_VERIFIER)) {
			throw new DomainException("PassphraseInvalidMsg");
		}
		if (encryptedServices != null) {
			for (ServiceInfo serviceInfo : encryptedServices) {
				putServiceInfo(decrypt(encryptionHelper, serviceInfo));
			}
		}
	}

	/**
	 * Liefert true, wenn die geladene Datei keine Dienste enthält.
	 */
	public boolean isNew() {
		return encryptedServices == null;
	}

	/**
	 * Liefert true, wenn die Version gesetzt und größer oder gleich der aktuellen Dateiformatsversion
	 * (ADVANCED_FILE_FORMAT_VERSION) ist und außerdem der Prüfstring auf einen nicht leeren Wert gesetzt ist.
	 */
	public boolean isAdvancedFormat() {
		return version != null && version.compareTo(Constants.ADVANCED_FILE_FORMAT_VERSION) >= 0
				&& !EmptyHelper.isEmpty(encryptedVerifier);
	}

	/**
	 * Liefert true, wenn die Version leer oder kleiner als die niedrigste (ggf. mit Upgrade) unterstützte
	 * Dateiformatsversion (LOWEST_SUPPORTED_FILE_FORMAT_VERSION) ist oder der Prüfstring fehlt, aber die
	 * Datei nicht leer ist.
	 */
	public boolean isUnsupportedFormat() {
		return version == null || version.compareTo(Constants.LOWEST_SUPPORTED_FILE_FORMAT_VERSION) < 0
				|| EmptyHelper.isEmpty(encryptedVerifier);
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the encryptedVerifier
	 */
	public String getEncryptedVerifier() {
		return encryptedVerifier;
	}

	/**
	 * @param encryptedVerifier
	 *            the encryptedVerifier to set
	 */
	public void setEncryptedVerifier(String verifier) {
		this.encryptedVerifier = verifier;
	}

	/**
	 * @return the encryptedServices
	 */
	public Collection<ServiceInfo> getEncryptedServices() {
		return encryptedServices;
	}

	/**
	 * @return the saltAsHexString
	 */
	public String getSaltAsHexString() {
		return saltAsHexString;
	}

	/**
	 * @param saltAsHexString
	 *            the saltAsHexString to set
	 */
	public void setSaltAsHexString(String saltAsHexString) {
		this.saltAsHexString = saltAsHexString;
	}

	/**
	 * @return the initializerAsHexString
	 */
	public String getInitializerAsHexString() {
		return initializerAsHexString;
	}

	/**
	 * @param initializerAsHexString
	 *            the initializerAsHexString to set
	 */
	public void setInitializerAsHexString(String initializerAsHexString) {
		this.initializerAsHexString = initializerAsHexString;
	}

}

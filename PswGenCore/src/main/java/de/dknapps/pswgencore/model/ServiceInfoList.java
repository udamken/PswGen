/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2018 Uwe Damken
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
package de.dknapps.pswgencore.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import de.dknapps.pswgencore.util.DomainException;
import de.dknapps.pswgencore.util.EncryptionHelper;

/**
 * <p>
 * Hält eine Zuordnung von Dienstekürzeln zu zugehörigen Informationen, die zum Generieren eines Passworts
 * nötig sind.
 * </p>
 */
public class ServiceInfoList {

	/** Version von PswGen, mit der die Dienstedatei erstellt wurde */
	private String version;

	/** String zur Verifizierung der Passphrase, alt: verschlüsselter String, neu: services.hashCode() */
	private String verifier;

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
	public void putServiceInfo(final ServiceInfo si) {
		services.put(si.getServiceAbbreviation(), si);
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
		e.setUseOldPassphrase(d.isUseOldPassphrase());
		e.setDeleted(d.isDeleted());
		e.setTimeMillis(d.getTimeMillis());
		return e;
	}

	/**
	 * Liefert die Informationen zu einem Dienstekürzel.
	 */
	public ServiceInfo getServiceInfo(final String serviceAbbreviation) {
		ServiceInfo si = services.get(serviceAbbreviation);
		return (si == null || si.isDeleted()) ? null : si;
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
		d.setUseOldPassphrase(e.isUseOldPassphrase());
		d.setDeleted(e.isDeleted());
		d.setTimeMillis(e.getTimeMillis());
		return d;
	}

	/**
	 * Wenn es keinen Dienst mit dem Dienstekürzel gibt, wird eine Exception geworfen. Anderenfalls wird der
	 * Dienst als gelöscht markiert und dadurch beim getServices(false) nicht mehr mitgeliefert.
	 */
	public void removeServiceInfo(final String serviceAbbreviation) {
		ServiceInfo si = getServiceInfo(serviceAbbreviation);
		if (si == null) {
			throw new DomainException("ServiceAbbreviationMissingMsg");
		}
		si.setDeleted(true);
		si.resetTimeMillis();
	}

	/**
	 * Liefert die Informationen zu allen Dienstekürzeln, die nicht als gelöscht markiert sind.
	 */
	public List<ServiceInfo> getServices() {
		return getServices(false);
	}

	/**
	 * Liefert die Informationen zu allen Dienstekürzeln, ggf. auch mit den als gelöscht markierten Einträgen.
	 */
	protected List<ServiceInfo> getServices(boolean withDeletedEntries) {
		List<ServiceInfo> result = new ArrayList<>();
		for (ServiceInfo si : services.values()) {
			if (withDeletedEntries || !si.isDeleted()) {
				result.add(si);
			}
		}
		return result;
	}

	/**
	 * Bereitet das Speichern dieser ServiceInfoList in eine Datei vor, indem die Werte aus der Map in eine
	 * Collection gestellt werden. Die Collection enthält nur verschlüsselte Daten und die Map nur
	 * enstschlüsselte Daten. Die Umwandlung geschieht beim Befüllen der Collection aus der Map beim Speichern
	 * und beim Befüllen der Map aus der Collection beim Lesen.
	 */
	public void encrypt(EncryptionHelper encryptionHelper) {
		verifier = String.valueOf(services.hashCode()); // Zur Prüfung der Korrektheit nach dem Einlesen
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
		if (encryptedServices != null) {
			for (ServiceInfo serviceInfo : encryptedServices) {
				putServiceInfo(decrypt(encryptionHelper, serviceInfo));
			}
		}
		// Zur Überprüfung der Passphrase wird der hashCode() der Services-Map nach dem Entschlüsseln mit dem
		// hashCode() verglichen, der vor dem Verschlüsseln ermittelt wurde.
		if (!verifier.equals(String.valueOf(services.hashCode()))) {
			throw new DomainException("PassphraseInvalidMsg");
		}
	}

	/**
	 * Liefert true, wenn es mindestens einen Service gibt, bei dem isUseOldPassphrase() true liefert.
	 */
	public boolean containsServiceWithOldPassphrase() {
		for (ServiceInfo si : encryptedServices) {
			if (si.isUseOldPassphrase()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Arbeite die Dienste der anderen Liste in diese List unter Berücksichtigung von Löschkennzeichen und
	 * Timestamp ein.
	 */
	public void merge(ServiceInfoList other) {
		for (ServiceInfo otherSi : other.services.values()) {
			ServiceInfo si = services.get(otherSi.getServiceAbbreviation());
			if (si == null || si.olderThan(otherSi)) {
				// Nicht vorhandenen Dienst ergänzen oder veralteten Dienst aktualisieren
				services.put(otherSi.getServiceAbbreviation(), otherSi);
			}
		}
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
		return verifier;
	}

	/**
	 * @param encryptedVerifier
	 *            the encryptedVerifier to set
	 */
	public void setEncryptedVerifier(String verifier) {
		this.verifier = verifier;
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

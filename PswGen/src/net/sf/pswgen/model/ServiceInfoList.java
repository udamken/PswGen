package net.sf.pswgen.model;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.pswgen.util.Constants;
import net.sf.pswgen.util.EncryptionHelper;

/**
 * <p>
 * Hält eine Zuordnung von Dienstekürzeln zu zugehörigen Informationen, die zum Generieren eines Passworts
 * nötig sind.
 * </p>
 * <p>
 * Copyright (C) 2005-2013 Uwe Damken
 * </p>
 */
@XmlRootElement(name = "ServiceList")
public class ServiceInfoList {

	/** Version von PswGen, mit der die XML-Datei erstellt wurde */
	private String version;

	/** String zur Verifizierung der eingegebenen Passphrase */
	private String verifier;

	/** Die Werte aus der zu Grunde liegenden Map als Collection, JAXB kann "nur" Collections */
	@XmlElementWrapper(name = "Services")
	@XmlElement(name = "Service")
	private Collection<ServiceInfo> encryptedServices;

	/** Die hinter dieser ServiceInfoList liegende HashMap */
	private TreeMap<String, ServiceInfo> services;

	/**
	 * Ein Dienstekürzel und die zugehörigen Informationen zufügen.
	 */
	public ServiceInfo putServiceInfo(final ServiceInfo si) {
		return services.put(si.getServiceAbbreviation(), si);
	}

	/**
	 * Liefert eine Kopie der übergebenen Dienstbeschreibung mit verschlüsselten Strings zurück.
	 */
	private ServiceInfo encrypt(final String passphrase, final ServiceInfo d) {
		ServiceInfo e = new ServiceInfo(); // d(ecrypt) --encrypt--> e(ncrypt)
		e.setServiceAbbreviation(EncryptionHelper.encrypt(passphrase, d.getServiceAbbreviation()));
		e.setAdditionalInfo(EncryptionHelper.encrypt(passphrase, d.getAdditionalInfo()));
		e.setLoginUrl(EncryptionHelper.encrypt(passphrase, d.getLoginUrl()));
		e.setLoginInfo(EncryptionHelper.encrypt(passphrase, d.getLoginInfo()));
		e.setAdditionalLoginInfo(EncryptionHelper.encrypt(passphrase, d.getAdditionalLoginInfo()));
		e.setUseSmallLetters(d.isUseSmallLetters());
		e.setUseCapitalLetters(d.isUseCapitalLetters());
		e.setUseDigits(d.isUseDigits());
		e.setUseSpecialCharacters(d.isUseSpecialCharacters());
		e.setSpecialCharacters(EncryptionHelper.encrypt(passphrase, d.getSpecialCharacters()));
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
		e.setPassword(EncryptionHelper.encrypt(passphrase, d.getPassword()));
		e.setPasswordRepeated(EncryptionHelper.encrypt(passphrase, d.getPasswordRepeated()));
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
	private ServiceInfo decrypt(final String passphrase, final ServiceInfo e) {
		ServiceInfo d = new ServiceInfo(); // e(ncrypt) --decrypt--> d(ecrypt)
		d.setServiceAbbreviation(EncryptionHelper.decrypt(passphrase, e.getServiceAbbreviation()));
		d.setAdditionalInfo(EncryptionHelper.decrypt(passphrase, e.getAdditionalInfo()));
		d.setLoginUrl(EncryptionHelper.decrypt(passphrase, e.getLoginUrl()));
		d.setLoginInfo(EncryptionHelper.decrypt(passphrase, e.getLoginInfo()));
		d.setAdditionalLoginInfo(EncryptionHelper.decrypt(passphrase, e.getAdditionalLoginInfo()));
		d.setUseSmallLetters(e.isUseSmallLetters());
		d.setUseCapitalLetters(e.isUseCapitalLetters());
		d.setUseDigits(e.isUseDigits());
		d.setUseSpecialCharacters(e.isUseSpecialCharacters());
		d.setSpecialCharacters(EncryptionHelper.decrypt(passphrase, e.getSpecialCharacters()));
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
		d.setPassword(EncryptionHelper.decrypt(passphrase, e.getPassword()));
		d.setPasswordRepeated(EncryptionHelper.decrypt(passphrase, e.getPasswordRepeated()));
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
	 * Bereitet das Speichern dieser ServiceInfoList in eine XML-Datei vor, indem die Werte aus der Map in
	 * eine Collection gestellt werden. Der Grund dafür ist, dass JAXB nicht mit Maps, sondern "nur" mit
	 * Collections umgehen kann. Allerdings wird der Umweg hier ausgenutzt: Die Collection enthält nur
	 * verschlüsselte Daten und die Map nur enstschlüsselte Daten. Die Umwandlung geschieht beim Befüllen der
	 * Collection aus der Map beim Speichern und beim Befüllen der Map aus der Collection beim Lesen.
	 */
	public void encrypt(final String passphrase) {
		encryptedServices = new ArrayList<ServiceInfo>();
		if (services != null) {
			for (ServiceInfo si : services.values()) {
				encryptedServices.add(encrypt(passphrase, si));
			}
		}
	}

	/**
	 * Fügt den Collection verschlüsselter Dienste einen hinzu. Hierfür wird nicht setEncryptServices()
	 * definiert, weil das mit der JAXB-Implementierung kollidiert.
	 */
	public void addEncryptedService(ServiceInfo si) {
		if (encryptedServices == null) {
			encryptedServices = new ArrayList<ServiceInfo>();
		}
		encryptedServices.add(si);
	}

	/**
	 * Bereitet das Laden dieser ServiceInfoList aus einer XML-Datei nach, indem die Werte aus der Collection
	 * wieder in die Map gestellt werden. Der Umweg ist nötig, weil JAXB nur mit Collections, nicht aber mit
	 * Maps umgehen kann. Allerdings wird der Umweg hier ausgenutzt: Die Collection nur verschlüsselte Daten
	 * enthält und die Map nur enstschlüsselte Daten, die Umwandlung geschieht beim Befüllen der Collection
	 * aus der Map beim Speichern und beim Befüllen der Map aus der Collection beim Lesen.
	 */
	public void decrypt(final String passphrase) {
		services = new TreeMap<String, ServiceInfo>();
		if (encryptedServices != null) {
			for (ServiceInfo serviceInfo : encryptedServices) {
				putServiceInfo(decrypt(passphrase, serviceInfo));
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
	 * Liefert true, wenn die Version gesetzt und >= 1.7.0 ist und außerdem der Prüfstring auf einen nicht
	 * leeren Wert gesetzt ist.
	 */
	public boolean isAdvancedFormat() {
		return version != null && version.compareTo(Constants.ADVANCED_FILE_FORMAT_VERSION) >= 0
				&& verifier != null && verifier.length() > 0;
	}

	/**
	 * Liefert true, wenn die Version leer oder <= 1.5.9 ist oder der Prüfstring fehlt, aber die Datei nicht
	 * leer ist.
	 */
	public boolean isUnsupportedFormat() {
		return version == null || version.compareTo(Constants.UNSUPPORTED_FILE_FORMAT_VERSION) <= 0
				|| verifier == null || verifier.isEmpty();
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
	 * @return the verifier
	 */
	public String getVerifier() {
		return verifier;
	}

	/**
	 * @param verifier
	 *            the verifier to set
	 */
	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	/**
	 * @return the encryptedServices
	 */
	public Collection<ServiceInfo> getEncryptedServices() {
		return encryptedServices;
	}

}

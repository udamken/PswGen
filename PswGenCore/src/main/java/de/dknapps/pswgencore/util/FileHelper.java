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
package de.dknapps.pswgencore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.logging.Logger;

import de.dknapps.pswgencore.CoreConstants;
import de.dknapps.pswgencore.model.ServiceInfo;
import de.dknapps.pswgencore.model.ServiceInfoList;

/**
 * <p>
 * FileHelper ist ein Singleton und hilft dabei, Dienstedaten im JSON-Format zu speichern und zu laden.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid bis auf die JSON-Importe identisch, sprich kopiert.
 * </p>
 */
public class FileHelper {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(CoreConstants.LOGGER_NAME);

	/** Die eine und einzige Instanz dieser Klasse */
	private static FileHelper instance = null;

	private CommonJsonReaderWriterFactory commonJsonReaderWriterFactory;

	/**
	 * Singleton => privater Konstruktor
	 */
	private FileHelper(CommonJsonReaderWriterFactory commonJsonReaderWriterFactory) {
		this.commonJsonReaderWriterFactory = commonJsonReaderWriterFactory;
	}

	/**
	 * Liefert die eine und einzige Instanz.
	 */
	public static synchronized FileHelper getInstance(
			CommonJsonReaderWriterFactory commonJsonReaderWriterFactory) {
		if (instance == null) {
			instance = new FileHelper(commonJsonReaderWriterFactory);
		}
		return instance;
	}

	/**
	 * Lädt eine gemischte Liste der Dienste aus beiden Dateien.
	 */
	public ServiceInfoList loadServiceInfoList(File servicesFile, File otherServicesFile, String passphrase) {
		ServiceInfoList services = loadServiceInfoList(servicesFile, passphrase);
		ServiceInfoList otherServices = loadServiceInfoList(otherServicesFile, passphrase);
		services.merge(otherServices);
		return services;
	}

	/**
	 * Lädt die Liste aller Dienste aus der angegebenen Datei und entschlüsselt sie. Wenn die Datei (noch)
	 * nicht existiert, wird eine leere Diensteliste zurückgegeben. Wenn die Datei in einem nicht mehr
	 * unterstützten Format vorliegt, wird eine DomainException geworfen. Bei einer älteren, aber noch
	 * lesbaren Version, wird die alte Datei umbenannt und die Dienste werden im neuen Format gespeichert.
	 */
	private ServiceInfoList loadServiceInfoList(File servicesFile, String passphrase) {
		try {

			// Leere Diensteliste zurückgegeben, wenn die Datei (noch) nicht existiert
			if (servicesFile == null || !servicesFile.exists()) {
				return new ServiceInfoList();
			}

			// JSON-Inhalte aus er Datei einlesen, bei veralteten Versionen wird eine Exception geworfen
			FileInputStream in = new FileInputStream(servicesFile);
			ServiceInfoList services = readJsonStream(in, servicesFile.lastModified());

			// Datei konvertieren, wenn die Version nicht aktuell ist, aber noch untersützt wird
			if (services.getVersion().compareTo(CoreConstants.NEWEST_FILE_FORMAT_VERSION) < 0) {

				// Alte Datei zur Sicherheit durch Umbenennen aufbewahren
				servicesFile.renameTo(new File(servicesFile.getPath() + ".upgraded"));

				// Services in eine neue Datei im neuen Format (mit Timestamp je Service) speichern
				saveServiceInfoList(servicesFile, services);

				// Die frisch geschriebene Datei erneut einlesen und das Ergebnis davon zurückgeben
				return loadServiceInfoList(servicesFile, passphrase);
			}

			// Zum Abschluss alles entschlüsselt von der Liste in eine Map übertragen
			EncryptionHelper encryptionHelper = new EncryptionHelper(passphrase.toCharArray(),
					services.getSaltAsHexString(), services.getInitializerAsHexString());
			services.decrypt(encryptionHelper);
			return services;

		} catch (IOException e) {
			throw new DomainException("FileCouldNotBeOpenedMsg", e);
		}
	}

	private ServiceInfoList readJsonStream(FileInputStream in, long lastModified) throws IOException {
		ServiceInfoList services = new ServiceInfoList();
		CommonJsonReader reader = commonJsonReaderWriterFactory
				.getJsonReader(new InputStreamReader(in, CoreConstants.CHARSET_NAME));
		try {
			reader.beginObject();
			checkJsonName(reader, "version");
			services.setVersion(reader.nextString());

			if (services.getVersion().compareTo(CoreConstants.LOWEST_SUPPORTED_FILE_FORMAT_VERSION) < 0) {
				throw new DomainException("UnsupportedFileFormatMsg");
			}

			checkJsonName(reader, "verifier");
			services.setEncryptedVerifier(reader.nextString());

			addReadServices(services, reader, lastModified);

			checkJsonName(reader, "salt");
			services.setSaltAsHexString(reader.nextString());
			checkJsonName(reader, "initializer");
			services.setInitializerAsHexString(reader.nextString());
			reader.endObject();
		} finally {
			reader.close();
		}
		return services;
	}

	private void addReadServices(ServiceInfoList services, CommonJsonReader reader, long lastModified)
			throws IOException {
		checkJsonName(reader, "services");
		reader.beginArray();
		while (reader.hasNext()) {
			services.addEncryptedService(readService(reader, lastModified));
		}
		reader.endArray();
	}

	private ServiceInfo readService(CommonJsonReader reader, long lastModified) throws IOException {
		ServiceInfo si = new ServiceInfo();
		reader.beginObject();
		checkJsonName(reader, "serviceAbbreviation");
		si.setServiceAbbreviation(reader.nextString());
		checkJsonName(reader, "additionalInfo");
		si.setAdditionalInfo(reader.nextString());
		checkJsonName(reader, "loginUrl");
		si.setLoginUrl(reader.nextString());
		checkJsonName(reader, "loginInfo");
		si.setLoginInfo(reader.nextString());
		checkJsonName(reader, "additionalLoginInfo");
		si.setAdditionalLoginInfo(reader.nextString());
		checkJsonName(reader, "useSmallLetters");
		si.setUseSmallLetters(reader.nextBoolean());
		checkJsonName(reader, "useCapitalLetters");
		si.setUseCapitalLetters(reader.nextBoolean());
		checkJsonName(reader, "useDigits");
		si.setUseDigits(reader.nextBoolean());
		checkJsonName(reader, "useSpecialCharacters");
		si.setUseSpecialCharacters(reader.nextBoolean());
		checkJsonName(reader, "specialCharacters");
		si.setSpecialCharacters(reader.nextString());
		checkJsonName(reader, "smallLettersCount");
		si.setSmallLettersCount(reader.nextInt());
		checkJsonName(reader, "smallLettersStartIndex");
		si.setSmallLettersStartIndex(reader.nextInt());
		checkJsonName(reader, "smallLettersEndIndex");
		si.setSmallLettersEndIndex(reader.nextInt());
		checkJsonName(reader, "capitalLettersCount");
		si.setCapitalLettersCount(reader.nextInt());
		checkJsonName(reader, "capitalLettersStartIndex");
		si.setCapitalLettersStartIndex(reader.nextInt());
		checkJsonName(reader, "capitalLettersEndIndex");
		si.setCapitalLettersEndIndex(reader.nextInt());
		checkJsonName(reader, "digitsCount");
		si.setDigitsCount(reader.nextInt());
		checkJsonName(reader, "specialCharactersCount");
		si.setSpecialCharactersCount(reader.nextInt());
		checkJsonName(reader, "digitsStartIndex");
		si.setDigitsStartIndex(reader.nextInt());
		checkJsonName(reader, "digitsEndIndex");
		si.setDigitsEndIndex(reader.nextInt());
		checkJsonName(reader, "specialCharactersStartIndex");
		si.setSpecialCharactersStartIndex(reader.nextInt());
		checkJsonName(reader, "specialCharactersEndIndex");
		si.setSpecialCharactersEndIndex(reader.nextInt());
		checkJsonName(reader, "totalCharacterCount");
		si.setTotalCharacterCount(reader.nextInt());
		checkJsonName(reader, "password");
		si.setPassword(reader.nextString());
		checkJsonName(reader, "passwordRepeated");
		si.setPasswordRepeated(reader.nextString());
		if (reader.peekReturnsEndObject()) {
			si.setUseOldPassphrase(false);
		} else {
			// Ab 1.8.0 optional ohne ein neues Dateiformat zu begründen
			checkJsonName(reader, "useOldPassphrase");
			si.setUseOldPassphrase(reader.nextBoolean());
		}
		if (reader.peekReturnsEndObject()) {
			si.setDeleted(false);
			si.setTimeMillis(String.valueOf(lastModified));
		} else {
			// Erst ab 2.0.0 gibt es deleted und timeMillis, daher ist beides optional
			checkJsonName(reader, "deleted");
			si.setDeleted(reader.nextBoolean());
			checkJsonName(reader, "timeMillis");
			si.setTimeMillis(reader.nextString());
		}
		reader.endObject();
		return si;
	}

	/**
	 * Prüft den nächsten am Reader vorliegenden Elementnamen und wirft bei einer Abweichung eine Exception.
	 */
	private void checkJsonName(CommonJsonReader reader, String expectedName) throws IOException {
		String actualName = reader.nextName();
		if (!expectedName.equals(actualName)) {
			throw new IOException(
					"Json name mismatch, expected=<" + expectedName + ">, actual=<" + actualName + ">");
		}
	}

	/**
	 * Verschlüsselt und speichert alle Diensteinformationen.
	 */
	public void saveServiceInfoList(File servicesFile, ServiceInfoList services, String passphrase)
			throws IOException {
		EncryptionHelper encryptionHelper = new EncryptionHelper(passphrase.toCharArray());
		services.setSaltAsHexString(encryptionHelper.getSaltAsHexString());
		services.setInitializerAsHexString(encryptionHelper.getInitializerAsHexString());
		services.encrypt(encryptionHelper);
		saveServiceInfoList(servicesFile, services);
	}

	/**
	 * Speichert alle Diensteinformationen.
	 */
	private void saveServiceInfoList(File servicesFile, ServiceInfoList services) throws IOException {
		FileOutputStream out = new FileOutputStream(servicesFile);
		writeJsonStream(out, services);
		out.close();
	}

	private void writeJsonStream(OutputStream out, ServiceInfoList services) throws IOException {
		CommonJsonWriter writer = commonJsonReaderWriterFactory
				.getJsonWriter(new OutputStreamWriter(out, CoreConstants.CHARSET_NAME));
		writer.setIndent("\t");
		writer.beginObject();
		services.setVersion(CoreConstants.APPLICATION_VERSION);
		writer.name("version").value(services.getVersion());
		writer.name("verifier").value(services.getEncryptedVerifier());
		writeServices(writer, services.getEncryptedServices());
		/**
		 * <pre>
		 * http://stackoverflow.com/questions/13901529/symmetric-encryption-aes-is-saving-the-iv-and-salt-alongside-the-encrypted-da
		 * 
		 * Storing the IV and Salt a long with the cipher text is proper and a best practice. Hard coding the
		 * salt is not useful, being random is important, hard coding the iterations is perfectly okay but is
		 * typically much higher than 300 (in fact at least 1000 and you typically go much higher if your
		 * machine/usage can handle it).
		 * </pre>
		 */
		writer.name("salt").value(services.getSaltAsHexString());
		writer.name("initializer").value(services.getInitializerAsHexString());
		writer.endObject();
		writer.close();
	}

	private void writeServices(CommonJsonWriter writer, Collection<ServiceInfo> services) throws IOException {
		writer.name("services");
		writer.beginArray();
		for (ServiceInfo si : services) {
			writeService(writer, si);
		}
		writer.endArray();
	}

	private void writeService(CommonJsonWriter writer, ServiceInfo si) throws IOException {
		writer.beginObject();
		writer.name("serviceAbbreviation").value(si.getServiceAbbreviation());
		writer.name("additionalInfo").value(si.getAdditionalInfo());
		writer.name("loginUrl").value(si.getLoginUrl());
		writer.name("loginInfo").value(si.getLoginInfo());
		writer.name("additionalLoginInfo").value(si.getAdditionalLoginInfo());
		writer.name("useSmallLetters").value(si.isUseSmallLetters());
		writer.name("useCapitalLetters").value(si.isUseCapitalLetters());
		writer.name("useDigits").value(si.isUseDigits());
		writer.name("useSpecialCharacters").value(si.isUseSpecialCharacters());
		writer.name("specialCharacters").value(si.getSpecialCharacters());
		writer.name("smallLettersCount").value(si.getSmallLettersCount());
		writer.name("smallLettersStartIndex").value(si.getSmallLettersStartIndex());
		writer.name("smallLettersEndIndex").value(si.getSmallLettersEndIndex());
		writer.name("capitalLettersCount").value(si.getCapitalLettersCount());
		writer.name("capitalLettersStartIndex").value(si.getCapitalLettersStartIndex());
		writer.name("capitalLettersEndIndex").value(si.getCapitalLettersEndIndex());
		writer.name("digitsCount").value(si.getDigitsCount());
		writer.name("specialCharactersCount").value(si.getSpecialCharactersCount());
		writer.name("digitsStartIndex").value(si.getDigitsStartIndex());
		writer.name("digitsEndIndex").value(si.getDigitsEndIndex());
		writer.name("specialCharactersStartIndex").value(si.getSpecialCharactersStartIndex());
		writer.name("specialCharactersEndIndex").value(si.getSpecialCharactersEndIndex());
		writer.name("totalCharacterCount").value(si.getTotalCharacterCount());
		writer.name("password").value(si.getPassword());
		writer.name("passwordRepeated").value(si.getPasswordRepeated());
		writer.name("useOldPassphrase").value(si.isUseOldPassphrase());
		writer.name("deleted").value(si.isDeleted());
		writer.name("timeMillis").value(si.getTimeMillis());
		writer.endObject();
	}

}

package de.dknapps.pswgen.util;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2016 Uwe Damken

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import de.dknapps.pswgen.model.ServiceInfo;
import de.dknapps.pswgen.model.ServiceInfoList;

/**
 * <p>
 * FileHelper ist ein Singleton und hilft dabei, Dienstedaten im JSON-Format zu speichern und zu laden.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid bis auf die JSON-Importe identisch, sprich kopiert.
 * </p>
 * <p>
 * Copyright (C) 2005-2016 Uwe Damken
 * </p>
 */
public class FileHelper {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(Constants.APPLICATION_PACKAGE_NAME + ".Logger");

	/** Die eine und einzige Instanz dieser Klasse */
	private static FileHelper instance = null;

	/**
	 * Singleton => privater Konstruktor
	 */
	private FileHelper() {
		// Nichts zu tun
	}

	/**
	 * Liefert die eine und einzige Instanz.
	 */
	public static synchronized FileHelper getInstance() {
		if (instance == null) {
			instance = new FileHelper();
		}
		return instance;
	}

	/**
	 * Lädt alle Diensteinformationen.
	 */
	public ServiceInfoList loadServiceInfoList(File servicesFile) {
		ServiceInfoList services = null;
		try {
			if (servicesFile.exists()) {
				FileInputStream in = new FileInputStream(servicesFile);
				services = loadServiceInfoList(in);
			} else {
				services = new ServiceInfoList(); // später wird eine neue Datei erzeugt
			}
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, Constants.MSG_EXCP_SERVICES, e);
		}
		return services;
	}

	/**
	 * Lädt alle Diensteinformationen.
	 */
	public ServiceInfoList loadServiceInfoList(FileInputStream in) {
		ServiceInfoList services = null;
		try {
			services = readJsonStream(in);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, Constants.MSG_EXCP_SERVICES, e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, Constants.MSG_EXCP_SERVICES, e);
			}
		}
		return services;
	}

	private ServiceInfoList readJsonStream(FileInputStream in) throws IOException {
		ServiceInfoList services = new ServiceInfoList();
		JsonReader reader = new JsonReader(new InputStreamReader(in, Constants.CHARSET_NAME));
		try {
			reader.beginObject();
			checkJsonName(reader, "version");
			services.setVersion(reader.nextString());
			checkJsonName(reader, "verifier");
			services.setEncryptedVerifier(reader.nextString());
			addReadServices(services, reader);
			if (reader.peek() != JsonToken.END_OBJECT) {
				// Erst ab 1.7.4 gibt es salt und initializer, daher ist beides optional
				checkJsonName(reader, "salt");
				services.setSaltAsHexString(reader.nextString());
				checkJsonName(reader, "initializer");
				services.setInitializerAsHexString(reader.nextString());
			}
			reader.endObject();
		} finally {
			reader.close();
		}
		return services;
	}

	private void addReadServices(ServiceInfoList services, JsonReader reader) throws IOException {
		checkJsonName(reader, "services");
		reader.beginArray();
		while (reader.hasNext()) {
			services.addEncryptedService(readService(reader));
		}
		reader.endArray();
	}

	private ServiceInfo readService(JsonReader reader) throws IOException {
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
		reader.endObject();
		return si;
	}

	/**
	 * Prüft den nächsten am Reader vorliegenden Elementnamen und wirft bei einer Abweichung eine Exception.
	 */
	private void checkJsonName(JsonReader reader, String expectedName) throws IOException {
		String actualName = reader.nextName();
		if (!expectedName.equals(actualName)) {
			throw new IOException(
					"Json name mismatch, expected=<" + expectedName + ">, actual=<" + actualName + ">");
		}
	}

	/**
	 * Speichert alle Diensteinformationen.
	 */
	public void saveServiceInfoList(File servicesFile, ServiceInfoList services) throws IOException {
		FileOutputStream out = new FileOutputStream(servicesFile);
		writeJsonStream(out, services);
		out.close();
	}

	private void writeJsonStream(OutputStream out, ServiceInfoList services) throws IOException {
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, Constants.CHARSET_NAME));
		writer.setIndent("\t");
		writer.beginObject();
		services.setVersion(Constants.APPLICATION_VERSION);
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

	private void writeServices(JsonWriter writer, Collection<ServiceInfo> services) throws IOException {
		writer.name("services");
		writer.beginArray();
		for (ServiceInfo si : services) {
			writeService(writer, si);
		}
		writer.endArray();
	}

	private void writeService(JsonWriter writer, ServiceInfo si) throws IOException {
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
		writer.endObject();
	}

}

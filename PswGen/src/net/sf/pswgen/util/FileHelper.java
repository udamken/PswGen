package net.sf.pswgen.util;

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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.sf.pswgen.model.ServiceInfo;
import net.sf.pswgen.model.ServiceInfoList;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

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

/**
 * <p>
 * FileHelper ist ein Singleton und hilft dabei, Dienstedaten zu speichern und zu laden.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class FileHelper {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(Constants.APPLICATION_PACKAGE_NAME + ".Logger",
			Constants.APPLICATION_PACKAGE_NAME + ".Messages");

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
	public ServiceInfoList loadServiceInfoListFromXml(File servicesFile) {
		ServiceInfoList services = new ServiceInfoList();
		try {
			JAXBContext context = JAXBContext.newInstance(ServiceInfoList.class);
			Unmarshaller um = context.createUnmarshaller();
			if (servicesFile.exists()) {
				FileInputStream in = new FileInputStream(servicesFile);
				services = (ServiceInfoList) um.unmarshal(in);
				in.close();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, Constants.MSG_EXCP_SERVICES, e);
		}
		return services;
	}

	/**
	 * Lädt alle Diensteinformationen.
	 */
	public ServiceInfoList loadServiceInfoList(File servicesFile) {
		ServiceInfoList services = new ServiceInfoList();
		try {
			if (servicesFile.exists()) {
				FileInputStream in = new FileInputStream(servicesFile);
				services = readJsonStream(in);
				in.close();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, Constants.MSG_EXCP_SERVICES, e);
		}
		return services;
	}

	private ServiceInfoList readJsonStream(FileInputStream in) throws IOException {
		ServiceInfoList services = new ServiceInfoList();
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			reader.beginObject();
			checkJsonName(reader, "version");
			services.setVersion(reader.nextString());
			checkJsonName(reader, "verifier");
			services.setVerifier(reader.nextString());
			addReadServices(services, reader);
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
			throw new IOException("Json name mismatch, expected=<" + expectedName + ">, actual=<"
					+ actualName + ">");
		}
	}

	/**
	 * Speichert alle Diensteinformationen.
	 */
	public void saveServiceInfoList(File servicesFile, ServiceInfoList services) {
		try {
			FileOutputStream out = new FileOutputStream(servicesFile);
			writeJsonStream(out, services);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception occured: " + e); // FIXME ??
		}
	}

	private void writeJsonStream(OutputStream out, ServiceInfoList services) throws IOException {
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
		writer.setIndent("\t");
		writer.beginObject();
		services.setVersion(Constants.APPLICATION_VERSION);
		writer.name("version").value(services.getVersion());
		writer.name("verifier").value(services.getVerifier());
		writeServices(writer, services.getEncryptedServices());
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
package net.sf.pswgen.util;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2014 Uwe Damken

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.sf.pswgen.model.ServiceInfoList;

/**
 * <p>
 * FileHelper ist ein Singleton und hilft dabei, Dienstedaten im XML-Format zu speichern und zu laden.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class XmlFileHelper {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(Constants.APPLICATION_PACKAGE_NAME + ".Logger");

	/** Die eine und einzige Instanz dieser Klasse */
	private static XmlFileHelper instance = null;

	/**
	 * Singleton => privater Konstruktor
	 */
	private XmlFileHelper() {
		// Nichts zu tun
	}

	/**
	 * Liefert die eine und einzige Instanz.
	 */
	public static synchronized XmlFileHelper getInstance() {
		if (instance == null) {
			instance = new XmlFileHelper();
		}
		return instance;
	}

	/**
	 * Lädt alle Diensteinformationen.
	 */
	public ServiceInfoList loadServiceInfoList(File servicesFile) {
		ServiceInfoList services = null;
		try {
			JAXBContext context = JAXBContext.newInstance(ServiceInfoList.class);
			Unmarshaller um = context.createUnmarshaller();
			if (servicesFile.exists()) {
				FileInputStream in = new FileInputStream(servicesFile);
				services = (ServiceInfoList) um.unmarshal(in);
				in.close();
			} else {
				services = new ServiceInfoList(); // später wird eine neue Datei erzeugt
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, Constants.MSG_EXCP_SERVICES, e);
		}
		return services;
	}

}

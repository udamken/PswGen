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
package de.dknapps.pswgendroid;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;
import android.widget.Toast;
import de.dknapps.pswgen.model.ServiceInfo;
import de.dknapps.pswgen.model.ServiceInfoList;
import de.dknapps.pswgen.util.Constants;
import de.dknapps.pswgen.util.DomainException;
import de.dknapps.pswgen.util.EncryptionHelper;
import de.dknapps.pswgen.util.FileHelper;
import de.dknapps.pswgendroid.R;

/**
 * <p>
 * Dient als Verbindungsklasse von Klassen aus PswGenDroid zu Klassen aus PswGen, damit letztere ohne
 * Änderungen aus PswGen in PswGenDroid übernommen werden können.
 * </p>
 */
public class PswGenAdapter {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(Constants.APPLICATION_PACKAGE_NAME + ".Logger");

	/** Alle Informationen zu Dienstekürzeln */
	private static ServiceInfoList services;

	/** Alle Informationen zu Dienstekürzeln */
	private static List<ServiceInfo> servicesAsList;

	/** Überprüfte Passphrase für die Entschlüsselung und Passwortgenerierung */
	private static String validatedPassphrase;

	/**
	 * Dienste aus der übergebenen Datei laden, die Pasphrase prüfen und für die weitere Benutzung
	 * aufbewahren.
	 */
	public static void loadServiceInfoList(FileInputStream in, String passphrase) {
		services = FileHelper.getInstance().loadServiceInfoList(in);
		if (services == null) {
			throw new DomainException("UnknownFileFormatMsg");
		}
		EncryptionHelper encryptionHelper = new EncryptionHelper(passphrase.toCharArray(),
				services.getSaltAsHexString(), services.getInitializerAsHexString());
		services.decrypt(encryptionHelper); // Info-Collection entschlüsselt in Map stellen
		servicesAsList = new ArrayList<ServiceInfo>(services.getServices());
		validatedPassphrase = passphrase;
	}

	/**
	 * Bereits geladene Dienste "entladen", also dereferenzieren und die Passphrase löschen.
	 */
	public static void unloadServiceInfoList() {
		services = null;
		servicesAsList = null;
		validatedPassphrase = null;
	}

	/**
	 * Liefert true, wenn die Dienste geladen wurden und die Passphrase gesetzt ist, sonst false.
	 */
	public static boolean isServiceInfoListLoaded() {
		return services != null && servicesAsList != null && validatedPassphrase != null;
	}

	/**
	 * Liefert den Dienst mit dem übergebenen Dienstekürzel.
	 */
	public static ServiceInfo getServiceInfo(String serviceAbbreviation) {
		return services.getServiceInfo(serviceAbbreviation);
	}

	/**
	 * Liefert den Dienst an der übergebenen Position in der Liste der Dienste.
	 */
	public static ServiceInfo getServiceInfo(int position) {
		return servicesAsList.get(position);
	}

	/**
	 * Liefert die Dienste als Liste (ServiceInfoList.values ist nur eine Collection).
	 */
	public static List<ServiceInfo> getServicesAsList() {
		return servicesAsList;
	}

	/**
	 * Liefert die bereits überprüfte Passphrase.
	 */
	public static String getValidatedPassphrase() {
		return validatedPassphrase;
	}

	/**
	 * Fehler in Form eines Throwables behandeln (Logging und Fehler anzeigen).
	 */
	public static void handleThrowable(Context context, final Throwable t) {
		if (t instanceof DomainException) {
			LOGGER.log(Level.SEVERE, "DomainException caught: ", t);
			Toast.makeText(context, getDomainExceptionText(context, (DomainException) t), Toast.LENGTH_LONG)
			.show();
		} else {
			LOGGER.log(Level.SEVERE, "Throwable caught: ", t);
			Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Liefert zu der Text-Id aus einer DomainException den Text aus den Android-Ressourcen.
	 */
	public static String getDomainExceptionText(Context context, DomainException domainException) {
		final String msg = domainException.getMessage();
		if (msg.equals("PassphraseInvalidMsg")) {
			return context.getString(R.string.passphrase_invalid);
		} else if (msg.equals("TotalCharacterCountExceededMsg")) {
			return context.getString(R.string.total_character_count_exceeded);
		} else if (msg.equals("InvalidCharacterCountMsg")) {
			return context.getString(R.string.invalid_character_count);
		} else if (msg.equals("CharacterOrTotalCharacterCountMissingMsg")) {
			return context.getString(R.string.character_or_total_character_count_missing);
		} else if (msg.equals("UnknownFileFormatMsg")) {
			return context.getString(R.string.unknown_file_format);
		}
		return msg;
	}

}

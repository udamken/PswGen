/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2017 Uwe Damken
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
package de.dknapps.pswgendroid;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;
import android.widget.Toast;

import de.dknapps.pswgencore.model.ServiceInfo;
import de.dknapps.pswgencore.model.ServiceInfoList;
import de.dknapps.pswgencore.util.DomainException;
import de.dknapps.pswgencore.util.EncryptionHelper;
import de.dknapps.pswgencore.util.FileHelper;
import de.dknapps.pswgendroid.util.CommonJsonReaderWriterFactoryAndroidImpl;

/**
 * <p>
 * Dient als Verbindungsklasse von Klassen aus PswGenDroid zu Klassen aus PswGen, damit letztere ohne
 * Änderungen aus PswGen in PswGenDroid übernommen werden können.
 * </p>
 */
public class PswGenAdapter {

    /**
     * Der Logger dieser Anwendung
     */
    private static final Logger LOGGER = Logger.getLogger(DroidConstants.LOGGER_NAME);

    /**
     * Alle Informationen zu Dienstekürzeln
     */
    private static ServiceInfoList services;

    /**
     * Alle Informationen zu Dienstekürzeln
     */
    private static List<ServiceInfo> servicesAsList;

    /**
     * Überprüfte Passphrase für die Entschlüsselung und Passwortgenerierung
     */
    private static String validatedPassphrase;

    /**
     * Eingegebene alte Passphrase für die Generierung von Passworten mit der alten Passphrase
     */
    private static String oldPassphrase;

    /**
     * Dienste aus der übergebenen Datei laden, die Pasphrase prüfen und mit der alten Passphrase für die weitere Benutzung
     * aufbewahren.
     */
    public static void loadServiceInfoList(File servicesFile, File otherServicesFile, String passphrase, String oldPassphrase) {
        FileHelper fileHelper = FileHelper.getInstance(new CommonJsonReaderWriterFactoryAndroidImpl());
        validatedPassphrase = passphrase;
        services = fileHelper.loadServiceInfoList(servicesFile, otherServicesFile, validatedPassphrase);
        servicesAsList = new ArrayList<ServiceInfo>(services.getServices(false));
        PswGenAdapter.oldPassphrase = oldPassphrase;
    }

    /**
     * Bereits geladene Dienste "entladen", also dereferenzieren und die Passphrase löschen.
     */
    public static void unloadServiceInfoList() {
        services = null;
        servicesAsList = null;
        validatedPassphrase = null;
        oldPassphrase = null;
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
     * Liefert die alte Passphrase.
     */
    public static String getOldPassphrase() {
        return oldPassphrase;
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
            return context.getString(R.string.PassphraseInvalidMsg);
        } else if (msg.equals("FileCouldNotBeOpenedMsg")) {
            return context.getString(R.string.FileCouldNotBeOpenedMsg);
        } else if (msg.equals("UnsupportedFileFormatMsg")) {
            return context.getString(R.string.UnsupportedFileFormatMsg);
        } else if (msg.equals("TotalCharacterCountExceededMsg")) {
            return context.getString(R.string.TotalCharacterCountExceededMsg);
        } else if (msg.equals("InvalidCharacterCountMsg")) {
            return context.getString(R.string.InvalidCharacterCountMsg);
        } else if (msg.equals("CharacterOrTotalCharacterCountMissingMsg")) {
            return context.getString(R.string.CharacterOrTotalCharacterCountMissingMsg);
        } else if (msg.equals("PassphraseMismatchMsg")) {
            return context.getString(R.string.PassphraseMismatchMsg);
        } else if (msg.equals("ServiceAbbreviationEmptyMsg")) {
            return context.getString(R.string.ServiceAbbreviationEmptyMsg);
        } else if (msg.equals("ServiceAbbreviationMissingMsg")) {
            return context.getString(R.string.ServiceAbbreviationMissingMsg);
        } else if (msg.equals("PassphraseEmptyMsg")) {
            return context.getString(R.string.PassphraseEmptyMsg);
        } else if (msg.equals("NewPassphraseMismatchMsg")) {
            return context.getString(R.string.NewPassphraseMismatchMsg);
        } else if (msg.equals("OldPassphraseEmptyMsg")) {
            return context.getString(R.string.OldPassphraseEmptyMsg);
        } else if (msg.equals("NewPassphraseEmptyMsg")) {
            return context.getString(R.string.NewPassphraseEmptyMsg);
        } else if (msg.equals("PasswordMismatchMsg")) {
            return context.getString(R.string.PasswordMismatchMsg);
        }
        return msg;
    }

}

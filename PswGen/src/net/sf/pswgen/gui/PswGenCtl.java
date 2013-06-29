package net.sf.pswgen.gui;

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

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.pswgen.gui.base.BaseCtl;
import net.sf.pswgen.model.ServiceInfo;
import net.sf.pswgen.model.ServiceInfoList;
import net.sf.pswgen.util.Constants;
import net.sf.pswgen.util.DomainException;
import net.sf.pswgen.util.EmptyHelper;
import net.sf.pswgen.util.EncryptionHelper;
import net.sf.pswgen.util.PasswordFactory;

/**
 * <p>
 * Der Controller der Anwendung. Hier werden sämtliche Dialoge gesteuert und die Anwendungsfälle
 * implementiert. Als Alternative zu der Idee, je Dialog einen Controller zu implementieren. Eventuell wird
 * die Klasse aber auch mal ein bisschen groß und die Anwendungslogik muss ausgelagert werden.
 * </p>
 * <p>
 * Copyright (C) 2005-2013 Uwe Damken
 * </p>
 */
public class PswGenCtl extends BaseCtl {

	/** Der Logger für diese Klasse */
	private static final Logger LOGGER = Logger.getLogger(Constants.APPLICATION_PACKAGE_NAME + ".Logger",
			Constants.APPLICATION_PACKAGE_NAME + ".Messages");

	/** Alle Informationen zu Dienstekürzeln */
	private ServiceInfoList services = new ServiceInfoList();

	/** Der Dateiname zum Laden und Speichern der Diensteliste */
	private File servicesFile;

	/** Die in der StartupView eingegebene Passphrase */
	private String validatedPassphrase;

	/**
	 * Liefert das eingegebene oder ein generiertes Passwort. Sobald entweder das Passwort oder das
	 * wiederholte Passwort eingegeben wurden, müssen sie übereinstimmen, sonst wird eine Exception geworfen,
	 * die zu einer Fehlermeldung führt. Eine Eingabe hat also in jedem Fall Vorrang vor der Generierung.
	 */
	private String validatedOrGeneratePassword(final MainView mainView) {
		String password = mainView.getPassword();
		final String passwordRepeated = mainView.getPasswordRepeated();
		if (password.length() == 0 && passwordRepeated.length() == 0) { // Beide leer? => generieren
			password = generatePassword(mainView);
		} else {
			if (!password.equals(passwordRepeated)) { // Mismatch?
				throw new DomainException("PasswordMismatchMsg");
			}
		}
		return password;
	}

	/**
	 * Eingabewert des Dienstekürzels überprüfen.
	 */
	private void validateServiceAbbreviation(final String serviceAbbreviation) {
		if (serviceAbbreviation.length() == 0) {
			throw new DomainException("ServiceAbbreviationEmptyMsg");
		}
	}

	/**
	 * Lädt alle Diensteinformationen.
	 */
	private void loadServiceInfoList() {
		try {
			JAXBContext context = JAXBContext.newInstance(ServiceInfoList.class);
			Unmarshaller um = context.createUnmarshaller();
			FileInputStream in = new FileInputStream(servicesFile);
			services = (ServiceInfoList) um.unmarshal(in);
			in.close();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, Constants.MSG_EXCP_SERVICES, e);
		}
	}

	/**
	 * Speichert alle Diensteinformationen.
	 */
	private void saveServiceInfoList() {
		try {
			if (!services.isAdvancedFormat()) { // Noch im alten Format? => Konvertieren
				final String verifierEncrypted = EncryptionHelper.encrypt(validatedPassphrase,
						Constants.APPLICATION_VERIFIER);
				services.setVerifier(verifierEncrypted);
				services.setVersion(Constants.APPLICATION_VERSION);
				for (ServiceInfo si : services.getServices()) {
					// FIXME Alle Felder umverschlüsseln ...
				}
			}
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext.newInstance(ServiceInfoList.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			FileOutputStream out = new FileOutputStream(servicesFile);
			services.encrypt(validatedPassphrase); // Infos aus Map verschlüsselt in Collection stellen
			m.marshal(services, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception occured: " + e);
		}
	}

	/**
	 * Lädt die Einstellungen für ein Dienstekürzel.
	 */
	public void actionPerformedLoadService(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			loadService(mainView);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Lädt die Einstellungen für ein Dienstekürzel.
	 */
	private void loadService(final MainView mainView) {
		String serviceAbbreviation = mainView.getServiceAbbreviation();
		validateServiceAbbreviation(serviceAbbreviation);
		ServiceInfo si = services.getServiceInfo(serviceAbbreviation);
		if (si == null) {
			throw new DomainException("ServiceAbbreviationMissingMsg");
		} else {
			mainView.setAdditionalInfo(si.getAdditionalInfo());
			mainView.setLoginUrl(si.getLoginUrl());
			mainView.setLoginInfo(si.getLoginInfo());
			mainView.setAdditionalLoginInfo(si.getAdditionalLoginInfo());
			mainView.setUseSmallLetters(si.isUseSmallLetters());
			mainView.setUseCapitalLetters(si.isUseCapitalLetters());
			mainView.setUseDigits(si.isUseDigits());
			mainView.setUseSpecialCharacters(si.isUseSpecialCharacters());
			mainView.setSpecialCharacters(si.getSpecialCharacters());
			ensureAtLeastDefaultSpecialCharacters(mainView);
			mainView.setSmallLettersCount(si.getSmallLettersCount());
			mainView.setSmallLettersStartIndex(si.getSmallLettersStartIndex());
			mainView.setSmallLettersEndIndex(si.getSmallLettersEndIndex());
			mainView.setCapitalLettersCount(si.getCapitalLettersCount());
			mainView.setCapitalLettersStartIndex(si.getCapitalLettersStartIndex());
			mainView.setCapitalLettersEndIndex(si.getCapitalLettersEndIndex());
			mainView.setDigitsCount(si.getDigitsCount());
			mainView.setSpecialCharactersCount(si.getSpecialCharactersCount());
			mainView.setDigitsStartIndex(si.getDigitsStartIndex());
			mainView.setDigitsEndIndex(si.getDigitsEndIndex());
			mainView.setSpecialCharactersStartIndex(si.getSpecialCharactersStartIndex());
			mainView.setSpecialCharactersEndIndex(si.getSpecialCharactersEndIndex());
			mainView.setTotalCharacterCount(si.getTotalCharacterCount());
			mainView.setPassword(si.getPassword());
			mainView.setPasswordRepeated(si.getPasswordRepeated());
		}
	}

	/**
	 * Aus der Liste neu ausgewählten Dienst laden.
	 */
	public void valueChangedLoadServiceFromList(final MainView mainView, final String serviceAbbreviation) {
		try {
			mainView.setWaitCursor();
			mainView.setServiceAbbreviation(serviceAbbreviation);
			loadService(mainView);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Löscht die Einstellungen für ein Dienstekürzel und speichert die Einstellungen für alle Dienstekürzel
	 * auf der Platte.
	 */
	public void actionPerformedRemoveService(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			String serviceAbbreviation = mainView.getServiceAbbreviation();
			validateServiceAbbreviation(serviceAbbreviation);
			int chosenOption = JOptionPane.showConfirmDialog(mainView, getGuiText("RemoveServiceMsg"),
					Constants.APPLICATION_NAME, JOptionPane.YES_NO_OPTION);
			if (chosenOption != JOptionPane.NO_OPTION) { // Dienst nicht
				ServiceInfo si = services.removeServiceInfo(serviceAbbreviation);
				if (si == null) { // Dienst gar nicht vorhanden?
					throw new DomainException("ServiceAbbreviationMissingMsg");
				} else {
					saveServiceInfoList();
					mainView.updateStoredService();
				}
			}
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Öffnet die Login-URL im Browser und kopiert die Login-Informationen in die Zwischenablage.
	 */
	public void actionPerformedOpenUrlInBrowser(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			final String loginUrl = mainView.getLoginUrl();
			Desktop.getDesktop().browse(new URI(loginUrl));
			copyLoginInfo(mainView);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Kopiert die Login-Informationen in die Zwischenablage.
	 */
	public void actionPerformedCopyLoginInfo(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			copyLoginInfo(mainView);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Kopiert die Login-Informationen in die Zwischenablage.
	 */
	private void copyLoginInfo(final MainView mainView) {
		final String loginInfo = mainView.getLoginInfo();
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(loginInfo), null);
	}

	/**
	 * Öffnet die Hilfe-URL im Browser.
	 */
	public void actionPerformedOpenHelpInBrowser(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			String url = Constants.HELP_URL_EN;
			if (Locale.getDefault().getLanguage().equalsIgnoreCase("de")) {
				url = Constants.HELP_URL_DE;
			}
			Desktop.getDesktop().browse(new URI(url));
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Öffnet den About-Dialog.
	 */
	public void actionPerformedOpenAbout(@SuppressWarnings("unused")
	final MainView mainView) {
		try {
			AboutView aboutView = new AboutView(this);
			addView(aboutView);
			aboutView.pack();
			aboutView.setVisible(true);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			// mainView.setDefaultCursor();
		}
	}

	/**
	 * Vermerkt die Einstellungen für ein Dienstekürzel und speichert die Einstellungen für alle Dienstekürzel
	 * auf der Platte.
	 */
	public void actionPerformedStoreService(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			String serviceAbbreviation = mainView.getServiceAbbreviation();
			validateServiceAbbreviation(serviceAbbreviation);
			ServiceInfo si = services.getServiceInfo(serviceAbbreviation);
			if (si != null) { // Ist der Dienst bereits vermerkt?
				int chosenOption = JOptionPane.showConfirmDialog(mainView, getGuiText("OverwriteServiceMsg"),
						Constants.APPLICATION_NAME, JOptionPane.YES_NO_OPTION);
				if (chosenOption == JOptionPane.NO_OPTION) { // Dienst nicht
					// überschreiben?
					return; // nein ... dann ist nichts mehr zu tun
				}
			}
			si = new ServiceInfo(serviceAbbreviation);
			si.setAdditionalInfo(mainView.getAdditionalInfo());
			si.setLoginUrl(mainView.getLoginUrl());
			si.setLoginInfo(mainView.getLoginInfo());
			si.setAdditionalLoginInfo(mainView.getAdditionalLoginInfo());
			si.setUseSmallLetters(mainView.getUseSmallLetters());
			si.setUseCapitalLetters(mainView.getUseCapitalLetters());
			si.setUseDigits(mainView.getUseDigits());
			si.setUseSpecialCharacters(mainView.getUseSpecialCharacters());
			si.setSpecialCharacters(mainView.getSpecialCharacters());
			si.setSmallLettersCount(mainView.getSmallLettersCount());
			si.setSmallLettersStartIndex(mainView.getSmallLettersStartIndex());
			si.setSmallLettersEndIndex(mainView.getSmallLettersEndIndex());
			si.setCapitalLettersCount(mainView.getCapitalLettersCount());
			si.setCapitalLettersStartIndex(mainView.getCapitalLettersStartIndex());
			si.setCapitalLettersEndIndex(mainView.getCapitalLettersEndIndex());
			si.setDigitsCount(mainView.getDigitsCount());
			si.setSpecialCharactersCount(mainView.getSpecialCharactersCount());
			si.setDigitsStartIndex(mainView.getDigitsStartIndex());
			si.setDigitsEndIndex(mainView.getDigitsEndIndex());
			si.setSpecialCharactersStartIndex(mainView.getSpecialCharactersStartIndex());
			si.setSpecialCharactersEndIndex(mainView.getSpecialCharactersEndIndex());
			si.setTotalCharacterCount(mainView.getTotalCharacterCount());
			si.setPassword(mainView.getPassword());
			si.setPasswordRepeated(mainView.getPasswordRepeated());
			services.putServiceInfo(si);
			saveServiceInfoList();
			mainView.updateStoredService();
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Generiert ein Passwort und gibt es zurück.
	 */
	private String generatePassword(final MainView mainView) {
		mainView.setWaitCursor();
		ensureAtLeastDefaultSpecialCharacters(mainView);
		String characters = ""; // Zeichen für den Rest des Passworts
		final String serviceAbbreviation = mainView.getServiceAbbreviation();
		validateServiceAbbreviation(serviceAbbreviation);
		long seed = validatedPassphrase.hashCode() + serviceAbbreviation.hashCode();
		final String additionalInfo = mainView.getAdditionalInfo();
		if (additionalInfo.length() != 0) { // Zusatzinfos vorhanden?
			seed += additionalInfo.hashCode(); // => Zur Saat dazunehmen
		}
		final int pswLength = EmptyHelper.getValue(mainView.getTotalCharacterCount(), 0);
		PasswordFactory pg = new PasswordFactory(pswLength);
		pg.setSeedForRandomToEnforceReproducableResults(seed);
		if (mainView.getUseSmallLetters()) {
			int count = EmptyHelper.getValue(mainView.getSmallLettersCount(), 0);
			int start = EmptyHelper.getValue(mainView.getSmallLettersStartIndex(), 0);
			int end = EmptyHelper.getValue(mainView.getSmallLettersEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, Constants.LOWERCASE_LETTERS, start, end);
			} else {
				characters += Constants.LOWERCASE_LETTERS;
			}
		}
		if (mainView.getUseCapitalLetters()) {
			int count = EmptyHelper.getValue(mainView.getCapitalLettersCount(), 0);
			int start = EmptyHelper.getValue(mainView.getCapitalLettersStartIndex(), 0);
			int end = EmptyHelper.getValue(mainView.getCapitalLettersEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, Constants.UPPERCASE_LETTERS, start, end);
			} else {
				characters += Constants.UPPERCASE_LETTERS;
			}
		}
		if (mainView.getUseDigits()) {
			int count = EmptyHelper.getValue(mainView.getDigitsCount(), 0);
			int start = EmptyHelper.getValue(mainView.getDigitsStartIndex(), 0);
			int end = EmptyHelper.getValue(mainView.getDigitsEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, Constants.DIGITS, start, end);
			} else {
				characters += Constants.DIGITS;
			}
		}
		if (mainView.getUseSpecialCharacters()) {
			int count = EmptyHelper.getValue(mainView.getSpecialCharactersCount(), 1);
			int start = EmptyHelper.getValue(mainView.getSpecialCharactersStartIndex(), 0);
			int end = EmptyHelper.getValue(mainView.getSpecialCharactersEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, mainView.getSpecialCharacters(), start, end);
			} else {
				characters += mainView.getSpecialCharacters();
			}
		}
		return pg.getPassword(characters); // Rest auffüllen
	}

	/**
	 * Sonderzeichen müssen gesetzt sein, und wenn es nur eine Default-Auswahl ist.
	 */
	private void ensureAtLeastDefaultSpecialCharacters(final MainView mainView) {
		if (mainView.getSpecialCharacters() == null || mainView.getSpecialCharacters().length() == 0) {
			mainView.setSpecialCharacters(Constants.SPECIAL_CHARS);
		}
	}

	/**
	 * Generiert das Passwort und zeigt es an.
	 */
	public void actionPerformedDisplayPassword(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			final String psw = validatedOrGeneratePassword(mainView);
			JOptionPane.showMessageDialog(mainView, getGuiText("DisplayPasswordMsg") + " \"" + psw + "\"",
					Constants.APPLICATION_NAME, JOptionPane.PLAIN_MESSAGE);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Generiert das Passwort und kopiert es in die Zwischenablage.
	 */
	public void actionPerformedCopyPassword(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			final String psw = validatedOrGeneratePassword(mainView);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(psw), null);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Prüft die Eingabewerte der Passphrase und gibt die Passphrase zurück oder wirft eine Exception.
	 */
	private String validatePassphrase(final StartupView startupView) {
		final String passphrase = startupView.getPassphrase();
		final String passphraseRepeated = startupView.getPassphraseRepeated();
		if (passphrase.length() == 0) {
			throw new DomainException("PassphraseEmptyMsg");
		}
		if (services.isAdvancedFormat()) {
			// Beim neuen Format wird geprüft, ob die Passphrase den Prüfstring entschlüsseln kann
			final String verifierEncrypted = services.getVerifier();
			final String verifierDecrypted = EncryptionHelper.decrypt(passphrase, verifierEncrypted);
			if (!verifierDecrypted.equals(Constants.APPLICATION_VERIFIER)) {
				throw new DomainException("PassphraseInvalid");
			}
		} else {
			// Beim alten Format wird geprüft, ob die Passphrase zweimal gleich eingegeben wurde
			if (!passphrase.equals(passphraseRepeated)) { // Mismatch?
				throw new DomainException("PassphraseMismatchMsg");
			}
		}
		return passphrase;
	}

	/**
	 * Prüft die Passphrase und öffnet ggf. das Hauptfenster.
	 */
	public void actionPerformedOpenServices(final StartupView startupView) {
		try {
			validatedPassphrase = validatePassphrase(startupView);
			services.decrypt(validatedPassphrase); // Infos Collection entschlüsselt in Map stellen
			startupView.dispose();
			MainView mainView = new MainView(this);
			mainView.setTitle(servicesFile.getAbsolutePath() + " - " + Constants.APPLICATION_NAME + " "
					+ Constants.APPLICATION_VERSION);
			addView(mainView);
			mainView.pack();
			mainView.setVisible(true);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			// mainView.setDefaultCursor();
		}
	}

	public ServiceInfoList getServices() {
		return services;
	}

	/**
	 * Erzeugt einen Controller, die Hauptklasse wird zum Runterfahren benötigt.
	 */
	public PswGenCtl(final String givenServicesFilename) {
		super();
		servicesFile = new File(givenServicesFilename);
		loadServiceInfoList();
		StartupView startupView = new StartupView(this);
		startupView.setTitle(Constants.APPLICATION_NAME + " " + Constants.APPLICATION_VERSION);
		if (services.isAdvancedFormat()) { // Fortgeschrittenes Format?
			startupView.disablePassphraseRepeated(); // Passphrase nur 1x eingeben!
		}
		addView(startupView);
		startupView.pack();
		startupView.setVisible(true);
	}

}
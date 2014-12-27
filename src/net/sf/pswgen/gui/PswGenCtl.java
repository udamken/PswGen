package net.sf.pswgen.gui;

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

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.sf.pswgen.gui.base.BaseCtl;
import net.sf.pswgen.gui.base.BaseView;
import net.sf.pswgen.model.ServiceInfo;
import net.sf.pswgen.model.ServiceInfoList;
import net.sf.pswgen.util.Constants;
import net.sf.pswgen.util.DomainException;
import net.sf.pswgen.util.EmptyHelper;
import net.sf.pswgen.util.EncryptionHelper;
import net.sf.pswgen.util.FileHelper;
import net.sf.pswgen.util.PasswordFactory;
import net.sf.pswgen.util.XmlFileHelper;

/**
 * <p>
 * Der Controller der Anwendung. Hier werden sämtliche Dialoge gesteuert und die Anwendungsfälle
 * implementiert. Als Alternative zu der Idee, je Dialog einen Controller zu implementieren. Eventuell wird
 * die Klasse aber auch mal ein bisschen groß und die Anwendungslogik muss ausgelagert werden.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class PswGenCtl extends BaseCtl {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(Constants.APPLICATION_PACKAGE_NAME + ".Logger");

	/** Alle Informationen zu Dienstekürzeln */
	private ServiceInfoList services = new ServiceInfoList();

	/** Der Dateiname zum Laden und Speichern der Diensteliste */
	private File servicesFile;

	/** Die in der StartupView eingegebene Passphrase */
	private String validatedPassphrase;

	/**
	 * Erzeugt einen Controller.
	 */
	public PswGenCtl(final String givenServicesFilename) {
		super();
		servicesFile = new File(givenServicesFilename);
		setupLookAndFeel();
	}

	/**
	 * Setzt das Look&Feel auf den System-Standard.
	 */
	private void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, Constants.MSG_EXCP_LOOK_AND_FEEL, e);
		}
	}

	/**
	 * Datei lediglich in das neue Format konvertieren, dann endet die Anwendung.
	 */
	public void upgradeServiceInfoList(final String targetFilename) throws IOException {
		services = XmlFileHelper.getInstance().loadServiceInfoList(servicesFile);
		if (services == null || services.isNew()) {
			JOptionPane.showMessageDialog(null, getGuiText("EmptyFileNotUpgradableMsg"),
					Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, Constants.MSG_EMPTY_FILE_NOT_UPGRADABLE);
			System.exit(16);
		} else if (services.isUnsupportedFormat()) {
			JOptionPane.showMessageDialog(null, getGuiText("UnsupportedFileFormatMsg"),
					Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, Constants.MSG_UNSUPPORTED_FILE_FORMAT_VERSION);
			System.exit(16);
		} else if (services.isAdvancedFormat()) { // Schon im neuen Format? => nichts zu tun
			JOptionPane.showMessageDialog(null, getGuiText("FileFormatAlreadyConvertedMsg"),
					Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, Constants.MSG_ALREADY_CONVERTED_FILE_FORMAT_VERSION);
			System.exit(16);
		}
		FileHelper.getInstance().saveServiceInfoList(new File(targetFilename), services);
		JOptionPane.showMessageDialog(null, getGuiText("FileFormatSuccessfullyConvertedMsg"),
				Constants.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Lädt die Dienstedatei, fragt die Passphrase ab und öffnet das Hauptfenster. Die Anwendung endet nach
	 * dieser Methode nicht, das Beenden geschieht über die Oberfläche.
	 */
	public void start() {
		services = FileHelper.getInstance().loadServiceInfoList(servicesFile);
		if (services == null) { // JSON-Datei konnte nicht gelesen werden? => XML-Datei versuchen
			services = XmlFileHelper.getInstance().loadServiceInfoList(servicesFile);
		}
		if (services == null) {
			// Datei ist weder als JSON noch als XML lesbar
			JOptionPane.showMessageDialog(null, getGuiText("UnknownFileFormatMsg"),
					Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, Constants.MSG_UNKNOWN_FILE_FORMAT_VERSION);
			System.exit(20);
		} else if (services.isNew()) {
			// Ein frischer Anfang, beim Speichern wird eine neue Datei angelegt werden
		} else if (services.isUnsupportedFormat()) {
			// Dateiformat wird nicht mehr unterstützt und muss mit einer alten Version konvertiert werden
			JOptionPane.showMessageDialog(null, getGuiText("UnsupportedFileFormatMsg"),
					Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, Constants.MSG_UNSUPPORTED_FILE_FORMAT_VERSION);
			System.exit(16);
		} else if (services.isAdvancedFormat()) {
			// Datei hat das aktuelle Format, es ist nichts weiter zu tun
		} else {
			// Datei hat ein altes Format und muss erst mit -upgrade konvertiert werden
			JOptionPane.showMessageDialog(null, getGuiText("FileFormatMustBeConvertedMsg"),
					Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, Constants.MSG_TO_BE_CONVERTED_FILE_FORMAT_VERSION);
			System.exit(16);
		}
		StartupView startupView = new StartupView(this);
		startupView.setTitle(Constants.APPLICATION_NAME + " " + Constants.APPLICATION_VERSION);
		if (!services.isNew()) { // Keine neue Datei?
			startupView.disablePassphraseRepeated(); // Passphrase nur 1x eingeben!
		}
		addView(startupView);
		startupView.pack();
		startupView.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.pswgen.gui.base.BaseCtl#windowClosing(net.sf.pswgen.gui.base.BaseView)
	 */
	@Override
	public void windowClosing(BaseView view) {
		try {
			if (view instanceof MainView) {
				MainView mainView = (MainView) view;
				if (cancelOnDirty(mainView)) { // Aktion abbrechen?
					return;
				}
			}
			super.windowClosing(view);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			// Nichts mehr zu tun
		}
	}

	/**
	 * Prüft die Passphrase, aktualisert den Verifizierungs- und Versions-String (besonders wichtig bei neuen
	 * Dateien) und öffnet ggf. das Hauptfenster.
	 */
	public void actionPerformedOpenServices(final StartupView startupView) {
		try {
			validatedPassphrase = validatePassphrase(startupView);
			services.decrypt(validatedPassphrase); // Info-Collection entschlüsselt in Map stellen
			services.setVerifier(EncryptionHelper
					.encrypt(validatedPassphrase, Constants.APPLICATION_VERIFIER));
			services.setVersion(Constants.APPLICATION_VERSION);
			startupView.dispose();
			MainView mainView = new MainView(this);
			mainView.setTitle(servicesFile.getAbsolutePath() + " - " + Constants.APPLICATION_NAME + " "
					+ Constants.APPLICATION_VERSION);
			addView(mainView);
			mainView.pack();
			ensureAtLeastDefaultSpecialCharacters(mainView);
			clearService(mainView); // Diensteinstellungen initialisieren (Tagesdatum)
			mainView.setVisible(true);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			// Nichts mehr zu tun
		}
	}

	/**
	 * Aus der Liste neu ausgewählten Dienst laden.
	 */
	public void valueChangedLoadServiceFromList(final MainView mainView, final String serviceAbbreviation) {
		try {
			if (cancelOnDirty(mainView)) { // Aktion abbrechen?
				return;
			}
			mainView.setWaitCursor();
			validateServiceAbbreviation(serviceAbbreviation);
			ServiceInfo si = services.getServiceInfo(serviceAbbreviation);
			if (si == null) {
				throw new DomainException("ServiceAbbreviationMissingMsg");
			} else {
				putServiceToView(mainView, si);
			}
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Leert die Einstellungen für das Dienstekürzel, in AdditionalInfo kommt das Tagesdatum.
	 */
	public void actionPerformedClearService(final MainView mainView) {
		try {
			if (cancelOnDirty(mainView)) { // Aktion abbrechen?
				return;
			}
			mainView.setWaitCursor();
			clearService(mainView);
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
			int chosenOption = JOptionPane.showConfirmDialog(mainView, serviceAbbreviation
					+ getGuiText("RemoveServiceMsg"), Constants.APPLICATION_NAME, JOptionPane.YES_NO_OPTION);
			if (chosenOption == JOptionPane.YES_OPTION) { // Dienst löschen?
				validateServiceAbbreviation(serviceAbbreviation);
				ServiceInfo si = services.removeServiceInfo(serviceAbbreviation);
				if (si == null) { // Dienst gar nicht vorhanden?
					throw new DomainException("ServiceAbbreviationMissingMsg");
				} else {
					services.encrypt(validatedPassphrase);
					FileHelper.getInstance().saveServiceInfoList(servicesFile, services);
					mainView.updateStoredServices();
					putServiceToView(mainView, new ServiceInfo());
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
	 * Öffnet die Hilfe-URL im Browser.
	 */
	public void actionPerformedOpenHelpInBrowser(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			Desktop.getDesktop().browse(new URI(getGuiText("HelpUrl")));
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
	 * Generiert das Passwort und kopiert es in die Zwischenablage.
	 */
	public void actionPerformedCopyPassword(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			final String psw = getValidatedOrGeneratedPassword(mainView);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(psw), null);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Generiert das Passwort und zeigt es an.
	 */
	public void actionPerformedDisplayPassword(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			final String psw = getValidatedOrGeneratedPassword(mainView);
			JOptionPane.showMessageDialog(mainView, getGuiText("DisplayPasswordMsg") + " \"" + psw + "\"",
					Constants.APPLICATION_NAME, JOptionPane.PLAIN_MESSAGE);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Vermerkt die Einstellungen für einen Dienst und speichert alle Dienste.
	 */
	public void actionPerformedStoreService(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			storeService(mainView);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Leert die Einstellungen für das Dienstekürzel, in AdditionalInfo kommt das Tagesdatum.
	 */
	private void clearService(final MainView mainView) {
		ServiceInfo si = new ServiceInfo();
		si.setAdditionalInfo(Constants.DATE_FORMAT.format(new Date()));
		putServiceToView(mainView, si);
	}

	/**
	 * Holt die Werte des Dienstes aus der View.
	 */
	private ServiceInfo getServiceFromView(final MainView mainView) {
		ServiceInfo si = new ServiceInfo(mainView.getServiceAbbreviation());
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
		return si;
	}

	/**
	 * Stellt die Werte eines Dienstes in die View.
	 */
	private void putServiceToView(final MainView mainView, ServiceInfo si) {
		mainView.setServiceAbbreviation(si.getServiceAbbreviation());
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
		mainView.setDirty(false);
	}

	/**
	 * Für die Generierung des Passwortes wird immer eine Default-Menge von Sonderzeichen benutzt. Das wird
	 * hier nur in der GUI reflektiert.
	 */
	private void ensureAtLeastDefaultSpecialCharacters(final MainView mainView) {
		if (EmptyHelper.isEmpty(mainView.getSpecialCharacters())) {
			mainView.setSpecialCharacters(Constants.SPECIAL_CHARS);
		}
	}

	/**
	 * Prüft die Eingabewerte der Passphrase und gibt die Passphrase zurück oder wirft eine Exception.
	 */
	private String validatePassphrase(final StartupView startupView) {
		final String passphrase = startupView.getPassphrase();
		final String passphraseRepeated = startupView.getPassphraseRepeated();
		if (EmptyHelper.isEmpty(passphrase)) {
			throw new DomainException("PassphraseEmptyMsg");
		}
		if (services.isNew()) {
			// Bei einer neuen Daten wird geprüft, ob die Passphrase zweimal gleich eingegeben wurde
			if (!passphrase.equals(passphraseRepeated)) { // Mismatch?
				throw new DomainException("PassphraseMismatchMsg");
			}
		} else {
			// Sonst wird geprüft, ob die Passphrase den Prüfstring entschlüsseln kann
			final String verifierEncrypted = services.getVerifier();
			final String verifierDecrypted = EncryptionHelper.decrypt(passphrase, verifierEncrypted);
			if (!verifierDecrypted.equals(Constants.APPLICATION_VERIFIER)) {
				throw new DomainException("PassphraseInvalidMsg");
			}
		}
		return passphrase;
	}

	/**
	 * Eingabewert des Dienstekürzels überprüfen.
	 */
	private void validateServiceAbbreviation(final String serviceAbbreviation) {
		if (EmptyHelper.isEmpty(serviceAbbreviation)) {
			throw new DomainException("ServiceAbbreviationEmptyMsg");
		}
	}

	/**
	 * Liefert true, wenn die aktuelle Aktion abgebrochen werden soll, oder false, wenn die Änderungen
	 * gespeichert oder verworfen werden sollen.
	 */
	private boolean cancelOnDirty(final MainView mainView) throws IOException {
		if (mainView.isDirty()) {
			int chosenOption = JOptionPane.showConfirmDialog(mainView, mainView.getServiceAbbreviation()
					+ getGuiText("SaveChangesMsg"), Constants.APPLICATION_NAME,
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (chosenOption == JOptionPane.YES_OPTION) { // Geänderte Werte speichern?
				storeService(mainView);
			} else if (chosenOption == JOptionPane.CANCEL_OPTION || chosenOption == JOptionPane.CLOSED_OPTION) {
				return true;
			}
		}
		return false;
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
	 * Liefert das eingegebene oder ein generiertes Passwort. Sobald entweder das Passwort oder das
	 * wiederholte Passwort eingegeben wurden, müssen sie übereinstimmen, sonst wird eine Exception geworfen,
	 * die zu einer Fehlermeldung führt. Eine Eingabe hat also in jedem Fall Vorrang vor der Generierung.
	 */
	private String getValidatedOrGeneratedPassword(final MainView mainView) {
		ensureAtLeastDefaultSpecialCharacters(mainView);
		return PasswordFactory.getPassword(getServiceFromView(mainView), validatedPassphrase);
	}

	/**
	 * Werte des Dienstes in die Liste übernehmen und die gesamte Liste speichern.
	 */
	private void storeService(final MainView mainView) throws IOException {
		String serviceAbbreviation = mainView.getServiceAbbreviation();
		validateServiceAbbreviation(serviceAbbreviation);
		if (services.getServiceInfo(serviceAbbreviation) != null) { // Ist der Dienst bereits vermerkt?
			int chosenOption = JOptionPane.showConfirmDialog(mainView, serviceAbbreviation
					+ getGuiText("OverwriteServiceMsg"), Constants.APPLICATION_NAME,
					JOptionPane.YES_NO_OPTION);
			if (chosenOption == JOptionPane.NO_OPTION) { // Dienst nicht überschreiben? => fertig
				return;
			}
		}
		services.putServiceInfo(getServiceFromView(mainView));
		services.encrypt(validatedPassphrase);
		FileHelper.getInstance().saveServiceInfoList(servicesFile, services);
		mainView.setDirty(false);
		mainView.updateStoredServices();
	}

	public ServiceInfoList getServices() {
		return services;
	}

}
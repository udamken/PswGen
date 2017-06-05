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
package de.dknapps.pswgendesktop.gui;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import de.dknapps.pswgencore.CoreConstants;
import de.dknapps.pswgencore.model.ServiceInfo;
import de.dknapps.pswgencore.model.ServiceInfoList;
import de.dknapps.pswgencore.util.DomainException;
import de.dknapps.pswgencore.util.EmptyHelper;
import de.dknapps.pswgencore.util.EncryptionHelper;
import de.dknapps.pswgencore.util.FileHelper;
import de.dknapps.pswgencore.util.PasswordFactory;
import de.dknapps.pswgendesktop.DesktopConstants;
import de.dknapps.pswgendesktop.gui.base.BaseCtl;
import de.dknapps.pswgendesktop.gui.base.BaseView;
import de.dknapps.pswgendesktop.util.CommonJsonReaderWriterFactoryGsonImpl;

public class PswGenCtl extends BaseCtl {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(DesktopConstants.LOGGER_NAME);

	/** Alle Informationen zu Dienstekürzeln */
	private ServiceInfoList services = new ServiceInfoList();

	/** Der Dateiname zum Laden und Speichern der Diensteliste */
	private File servicesFile;

	/** Die in der StartupView eingegebene Passphrase */
	private String validatedPassphrase;

	/** Die in der StartupView eingegebene alte Passphrase */
	private String oldPassphrase;

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
			LOGGER.log(Level.WARNING, CoreConstants.MSG_EXCP_LOOK_AND_FEEL, e);
		}
	}

	/**
	 * Datei lediglich in das neue Format konvertieren, dann endet die Anwendung.
	 */
	public void upgradeServiceInfoList(final String passphrase) throws IOException {
		FileHelper fileHelper = FileHelper.getInstance(new CommonJsonReaderWriterFactoryGsonImpl());
		services = fileHelper.loadServiceInfoList(servicesFile);
		if (services == null || services.isNew()) {
			String message = getGuiText("EmptyFileNotUpgradableMsg");
			JOptionPane.showMessageDialog(null, message, DesktopConstants.APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, message);
			System.exit(16);
		} else if (services.isUnsupportedFormat()) {
			String message = getGuiText("UnsupportedFileFormatMsg");
			JOptionPane.showMessageDialog(null, message, DesktopConstants.APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, message);
			System.exit(16);
		} else if (services.isAdvancedFormat()) { // Schon im neuen Format? => nichts zu tun
			String message = getGuiText("FileFormatAlreadyConvertedMsg");
			JOptionPane.showMessageDialog(null, message, DesktopConstants.APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, message);
			System.exit(16);
		}
		try {
			// Geladene Services mit einem EncryptionHelper auf die bisherige Art entschlüsseln
			EncryptionHelper encryptionHelper = new EncryptionHelper(passphrase.toCharArray(), null, null);
			services.decrypt(encryptionHelper);
		} catch (DomainException e) {
			LOGGER.log(Level.SEVERE, CoreConstants.MSG_PASSPHRASE_INVALID + e);
			System.exit(16);
		}
		// Alte Datei zur Sicherheit durch Umbenennen aufbewahren
		Files.move(servicesFile.toPath(), (new File(servicesFile.getPath() + ".upgraded")).toPath());
		// Service in eine neue Datei mit neuer Verschlüsselungsart speichern
		saveServiceInfoList(passphrase);
		JOptionPane.showMessageDialog(null, getGuiText("FileFormatSuccessfullyConvertedMsg"),
				DesktopConstants.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Lädt die Dienstedatei, fragt die Passphrase ab und öffnet das Hauptfenster. Die Anwendung endet nach
	 * dieser Methode nicht, das Beenden geschieht über die Oberfläche.
	 */
	public void start() {
		FileHelper fileHelper = FileHelper.getInstance(new CommonJsonReaderWriterFactoryGsonImpl());
		services = fileHelper.loadServiceInfoList(servicesFile);
		if (services == null) {
			// Datei ist nicht (als JSON) lesbar
			String message = getGuiText("UnknownFileFormatMsg");
			JOptionPane.showMessageDialog(null, message, DesktopConstants.APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, message);
			System.exit(20);
		} else if (services.isNew()) {
			// Ein frischer Anfang, beim Speichern wird eine neue Datei angelegt werden
		} else if (services.isUnsupportedFormat()) {
			// Dateiformat wird nicht mehr unterstützt und muss mit einer alten Version konvertiert werden
			String message = getGuiText("UnsupportedFileFormatMsg");
			JOptionPane.showMessageDialog(null, message, DesktopConstants.APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, message);
			System.exit(16);
		} else if (services.isAdvancedFormat()) {
			// Datei hat das aktuelle Format, es ist nichts weiter zu tun
		} else {
			// Datei hat ein altes Format und muss erst mit -upgrade konvertiert werden
			String message = getGuiText("FileFormatMustBeConvertedMsg");
			JOptionPane.showMessageDialog(null, message, DesktopConstants.APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, message);
			System.exit(16);
		}
		StartupDialog startupDialog = new StartupDialog(this);
		startupDialog.setTitle(DesktopConstants.APPLICATION_NAME + " " + CoreConstants.APPLICATION_VERSION);
		if (!services.isNew()) { // Keine neue Datei?
			startupDialog.disablePassphraseRepeated(); // Passphrase nur 1x eingeben!
		}
		startupDialog.setContainsServiceWithOldPassphrase(services.containsServiceWithOldPassphrase());
		addWindow(startupDialog);
		startupDialog.pack();
		startupDialog.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.dknapps.pswgendesktop.gui.base.BaseCtl#windowClosing(de.dknapps.pswgendesktop.gui.base.BaseView)
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
	 * Prüft die Passphrase, entschlüsselt die Services und öffnet ggf. das Hauptfenster.
	 */
	public void actionPerformedOpenServices(final StartupDialog startupDialog) {
		try {
			validatedPassphrase = validatePassphraseAndDecryptServices(startupDialog);
			oldPassphrase = validateOldPassphrase(startupDialog);
			startupDialog.dispose();
			MainView mainView = new MainView(this);
			mainView.setTitle(servicesFile.getAbsolutePath() + " - " + DesktopConstants.APPLICATION_NAME + " "
					+ CoreConstants.APPLICATION_VERSION);
			addWindow(mainView);
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
	 * Prüft die Passphrase, entschlüsselt die Services und öffnet dann den Dialog zur Eingabe der neuen
	 * Passphrase.
	 */
	public void actionPerformedChangePassphrase(final StartupDialog startupDialog) {
		try {
			validatedPassphrase = validatePassphraseAndDecryptServices(startupDialog);
			startupDialog.dispose();
			ChangePassphraseDialog changePassphraseDialog = new ChangePassphraseDialog(this);
			changePassphraseDialog
					.setTitle(DesktopConstants.APPLICATION_NAME + " " + CoreConstants.APPLICATION_VERSION);
			addWindow(changePassphraseDialog);
			changePassphraseDialog.pack();
			changePassphraseDialog.setVisible(true);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			// Nichts mehr zu tun
		}
	}

	/**
	 * speichert sie ggf. unter einer neu einzugebenden Passphrase wieder ab und beendet die Anwendung
	 * anschließend Speichert alle Dienste (mit der neuen Passphrase).
	 */
	public void actionPerformedStoreServices(final ChangePassphraseDialog changePassphraseDialog) {
		try {
			validatedPassphrase = validateNewPassphrase(changePassphraseDialog);
			changePassphraseDialog.dispose();
			// Services, bei denen das Passwort generiert wird, auf UseOldPassphrase setzen
			for (ServiceInfo si : services.getServices()) {
				if (EmptyHelper.isEmpty(si.getPassword())) {
					si.setUseOldPassphrase(true); // Passwort ab sofort mit der alten Passphrase erzeugen
				}
			}
			// Alte Datei zur Sicherheit durch Umbenennen aufbewahren
			Files.move(servicesFile.toPath(), (new File(servicesFile.getPath() + ".rephrased")).toPath());
			// Neu verschlüsselt speichern
			saveServiceInfoList(validatedPassphrase);
			// Von vorne neu beginnen
			start();
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
			int chosenOption = JOptionPane.showConfirmDialog(mainView,
					serviceAbbreviation + getGuiText("RemoveServiceMsg"), DesktopConstants.APPLICATION_NAME,
					JOptionPane.YES_NO_OPTION);
			if (chosenOption == JOptionPane.YES_OPTION) { // Dienst löschen?
				validateServiceAbbreviation(serviceAbbreviation);
				ServiceInfo si = services.removeServiceInfo(serviceAbbreviation);
				if (si == null) { // Dienst gar nicht vorhanden?
					throw new DomainException("ServiceAbbreviationMissingMsg");
				} else {
					saveServiceInfoList(validatedPassphrase);
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
	public void actionPerformedOpenAbout(@SuppressWarnings("unused") final MainView mainView) {
		try {
			AboutView aboutView = new AboutView(this);
			addWindow(aboutView);
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
			copyPassword(mainView);
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
			PasswordDialog passwordDialog = new PasswordDialog(this);
			passwordDialog.setTitle(mainView.getServiceAbbreviation() + " - "
					+ DesktopConstants.APPLICATION_NAME + " " + CoreConstants.APPLICATION_VERSION);
			passwordDialog.setPassword(psw);
			passwordDialog.setPasswordExplanation(getPasswordExplanation(psw));
			addWindow(passwordDialog);
			passwordDialog.pack();
			passwordDialog.setVisible(true);
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
	 * Generiert das Passwort und kopiert es in die Zwischenablage, dann wird AdditionalInfo mit dem
	 * Tagesdatum gefüllt und der Dienst von der Verwendung der alten auf die Verwendung der neuen Passphrase
	 * umgestellt.
	 */
	public void actionPerformedUseNewPassphrase(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			copyPassword(mainView);
			ServiceInfo si = getServiceFromView(mainView);
			resetAdditionalInfo(si);
			si.setUseOldPassphrase(false);
			putServiceToView(mainView, si);
			mainView.setDirty(true);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * 
	 */
	public void actionPerformedPasswordOk(PasswordDialog passwordDialog) {
		passwordDialog.dispose();
	}

	/**
	 * Leert die Einstellungen für das Dienstekürzel, in AdditionalInfo kommt das Tagesdatum.
	 */
	private void clearService(final MainView mainView) {
		ServiceInfo si = new ServiceInfo();
		resetAdditionalInfo(si);
		putServiceToView(mainView, si);
	}

	/**
	 * Stellt das Tagesdatum in das Feld AdditionalInfo.
	 */
	private void resetAdditionalInfo(ServiceInfo si) {
		si.setAdditionalInfo(CoreConstants.DATE_FORMAT.format(new Date()));
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
		si.setUseOldPassphrase(mainView.getUseOldPassphrase());
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
		mainView.setUseOldPassphrase(si.isUseOldPassphrase());
		mainView.setDirty(false);
	}

	/**
	 * Für die Generierung des Passwortes wird immer eine Default-Menge von Sonderzeichen benutzt. Das wird
	 * hier nur in der GUI reflektiert.
	 */
	private void ensureAtLeastDefaultSpecialCharacters(final MainView mainView) {
		if (EmptyHelper.isEmpty(mainView.getSpecialCharacters())) {
			mainView.setSpecialCharacters(CoreConstants.SPECIAL_CHARS);
		}
	}

	/**
	 * Prüft die Eingabewerte der Passphrase, entschlüsselt die Services und gibt die Passphrase zurück oder
	 * wirft eine Exception.
	 */
	private String validatePassphraseAndDecryptServices(final StartupDialog startupView) {
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
			EncryptionHelper encryptionHelper = new EncryptionHelper(passphrase.toCharArray(),
					services.getSaltAsHexString(), services.getInitializerAsHexString());
			services.decrypt(encryptionHelper); // Info-Collection entschlüsselt in Map stellen
		}
		return passphrase;
	}

	/**
	 * Prüft die Eingabewerte der alten Passphrase und gibt die alte Passphrase zurück oder wirft eine
	 * Exception.
	 */
	private String validateOldPassphrase(final StartupDialog startupView) {
		final String oldPassphrase = startupView.getOldPassphrase();
		if (EmptyHelper.isEmpty(oldPassphrase)) {
			throw new DomainException("OldPassphraseEmptyMsg");
		}
		return oldPassphrase;
	}

	/**
	 * Prüft die Eingabewerte der Passphrase, entschlüsselt die Services und gibt die Passphrase zurück oder
	 * wirft eine Exception.
	 */
	private String validateNewPassphrase(final ChangePassphraseDialog changePassphraseDialog) {
		final String newPassphrase = changePassphraseDialog.getNewPassphrase();
		final String newPassphraseRepeated = changePassphraseDialog.getNewPassphraseRepeated();
		if (EmptyHelper.isEmpty(newPassphrase)) {
			throw new DomainException("NewPassphraseEmptyMsg");
		}
		// Prüfen, ob die neue Passphrase zweimal gleich eingegeben wurde
		if (!newPassphrase.equals(newPassphraseRepeated)) { // Mismatch?
			throw new DomainException("NewPassphraseMismatchMsg");
		}
		return newPassphrase;
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
			int chosenOption = JOptionPane.showConfirmDialog(mainView,
					mainView.getServiceAbbreviation() + getGuiText("SaveChangesMsg"),
					DesktopConstants.APPLICATION_NAME, JOptionPane.YES_NO_CANCEL_OPTION);
			if (chosenOption == JOptionPane.YES_OPTION) { // Geänderte Werte speichern?
				storeService(mainView);
			} else if (chosenOption == JOptionPane.CANCEL_OPTION
					|| chosenOption == JOptionPane.CLOSED_OPTION) {
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
		ServiceInfo si = getServiceFromView(mainView);
		String passphrase = (si.isUseOldPassphrase()) ? oldPassphrase : validatedPassphrase;
		return PasswordFactory.getPassword(si, passphrase);
	}

	/**
	 * Liefert eine immer lesbare Erläuterung zum übergebenen Passwort.
	 */
	private String getPasswordExplanation(String password) {
		final String prefixLowercaseLetters = getGuiText("PrefixLowercaseLetters");
		final String prefixUppercaseLetters = getGuiText("PrefixUppercaseLetters");
		final String prefixDigits = getGuiText("PrefixDigits");
		final String prefixSpecialChars = getGuiText("PrefixSpecialChars");
		return PasswordFactory.getPasswordExplanation(password, prefixLowercaseLetters,
				prefixUppercaseLetters, prefixDigits, prefixSpecialChars);
	}

	/**
	 * Generiert das Passwort und kopiert es in die Zwischenablage.
	 */
	private void copyPassword(final MainView mainView) {
		final String psw = getValidatedOrGeneratedPassword(mainView);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(psw), null);
	}

	/**
	 * Werte des Dienstes in die Liste übernehmen und die gesamte Liste speichern.
	 */
	private void storeService(final MainView mainView) throws IOException {
		String serviceAbbreviation = mainView.getServiceAbbreviation();
		validateServiceAbbreviation(serviceAbbreviation);
		if (services.getServiceInfo(serviceAbbreviation) != null) { // Ist der Dienst bereits vermerkt?
			int chosenOption = JOptionPane.showConfirmDialog(mainView,
					serviceAbbreviation + getGuiText("OverwriteServiceMsg"),
					DesktopConstants.APPLICATION_NAME, JOptionPane.YES_NO_OPTION);
			if (chosenOption == JOptionPane.NO_OPTION) { // Dienst nicht überschreiben? => fertig
				return;
			}
		}
		services.putServiceInfo(getServiceFromView(mainView));
		saveServiceInfoList(validatedPassphrase);
		mainView.setDirty(false);
		mainView.updateStoredServices();
	}

	/**
	 * Liste der Diente in eine Datei speichern
	 */
	private void saveServiceInfoList(final String passphrase) throws IOException {
		FileHelper fileHelper = FileHelper.getInstance(new CommonJsonReaderWriterFactoryGsonImpl());
		EncryptionHelper encryptionHelper = new EncryptionHelper(passphrase.toCharArray());
		services.setSaltAsHexString(encryptionHelper.getSaltAsHexString());
		services.setInitializerAsHexString(encryptionHelper.getInitializerAsHexString());
		services.encrypt(encryptionHelper);
		fileHelper.saveServiceInfoList(servicesFile, services);
	}

	/**
	 * Liefert die Liste der Dienste
	 */
	public ServiceInfoList getServices() {
		return services;
	}

}
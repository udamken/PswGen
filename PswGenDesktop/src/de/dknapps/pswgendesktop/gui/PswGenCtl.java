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
package de.dknapps.pswgendesktop.gui;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import de.dknapps.pswgencore.CoreConstants;
import de.dknapps.pswgencore.model.ServiceInfo;
import de.dknapps.pswgencore.model.ServiceInfoList;
import de.dknapps.pswgencore.util.DomainException;
import de.dknapps.pswgencore.util.EmptyHelper;
import de.dknapps.pswgencore.util.FileHelper;
import de.dknapps.pswgencore.util.PasswordFactory;
import de.dknapps.pswgendesktop.DesktopConstants;
import de.dknapps.pswgendesktop.gui.base.BaseCtl;
import de.dknapps.pswgendesktop.gui.base.BaseView;
import de.dknapps.pswgendesktop.util.CommonJsonReaderWriterFactoryGsonImpl;

public class PswGenCtl extends BaseCtl {

	/** Alle Informationen zu Dienstekürzeln */
	private ServiceInfoList services = new ServiceInfoList();

	/** Der Dateiname zum Laden und Speichern der Diensteliste */
	private File servicesFile;

	/** Der Dateiname zum Laden der Diensteliste vom anderen Gerät */
	private File otherServicesFile;

	/** Die in der StartupView eingegebene Passphrase */
	private String validatedPassphrase;

	/** Die in der StartupView eingegebene alte Passphrase */
	private String oldPassphrase;

	/**
	 * Erzeugt einen Controller.
	 */
	public PswGenCtl(final String servicesFilename, final String otherServicesFilename) {
		super();
		servicesFile = new File(servicesFilename);
		otherServicesFile = new File(otherServicesFilename);
		setupLookAndFeel();
	}

	/**
	 * Setzt das Look&Feel auf den System-Standard.
	 */
	private void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.WARNING, CoreConstants.MSG_EXCP_LOOK_AND_FEEL, e);
		}
	}

	/**
	 * Fragt die aktuelle und ggf. alte Passphrase ab und öffnet das Hauptfenster. Die Anwendung endet nach
	 * dieser Methode nicht, das Beenden geschieht über die Oberfläche.
	 */
	public void start() {
		StartupDialog startupDialog = new StartupDialog(this);
		startupDialog.setTitle(DesktopConstants.APPLICATION_NAME + " " + CoreConstants.APPLICATION_VERSION);
		startupDialog.setFilepath(servicesFile.getAbsolutePath());
		startupDialog.setFilepathInfo(deriveInfo(servicesFile));
		startupDialog.setOtherFilepath(otherServicesFile.getAbsolutePath());
		startupDialog.setOtherFilepathInfo(deriveInfo(otherServicesFile));
		if (servicesFile.exists()) { // Datei bereits vorhanden?
			startupDialog.disablePassphraseRepeated(); // Passphrase nur 1x eingeben!
		}
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
		} catch (Exception e) {
			handleException(e);
		} finally {
			// Nichts mehr zu tun
		}
	}

	/**
	 * Prüft die Passphrase, entschlüsselt die Services und öffnet ggf. das Hauptfenster.
	 */
	public void actionPerformedOpenServices(final StartupDialog startupDialog) {
		try {
			validatedPassphrase = validatePassphrase(startupDialog);
			loadServices();
			oldPassphrase = validateOldPassphrase(startupDialog);
			startupDialog.dispose();
			MainView mainView = new MainView(this);
			mainView.setTitle(servicesFile.getAbsolutePath() + " - " + DesktopConstants.APPLICATION_NAME + " "
					+ CoreConstants.APPLICATION_VERSION);
			addWindow(mainView);
			mainView.pack();
			clearService(mainView); // Diensteinstellungen initialisieren (Tagesdatum)
			mainView.setVisible(true);
		} catch (Exception e) {
			handleException(e);
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
			validatedPassphrase = validatePassphrase(startupDialog);
			loadServices();
			startupDialog.dispose();
			ChangePassphraseDialog changePassphraseDialog = new ChangePassphraseDialog(this);
			changePassphraseDialog
					.setTitle(DesktopConstants.APPLICATION_NAME + " " + CoreConstants.APPLICATION_VERSION);
			addWindow(changePassphraseDialog);
			changePassphraseDialog.pack();
			changePassphraseDialog.setVisible(true);
		} catch (Exception e) {
			handleException(e);
		} finally {
			// Nichts mehr zu tun
		}
	}

	/**
	 * Speichert die Liste der Dienste ggf. unter einer neu einzugebenden Passphrase wieder ab und startet die
	 * Anwendung anschließend praktisch neu.
	 */
	public void actionPerformedStoreServices(final ChangePassphraseDialog changePassphraseDialog) {
		try {
			// FIXME dkn Es darf kein Dienst mehr mit alten Passwort existieren, dass war vorher in der GUI
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
		} catch (Exception e) {
			handleException(e);
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
		} catch (Exception e) {
			handleException(e);
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
		} catch (Exception e) {
			handleException(e);
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
			String abbreviation = mainView.getServiceAbbreviation();
			validateServiceAbbreviation(abbreviation);
			int chosenOption = JOptionPane.showConfirmDialog(mainView,
					abbreviation + getGuiText("RemoveServiceMsg"), DesktopConstants.APPLICATION_NAME,
					JOptionPane.OK_CANCEL_OPTION);
			if (chosenOption == JOptionPane.OK_OPTION) { // Dienst löschen?
				services.removeServiceInfo(abbreviation);
				saveServiceInfoList(validatedPassphrase);
				mainView.updateStoredServices();
				clearService(mainView);
			}
		} catch (Exception e) {
			handleException(e);
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
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(loginUrl));
			} else {
				// BROWSE not supported on Kubuntu ... see https://stackoverflow.com/a/18509384
				Runtime.getRuntime().exec("xdg-open " + loginUrl);
			}
			copyLoginInfo(mainView);
		} catch (Exception e) {
			handleException(e);
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
		} catch (Exception e) {
			handleException(e);
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
		} catch (Exception e) {
			handleException(e);
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
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * Generiert das Passwort und kopiert es in die Zwischenablage.
	 */
	public void actionPerformedCopyPassword(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			copyPassword(mainView);
		} catch (Exception e) {
			handleException(e);
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
		} catch (Exception e) {
			handleException(e);
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
		} catch (Exception e) {
			handleException(e);
		} finally {
			mainView.setDefaultCursor();
		}
	}

	/**
	 * Füllt AdditionalInfo mit dem Tagesdatum und stellt den Dienst von der Verwendung der alten auf die
	 * Verwendung der neuen Passphrase um.
	 */
	public void actionPerformedUseNewPassphrase(final MainView mainView) {
		try {
			mainView.setWaitCursor();
			ServiceInfo si = getServiceFromView(mainView);
			si.resetAdditionalInfo();
			si.setUseOldPassphrase(false);
			putServiceToView(mainView, si);
			mainView.setDirty(true);
		} catch (Exception e) {
			handleException(e);
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
	 * Returns displayable information about the file specified by the given filepath.
	 */
	private String deriveInfo(File file) {
		if (!file.exists()) {
			return getGuiText("msg_file_cannot_be_found");
		} else if (!file.canRead()) {
			return getGuiText("msg_file_cannot_be_read");
		} else {
			return getGuiText("msg_file_last_modified") + " "
					+ CoreConstants.TIMESTAMP_FORMAT.format(file.lastModified());
		}
	}

	/**
	 * Leert die Einstellungen für das Dienstekürzel, in AdditionalInfo kommt das Tagesdatum.
	 */
	private void clearService(final MainView mainView) {
		ServiceInfo si = new ServiceInfo();
		si.resetAdditionalInfo();
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
		si.setSmallLettersCount(mainView.getSmallLettersCount());
		si.setSmallLettersStartIndex(mainView.getSmallLettersStartIndex());
		si.setSmallLettersEndIndex(mainView.getSmallLettersEndIndex());
		si.setUseCapitalLetters(mainView.getUseCapitalLetters());
		si.setCapitalLettersCount(mainView.getCapitalLettersCount());
		si.setCapitalLettersStartIndex(mainView.getCapitalLettersStartIndex());
		si.setCapitalLettersEndIndex(mainView.getCapitalLettersEndIndex());
		si.setUseDigits(mainView.getUseDigits());
		si.setDigitsCount(mainView.getDigitsCount());
		si.setDigitsStartIndex(mainView.getDigitsStartIndex());
		si.setDigitsEndIndex(mainView.getDigitsEndIndex());
		si.setUseSpecialCharacters(mainView.getUseSpecialCharacters());
		si.setSpecialCharactersCount(mainView.getSpecialCharactersCount());
		ensureAtLeastDefaultSpecialCharacters(mainView);
		si.setSpecialCharacters(mainView.getSpecialCharacters());
		si.setSpecialCharactersStartIndex(mainView.getSpecialCharactersStartIndex());
		si.setSpecialCharactersEndIndex(mainView.getSpecialCharactersEndIndex());
		si.setTotalCharacterCount(mainView.getTotalCharacterCount());
		si.setPassword(mainView.getPassword());
		si.setPasswordRepeated(mainView.getPasswordRepeated());
		si.setUseOldPassphrase(mainView.getUseOldPassphrase());
		// last update is set not set from the view but from outside
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
		mainView.setSmallLettersCount(si.getSmallLettersCount());
		mainView.setSmallLettersStartIndex(si.getSmallLettersStartIndex());
		mainView.setSmallLettersEndIndex(si.getSmallLettersEndIndex());
		mainView.setUseCapitalLetters(si.isUseCapitalLetters());
		mainView.setCapitalLettersCount(si.getCapitalLettersCount());
		mainView.setCapitalLettersStartIndex(si.getCapitalLettersStartIndex());
		mainView.setCapitalLettersEndIndex(si.getCapitalLettersEndIndex());
		mainView.setUseDigits(si.isUseDigits());
		mainView.setDigitsCount(si.getDigitsCount());
		mainView.setDigitsStartIndex(si.getDigitsStartIndex());
		mainView.setDigitsEndIndex(si.getDigitsEndIndex());
		mainView.setUseSpecialCharacters(si.isUseSpecialCharacters());
		mainView.setSpecialCharacters(si.getSpecialCharacters());
		ensureAtLeastDefaultSpecialCharacters(mainView);
		mainView.setSpecialCharactersCount(si.getSpecialCharactersCount());
		mainView.setSpecialCharactersStartIndex(si.getSpecialCharactersStartIndex());
		mainView.setSpecialCharactersEndIndex(si.getSpecialCharactersEndIndex());
		mainView.setTotalCharacterCount(si.getTotalCharacterCount());
		mainView.setPassword(si.getPassword());
		mainView.setPasswordRepeated(si.getPasswordRepeated());
		mainView.setUseOldPassphrase(si.isUseOldPassphrase());
		mainView.setLastUpdate(si.getLastUpdate());
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
	 * Prüft die Eingabewerte der Passphrase und gibt die Passphrase zurück oder wirft eine Exception.
	 */
	private String validatePassphrase(final StartupDialog startupDialog) {
		final String passphrase = startupDialog.getPassphrase();
		final String passphraseRepeated = startupDialog.getPassphraseRepeated();
		if (EmptyHelper.isEmpty(passphrase)) {
			throw new DomainException("PassphraseEmptyMsg");
		}
		if (!servicesFile.exists()) { // Datei (noch) nicht vorhanden?
			// Bei einer neuen Datei wird geprüft, ob die Passphrase zweimal gleich eingegeben wurde
			if (!passphrase.equals(passphraseRepeated)) { // Mismatch?
				throw new DomainException("NewPassphraseMismatchMsg");
			}
		}
		return passphrase;
	}

	/**
	 * Lädt und entschlüsselt die Dienste.
	 */
	private void loadServices() {
		FileHelper fileHelper = FileHelper.getInstance(new CommonJsonReaderWriterFactoryGsonImpl());
		services = fileHelper.loadServiceInfoList(servicesFile, otherServicesFile, validatedPassphrase);
	}

	/**
	 * Prüft die Eingabewerte der alten Passphrase und gibt die alte Passphrase zurück oder wirft eine
	 * Exception.
	 */
	private String validateOldPassphrase(final StartupDialog startupDialog) {
		final String passphrase = startupDialog.getOldPassphrase();
		if (services.containsServiceWithOldPassphrase()) {
			if (EmptyHelper.isEmpty(passphrase)) {
				throw new DomainException("OldPassphraseEmptyMsg");
			}
		}
		return passphrase;
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
	private boolean cancelOnDirty(final MainView mainView) {
		if (mainView.isDirty()) {
			int chosenOption = JOptionPane.showConfirmDialog(mainView, getGuiText("DiscardChangesMsg"),
					DesktopConstants.APPLICATION_NAME, JOptionPane.OK_CANCEL_OPTION);
			return chosenOption != JOptionPane.OK_OPTION;
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
		String abbreviation = mainView.getServiceAbbreviation();
		validateServiceAbbreviation(abbreviation);
		if (services.getServiceInfo(abbreviation) != null) { // Ist der Dienst bereits vermerkt?
			int chosenOption = JOptionPane.showConfirmDialog(mainView,
					abbreviation + getGuiText("OverwriteServiceMsg"), DesktopConstants.APPLICATION_NAME,
					JOptionPane.OK_CANCEL_OPTION);
			if (chosenOption != JOptionPane.OK_OPTION) { // Dienst nicht überschreiben? => fertig
				return;
			}
		}
		ServiceInfo si = getServiceFromView(mainView);
		si.setDeleted(false);
		si.resetTimeMillis();
		services.putServiceInfo(si);
		saveServiceInfoList(validatedPassphrase);
		mainView.setDirty(false);
		mainView.updateStoredServices();
		putServiceToView(mainView, si); // update timestamp
	}

	/**
	 * Liste der Diente in eine Datei speichern
	 */
	private void saveServiceInfoList(final String passphrase) throws IOException {
		FileHelper fileHelper = FileHelper.getInstance(new CommonJsonReaderWriterFactoryGsonImpl());
		fileHelper.saveServiceInfoList(servicesFile, services, passphrase);
	}

	/**
	 * Liefert die Liste der Dienste
	 */
	public ServiceInfoList getServices() {
		return services;
	}

}
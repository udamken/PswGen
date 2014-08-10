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
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.pswgen.gui.base.BaseCtl;
import net.sf.pswgen.gui.base.BaseView;
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
 * Copyright (C) 2005-2014 Uwe Damken
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
					saveServiceInfoList();
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
	 * Lädt alle Diensteinformationen.
	 */
	private void loadServiceInfoList() {
		try {
			JAXBContext context = JAXBContext.newInstance(ServiceInfoList.class);
			Unmarshaller um = context.createUnmarshaller();
			if (!servicesFile.exists()) { // Datei gibt's nicht? => Leere Liste erzeugen
				services = new ServiceInfoList();
			} else {
				FileInputStream in = new FileInputStream(servicesFile);
				services = (ServiceInfoList) um.unmarshal(in);
				in.close();
				if (!services.isAdvancedFormat()) { // Noch im alten Format? => Konvertieren
					for (ServiceInfo si : services.getEncryptedServices()) { // Sind noch verschlüsselt ...
						if (si.isUseSpecialCharacters()
								&& EmptyHelper.isEmpty(si.getSpecialCharactersCount())) {
							si.setSpecialCharactersCount(1); // für Sonderzeichen war 1 der Default
						}
					}
					// Siehe saveServiceInfoList() für Konvertierungen, die die Passphrase benötigen
				}
			}
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
	 * Sonderzeichen müssen gesetzt sein, und wenn es nur eine Default-Auswahl ist.
	 */
	private void ensureAtLeastDefaultSpecialCharacters(final MainView mainView) {
		if (mainView.getSpecialCharacters() == null || mainView.getSpecialCharacters().length() == 0) {
			mainView.setSpecialCharacters(Constants.SPECIAL_CHARS);
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
	 * Eingabewert des Dienstekürzels überprüfen.
	 */
	private void validateServiceAbbreviation(final String serviceAbbreviation) {
		if (serviceAbbreviation.length() == 0) {
			throw new DomainException("ServiceAbbreviationEmptyMsg");
		}
	}

	/**
	 * Liefert true, wenn die aktuelle Aktion abgebrochen werden soll, oder false, wenn die Änderungen
	 * gespeichert oder verworfen werden sollen.
	 */
	private boolean cancelOnDirty(final MainView mainView) {
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
		String password = mainView.getPassword();
		final String passwordRepeated = mainView.getPasswordRepeated();
		if (password.length() == 0 && passwordRepeated.length() == 0) { // Beide leer? => generieren
			mainView.setWaitCursor();
			ensureAtLeastDefaultSpecialCharacters(mainView);
			validateServiceAbbreviation(mainView.getServiceAbbreviation());
			password = PasswordFactory.getPassword(getServiceFromView(mainView), validatedPassphrase);
		} else {
			if (!password.equals(passwordRepeated)) { // Mismatch?
				throw new DomainException("PasswordMismatchMsg");
			}
		}
		return password;
	}

	/**
	 * Werte des Dienstes in die Liste übernehmen und die gesamte Liste speichern.
	 */
	private void storeService(final MainView mainView) {
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
		saveServiceInfoList();
		mainView.setDirty(false);
		mainView.updateStoredServices();
	}

	public ServiceInfoList getServices() {
		return services;
	}

}
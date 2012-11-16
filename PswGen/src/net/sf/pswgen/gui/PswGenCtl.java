package net.sf.pswgen.gui;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.pswgen.base.gui.BaseCtl;
import net.sf.pswgen.base.util.DomainException;
import net.sf.pswgen.base.util.EmptyHelper;
import net.sf.pswgen.base.util.Services;
import net.sf.pswgen.model.ServiceInfo;
import net.sf.pswgen.model.ServiceInfoList;
import net.sf.pswgen.util.Constants;
import net.sf.pswgen.util.EncryptionHelper;
import net.sf.pswgen.util.PasswordFactory;

/**
 * <p>
 * Der Controller der Anwendung. Hier werden sämtliche Dialoge gesteuert und die Anwendungsfälle
 * implementiert. Als Alternative zu der Idee, je Dialog einen Controller zu implementieren. Eventuell wird
 * die Klasse aber auch mal ein bisschen groß und die Anwendungslogik muss ausgelagert werden.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
public class PswGenCtl extends BaseCtl {

	/** Das Hauptfenster dieser Anwendung */
	// private MFView mfView = null;
	/** Alle Informationen zu Dienstekürzeln */
	private ServiceInfoList services = new ServiceInfoList();

	/** Der Dateiname zum Laden und Speichern der Diensteliste */
	private File servicesFile;

	/**
	 * Eingabewerte der Passphrase prüfen.
	 */
	private void validatePassphrase(final MFView mfView) {
		final String passphrase = mfView.getPassphrase();
		final String passphraseRepeated = mfView.getPassphraseRepeated();
		if (!passphrase.equals(passphraseRepeated)) { // Mismatch?
			throw new DomainException("PassphraseMismatchMsg");
		}
		if (passphrase.length() == 0) {
			throw new DomainException("PassphraseEmptyMsg");
		}
	}

	/**
	 * Überprüft die Passphrase beim Verlassen des Kontrolleingabefeldes und liefert bei korrekter Eingabe
	 * true.
	 */
	public boolean focusLostValidatePassphrase(final MFView mfView) {
		try {
			mfView.setWaitCursor();
			validatePassphrase(mfView);
			return true;
		} catch (Throwable t) {
			handleThrowable(t);
			return false;
		} finally {
			mfView.setDefaultCursor();
		}
	}

	/**
	 * Liefert das eingegebene oder ein generiertes Passwort. Sobald entweder das Passwort oder das
	 * wiederholte Passwort eingegeben wurden, müssen sie übereinstimmen, sonst wird eine Exception geworfen,
	 * die zu einer Fehlermeldung führt. Eine Eingabe hat also in jedem Fall Vorrang vor der Generierung.
	 */
	private String validatedOrGeneratePassword(final MFView mfView) {
		String password = mfView.getPassword();
		final String passwordRepeated = mfView.getPasswordRepeated();
		if (password.length() == 0 && passwordRepeated.length() == 0) { // Beide leer? => generieren
			password = generatePassword(mfView);
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
			services.reinforceLoad(); // Laden nachbereiten (Collection in Map stellen)
			in.close();
		} catch (Exception e) {
			Services.getInstance().getLogger().log(Level.WARNING, Constants.MSG_EXCP_SERVICES, e);
		}
	}

	/**
	 * Speichert alle Diensteinformationen.
	 */
	private void saveServiceInfoList() {
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext.newInstance(ServiceInfoList.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			FileOutputStream out = new FileOutputStream(servicesFile);
			services.prepareSave(); // Speichern vorbereiten, Map in Collection stellen
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
	public void actionPerformedLoadService(final MFView mfView) {
		try {
			mfView.setWaitCursor();
			loadService(mfView);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mfView.setDefaultCursor();
		}
	}

	/**
	 * Lädt die Einstellungen für ein Dienstekürzel.
	 */
	private void loadService(final MFView mfView) {
		validatePassphrase(mfView); // benötigt zum Entschlüssen von LoginInfo
		String serviceAbbreviation = mfView.getServiceAbbreviation();
		validateServiceAbbreviation(serviceAbbreviation);
		ServiceInfo si = services.getServiceInfo(serviceAbbreviation);
		if (si == null) {
			throw new DomainException("ServiceAbbreviationMissingMsg");
		} else {
			mfView.setAdditionalInfo(si.getAdditionalInfo());
			mfView.setLoginUrl(si.getLoginUrl());
			final String passphrase = mfView.getPassphrase();
			final String loginInfoEncrypted = si.getLoginInfo();
			final String loginInfoDecrypted = EncryptionHelper.decrypt(passphrase, loginInfoEncrypted);
			mfView.setLoginInfo(loginInfoDecrypted);
			final String loginAdditionalInfoEncrypted = si.getAdditionalLoginInfo();
			final String loginAdditionalInfoDecrypted = EncryptionHelper.decrypt(passphrase,
					loginAdditionalInfoEncrypted);
			mfView.setAdditionalLoginInfo(loginAdditionalInfoDecrypted);
			mfView.setUseSmallLetters(si.isUseSmallLetters());
			mfView.setUseCapitalLetters(si.isUseCapitalLetters());
			mfView.setUseDigits(si.isUseDigits());
			mfView.setUseSpecialCharacters(si.isUseSpecialCharacters());
			mfView.setSmallLettersCount(si.getSmallLettersCount());
			mfView.setSmallLettersStartIndex(si.getSmallLettersStartIndex());
			mfView.setSmallLettersEndIndex(si.getSmallLettersEndIndex());
			mfView.setCapitalLettersCount(si.getCapitalLettersCount());
			mfView.setCapitalLettersStartIndex(si.getCapitalLettersStartIndex());
			mfView.setCapitalLettersEndIndex(si.getCapitalLettersEndIndex());
			mfView.setDigitsCount(si.getDigitsCount());
			mfView.setSpecialCharactersCount(si.getSpecialCharactersCount());
			mfView.setDigitsStartIndex(si.getDigitsStartIndex());
			mfView.setDigitsEndIndex(si.getDigitsEndIndex());
			mfView.setSpecialCharactersStartIndex(si.getSpecialCharactersStartIndex());
			mfView.setSpecialCharactersEndIndex(si.getSpecialCharactersEndIndex());
			mfView.setTotalCharacterCount(si.getTotalCharacterCount());
			final String passwordEncrypted = si.getPassword();
			final String passwordDecrypted = EncryptionHelper.decrypt(passphrase, passwordEncrypted);
			mfView.setPassword(passwordDecrypted);
			final String passwordRepeatedEncrypted = si.getPasswordRepeated();
			final String passwordRepeatedDecrypted = EncryptionHelper.decrypt(passphrase,
					passwordRepeatedEncrypted);
			mfView.setPasswordRepeated(passwordRepeatedDecrypted);
		}
	}

	/**
	 * Aus der Liste neu ausgewählten Dienst laden.
	 */
	public void valueChangedLoadServiceFromList(final MFView mfView, final String serviceAbbreviation) {
		try {
			mfView.setWaitCursor();
			mfView.setServiceAbbreviation(serviceAbbreviation);
			loadService(mfView);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mfView.setDefaultCursor();
		}
	}

	/**
	 * Löscht die Einstellungen für ein Dienstekürzel und speichert die Einstellungen für alle Dienstekürzel
	 * auf der Platte.
	 */
	public void actionPerformedRemoveService(final MFView mfView) {
		try {
			mfView.setWaitCursor();
			String serviceAbbreviation = mfView.getServiceAbbreviation();
			validateServiceAbbreviation(serviceAbbreviation);
			int chosenOption = JOptionPane.showConfirmDialog(mfView, "Dienst '" + serviceAbbreviation
					+ "' entfernen?", Services.getInstance().getConstants().getApplicationName(),
					JOptionPane.YES_NO_OPTION);
			if (chosenOption != JOptionPane.NO_OPTION) { // Dienst nicht
				ServiceInfo si = services.removeServiceInfo(serviceAbbreviation);
				if (si == null) { // Dienst gar nicht vorhanden?
					throw new DomainException("ServiceAbbreviationMissingMsg");
				} else {
					saveServiceInfoList();
					mfView.updateStoredService();
				}
			}
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mfView.setDefaultCursor();
		}
	}

	/**
	 * Öffnet die Login-URL im Browser und kopiert die Login-Informationen in die Zwischenablage.
	 */
	public void actionPerformedOpenUrlInBrowser(final MFView mfView) {
		try {
			mfView.setWaitCursor();
			final String loginUrl = mfView.getLoginUrl();
			Desktop.getDesktop().browse(new URI(loginUrl));
			copyLoginInfo(mfView);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mfView.setDefaultCursor();
		}
	}

	/**
	 * Kopiert die Login-Informationen in die Zwischenablage.
	 */
	public void actionPerformedCopyLoginInfo(final MFView mfView) {
		try {
			mfView.setWaitCursor();
			copyLoginInfo(mfView);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mfView.setDefaultCursor();
		}
	}

	/**
	 * Kopiert die Login-Informationen in die Zwischenablage.
	 */
	private void copyLoginInfo(final MFView mfView) {
		final String loginInfo = mfView.getLoginInfo();
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(loginInfo), null);
	}

	/**
	 * Vermerkt die Einstellungen für ein Dienstekürzel und speichert die Einstellungen für alle Dienstekürzel
	 * auf der Platte.
	 */
	public void actionPerformedStoreService(final MFView mfView) {
		try {
			mfView.setWaitCursor();
			String serviceAbbreviation = mfView.getServiceAbbreviation();
			validateServiceAbbreviation(serviceAbbreviation);
			ServiceInfo si = services.getServiceInfo(serviceAbbreviation);
			if (si != null) { // Ist der Dienst bereits vermerkt?
				int chosenOption = JOptionPane.showConfirmDialog(mfView,
						"Der Dienst wurde bereits vermerkt. Überschreiben?", Services.getInstance()
								.getConstants().getApplicationName(), JOptionPane.YES_NO_OPTION);
				if (chosenOption == JOptionPane.NO_OPTION) { // Dienst nicht
					// überschreiben?
					return; // nein ... dann ist nichts mehr zu tun
				}
			}
			si = new ServiceInfo(serviceAbbreviation);
			si.setAdditionalInfo(mfView.getAdditionalInfo());
			si.setLoginUrl(mfView.getLoginUrl());
			final String passphrase = mfView.getPassphrase();
			final String loginInfoDecrypted = mfView.getLoginInfo();
			final String loginInfoEncrypted = EncryptionHelper.encrypt(passphrase, loginInfoDecrypted);
			si.setLoginInfo(loginInfoEncrypted);
			final String loginAdditionalInfoDecrypted = mfView.getAdditionalLoginInfo();
			final String loginAdditionalInfoEncrypted = EncryptionHelper.encrypt(passphrase,
					loginAdditionalInfoDecrypted);
			si.setAdditionalLoginInfo(loginAdditionalInfoEncrypted);
			si.setUseSmallLetters(mfView.getUseSmallLetters());
			si.setUseCapitalLetters(mfView.getUseCapitalLetters());
			si.setUseDigits(mfView.getUseDigits());
			si.setUseSpecialCharacters(mfView.getUseSpecialCharacters());
			si.setSmallLettersCount(mfView.getSmallLettersCount());
			si.setSmallLettersStartIndex(mfView.getSmallLettersStartIndex());
			si.setSmallLettersEndIndex(mfView.getSmallLettersEndIndex());
			si.setCapitalLettersCount(mfView.getCapitalLettersCount());
			si.setCapitalLettersStartIndex(mfView.getCapitalLettersStartIndex());
			si.setCapitalLettersEndIndex(mfView.getCapitalLettersEndIndex());
			si.setDigitsCount(mfView.getDigitsCount());
			si.setSpecialCharactersCount(mfView.getSpecialCharactersCount());
			si.setDigitsStartIndex(mfView.getDigitsStartIndex());
			si.setDigitsEndIndex(mfView.getDigitsEndIndex());
			si.setSpecialCharactersStartIndex(mfView.getSpecialCharactersStartIndex());
			si.setSpecialCharactersEndIndex(mfView.getSpecialCharactersEndIndex());
			si.setTotalCharacterCount(mfView.getTotalCharacterCount());
			final String passwordDecrypted = mfView.getPassword();
			final String passwordEncrypted = EncryptionHelper.encrypt(passphrase, passwordDecrypted);
			si.setPassword(passwordEncrypted);
			final String passwordRepeatedDecrypted = mfView.getPasswordRepeated();
			final String passwordRepeatedEncrypted = EncryptionHelper.encrypt(passphrase,
					passwordRepeatedDecrypted);
			si.setPasswordRepeated(passwordRepeatedEncrypted);
			services.putServiceInfo(si);
			saveServiceInfoList();
			mfView.updateStoredService();
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mfView.setDefaultCursor();
		}
	}

	/**
	 * Generiert ein Passwort und gibt es zurück.
	 */
	private String generatePassword(final MFView mfView) {
		mfView.setWaitCursor();
		String characters = ""; // Zeichen für den Rest des Passworts
		validatePassphrase(mfView);
		final String passphrase = mfView.getPassphrase();
		final String serviceAbbreviation = mfView.getServiceAbbreviation();
		validateServiceAbbreviation(serviceAbbreviation);
		long seed = passphrase.hashCode() + serviceAbbreviation.hashCode();
		final String additionalInfo = mfView.getAdditionalInfo();
		if (additionalInfo.length() != 0) { // Zusatzinfos vorhanden?
			seed += additionalInfo.hashCode(); // => Zur Saat dazunehmen
		}
		final int pswLength = EmptyHelper.getValue(mfView.getTotalCharacterCount(), 0);
		PasswordFactory pg = new PasswordFactory(pswLength);
		pg.setSeedForRandomToEnforceReproducableResults(seed);
		if (mfView.getUseSmallLetters()) {
			int count = EmptyHelper.getValue(mfView.getSmallLettersCount(), 0);
			int start = EmptyHelper.getValue(mfView.getSmallLettersStartIndex(), 0);
			int end = EmptyHelper.getValue(mfView.getSmallLettersEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, Constants.LOWERCASE_LETTERS, start, end);
			} else {
				characters += Constants.LOWERCASE_LETTERS;
			}
		}
		if (mfView.getUseCapitalLetters()) {
			int count = EmptyHelper.getValue(mfView.getCapitalLettersCount(), 0);
			int start = EmptyHelper.getValue(mfView.getCapitalLettersStartIndex(), 0);
			int end = EmptyHelper.getValue(mfView.getCapitalLettersEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, Constants.UPPERCASE_LETTERS, start, end);
			} else {
				characters += Constants.UPPERCASE_LETTERS;
			}
		}
		if (mfView.getUseDigits()) {
			int count = EmptyHelper.getValue(mfView.getDigitsCount(), 0);
			int start = EmptyHelper.getValue(mfView.getDigitsStartIndex(), 0);
			int end = EmptyHelper.getValue(mfView.getDigitsEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, Constants.DIGITS, start, end);
			} else {
				characters += Constants.DIGITS;
			}
		}
		if (mfView.getUseSpecialCharacters()) {
			int count = EmptyHelper.getValue(mfView.getSpecialCharactersCount(), 1);
			int start = EmptyHelper.getValue(mfView.getSpecialCharactersStartIndex(), 0);
			int end = EmptyHelper.getValue(mfView.getSpecialCharactersEndIndex(), pswLength - 1);
			if (count != 0) {
				pg.distributeCharacters(count, Constants.SPECIAL_CHARS, start, end);
			} else {
				characters += Constants.SPECIAL_CHARS;
			}
		}
		return pg.getPassword(characters); // Rest auffüllen
	}

	/**
	 * Generieren das Passwort und zeigt es an.
	 */
	public void actionPerformedDisplayPassword(final MFView mfView) {
		try {
			mfView.setWaitCursor();
			final String psw = validatedOrGeneratePassword(mfView);
			JOptionPane.showMessageDialog(mfView, "Das generierte Passwort ist \"" + psw + "\"", Services
					.getInstance().getConstants().getApplicationName(), JOptionPane.PLAIN_MESSAGE);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mfView.setDefaultCursor();
		}
	}

	/**
	 * Generiert das Passwort und kopiert es in die Zwischenablage.
	 */
	public void actionPerformedCopyPassword(final MFView mfView) {
		try {
			mfView.setWaitCursor();
			final String psw = validatedOrGeneratePassword(mfView);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(psw), null);
		} catch (Throwable t) {
			handleThrowable(t);
		} finally {
			mfView.setDefaultCursor();
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
		MFView mfView = new MFView(this);
		mfView.setTitle(servicesFile.getAbsolutePath() + " - " + Constants.APPLICATION_NAME + " "
				+ Constants.APPLICATION_VERSION);
		addView(mfView); // dem Controller mitteilen
		mfView.pack();
		mfView.setVisible(true);
	}

}
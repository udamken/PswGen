package net.sf.pswgen.main;

import java.util.logging.Level;

import javax.swing.UIManager;

import net.sf.pswgen.base.util.Services;
import net.sf.pswgen.gui.PswGenCtl;
import net.sf.pswgen.util.Constants;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;


/**
 * <p>
 * Die Main-Klasse dieser Anwendung.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 * <p>
 * TODO Angedachte Änderungen ...
 * </p>
 * <p>
 * Verschlüsselung der Datei mit der Passphrase. So kann man bei Öffnen der XML-Datei schon sehen, ob die
 * Passphrase korrekt ist oder Teststring zum Überprüfen, ob die Datei richtig entschlüsselt wurde. Ablage des
 * verschlüsselten Teststrings und nach dem Entschlüsseln prüfen, ob es der erwartete String ist.
 * </p>
 * <p>
 * Im PswGenCtl gibt es Event-Handling-Methoden, die zum Teil direkt die Fachlogik beinhalten, andere rufen
 * separate Methoden auf. Generell wäre es sinnvoll, die Fachlogik zu separieren, leider ist sie aber sehr
 * GUI-nah.
 * </p>
 * <p>
 * CheckBox oder RadioButtons, welches Feld beim Öffnen in die Zwischenablage kopiert werden soll, die
 * Anmeldeinformationen oder gleich das Passwort, wie zum Beispiel bei der FRITZ!Box.
 * </p>
 * <p>
 * Für Integer die Feldlänge als WidgetInfo aufnehmen.
 * </p>
 * <p>
 * Speichernabfrage beim Wechsel zu einem anderen Dienst einbauen.
 * </p>
 * <p>
 * Mobile Version bereitsstellen, eventuell ohne Pflege mit nur einmaliger Passphrase-Eingabe.
 * </p>
 */
public class PswGen {

	/** Die eine und einzige Instanz dieser Anwendung */
	private static PswGen instance = null;

	/**
	 * Liefert die eine und einzige Instanz der Anwendung zurück.
	 */
	private static synchronized PswGen getInstance(final String servicesFilename) {
		if (instance == null) {
			instance = new PswGen();
			instance.initialize(servicesFilename);
		}
		return instance;
	}

	/**
	 * Initialisiert die eine und einzige Instanz.
	 */
	private void initialize(final String servicesFilename) {
		Services.getInstance(Constants.getInstance());
		setupLookAndFeel();
		new PswGenCtl(servicesFilename);
	}

	/**
	 * Setzt das Look&Feel auf den System-Standard.
	 */
	private void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Services.getInstance().getLogger().log(Level.WARNING, Constants.MSG_EXCP_LOOK_AND_FEEL, e);
		}
	}

	/**
	 * Hier werden die Kommandozeilenparameter analysiert und die Anwendung gestartet.
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		try {
			Options options = new Options();
			Option help = new Option("help", "print this message");
			Option services = OptionBuilder.withArgName("file").hasArg()
					.withDescription("use given file to store services").create("services");
			options.addOption(help);
			options.addOption(services);
			CommandLineParser parser = new GnuParser(); // GnuParser => mehrbuchstabige Optionen
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("help")) { // Hilfe ausgeben => nur das tun
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("pswgen", options);
			} else {
				String servicesFilename = line.getOptionValue("services", "services.xml");
				getInstance(servicesFilename); // Anwendung starten, PswGenCtl terminiert die VM
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception caught", e);
		}
	}
}
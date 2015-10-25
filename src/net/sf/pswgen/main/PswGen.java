package net.sf.pswgen.main;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.sf.pswgen.gui.PswGenCtl;
import net.sf.pswgen.util.Constants;

/**
 * <p>
 * Die Main-Klasse dieser Anwendung.
 * </p>
 * <p>
 * Copyright (C) 2005-2015 Uwe Damken
 * </p>
 */
public class PswGen {

	/**
	 * Hier werden die Kommandozeilenparameter analysiert und die Anwendung gestartet.
	 */
	public static void main(String[] args) throws ParseException, IOException {
		Options options = new Options();
		Option help = new Option("help", "print this message");
		@SuppressWarnings("static-access")
		Option services = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given file to store services").create("services");
		@SuppressWarnings("static-access")
		Option upgrade = OptionBuilder.withArgName("passphrase").hasArg()
				.withDescription("converts and re-encrypts services to new format if not too old")
				.create("upgrade");
		options.addOption(help);
		options.addOption(services);
		options.addOption(upgrade);
		CommandLineParser parser = new GnuParser(); // GnuParser => mehrbuchstabige Optionen
		CommandLine line = parser.parse(options, args);
		if (line.hasOption("help")) { // Hilfe ausgeben => nur das tun
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("pswgen", options);
		} else if (line.hasOption("upgrade")) { // Datei umformatieren => nur das tun
			String servicesFilename = line.getOptionValue("services", Constants.SERVICES_FILENAME);
			String passphrase = line.getOptionValue("upgrade");
			PswGenCtl ctl = new PswGenCtl(servicesFilename);
			ctl.upgradeServiceInfoList(passphrase);
		} else {
			String servicesFilename = line.getOptionValue("services", Constants.SERVICES_FILENAME);
			PswGenCtl ctl = new PswGenCtl(servicesFilename);
			ctl.start(); // Anwendung starten, PswGenCtl terminiert die VM
		}
	}

}
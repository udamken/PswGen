package net.sf.pswgen.main;

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

import java.io.File;

import net.sf.pswgen.gui.PswGenCtl;
import net.sf.pswgen.util.Constants;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * <p>
 * Die Main-Klasse dieser Anwendung.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class PswGen {

	/**
	 * Hier werden die Kommandozeilenparameter analysiert und die Anwendung gestartet.
	 */
	public static void main(String[] args) throws ParseException {
		Options options = new Options();
		Option help = new Option("help", "print this message");
		@SuppressWarnings("static-access")
		Option services = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given file to store services").create("services");
		Option upgrade = new Option("upgrade", "converts services to new format if not older than 1.6");
		options.addOption(help);
		options.addOption(services);
		options.addOption(upgrade);
		CommandLineParser parser = new GnuParser(); // GnuParser => mehrbuchstabige Optionen
		CommandLine line = parser.parse(options, args);
		if (line.hasOption("help")) { // Hilfe ausgeben => nur das tun
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("pswgen", options);
		} else if (line.hasOption("upgrade")) { // Hilfe ausgeben => nur das tun
			String sourceFilename = line.getOptionValue("services", "services.xml");
			String targetFilename = line.getOptionValue("services", Constants.SERVICES_FILENAME);
			if (sourceFilename.endsWith(".xml") && !targetFilename.endsWith(".json")) {
				targetFilename = sourceFilename.replaceFirst("\\.xml$", ".json");
			}
			PswGenCtl ctl = new PswGenCtl(sourceFilename);
			ctl.upgradeServiceInfoList(targetFilename);
		} else {
			String servicesFilename = line.getOptionValue("services", Constants.SERVICES_FILENAME);
			if (!line.hasOption("services")) {
				if (!(new File(servicesFilename)).exists()) {
					// Möglicherweise ist die Command Line noch von PswGen < 1.7.0
					servicesFilename = "services.xml";
				}
			}
			PswGenCtl ctl = new PswGenCtl(servicesFilename);
			ctl.start(); // Anwendung starten, PswGenCtl terminiert die VM
		}
	}

}
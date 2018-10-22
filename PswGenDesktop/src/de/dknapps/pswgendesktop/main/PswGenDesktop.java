/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2016 Uwe Damken
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
package de.dknapps.pswgendesktop.main;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.dknapps.pswgencore.CoreConstants;
import de.dknapps.pswgendesktop.gui.PswGenCtl;

public class PswGenDesktop {

	/**
	 * Hier werden die Kommandozeilenparameter analysiert und die Anwendung gestartet.
	 */
	public static void main(String[] args) throws IOException {
		Options options = new Options();
		Option help = new Option("help", "print this message");
		@SuppressWarnings("static-access")
		Option services = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given file to store services").create("services");
		@SuppressWarnings("static-access")
		Option other = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given file to merge with services file").create("other");
		options.addOption(help);
		options.addOption(services);
		options.addOption(other);
		CommandLineParser parser = new GnuParser(); // GnuParser => mehrbuchstabige Optionen
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage()); // line bleibt null, dann kommt die Hilfe
		}
		if (line == null || line.hasOption("help")) { // Hilfe ausgeben => nur das tun
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("pswgen", options);
		} else {
			String servicesFilename = line.getOptionValue("services", CoreConstants.SERVICES_FILENAME);
			String otherServicesFilename = line.getOptionValue("other");
			PswGenCtl ctl = new PswGenCtl(servicesFilename, otherServicesFilename);
			ctl.start(); // Anwendung starten, PswGenCtl terminiert die VM
		}
	}

}
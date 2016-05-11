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
package de.dknapps.pswgenserver;

import java.io.Console;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.prefs.Preferences;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.dknapps.pswgencore.CoreConstants;

/**
 * The main class for the PswGenServer.
 * 
 */
public class PswGenServerMain {
	/**
	 * The main method that parses the command-line arguments.
	 * 
	 * @param args
	 *            The command-line arguments.
	 * @throws IOException
	 *             If any I/O error occurs.
	 */
	public static void main(String[] args) throws IOException {
		OptionBuilder.withArgName("file");
		OptionBuilder.withLongOpt("file");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("use given file to store services");
		final Option servicesOption = OptionBuilder.create("services");

		OptionBuilder.withArgName("host");
		OptionBuilder.withLongOpt("host");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("sets the hostname to bind the server to");
		final Option hostOption = OptionBuilder.create('h');

		OptionBuilder.withArgName("port");
		OptionBuilder.withLongOpt("port");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("sets the port to run the server on");
		final Option portOption = OptionBuilder.create('p');

		OptionBuilder.withArgName("passphrase");
		OptionBuilder.withLongOpt("passphrase");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("sets the passphrase to use");
		final Option passphraseOption = OptionBuilder.create('P');

		OptionBuilder.withArgName("regenerate");
		OptionBuilder.withLongOpt("regenerate");
		OptionBuilder.withDescription("regenerates the security token");
		final Option regenerateOption = OptionBuilder.create('r');

		final Options options = new Options();
		options.addOption(servicesOption);
		options.addOption(hostOption);
		options.addOption(portOption);
		options.addOption(passphraseOption);
		options.addOption(regenerateOption);
		options.addOption(new Option("help", "print this message"));

		final CommandLineParser parser = new GnuParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		char[] passphrase;
		if (line != null && line.hasOption("passphrase")) {
			passphrase = line.getOptionValue("passphrase").toCharArray();
		} else {
			Console console = System.console();
			if (console == null) {
				throw new IllegalStateException(
						"Not running on an interactive Shell and option '--passphrase' is not specified!");
			}
			passphrase = console.readPassword("Passphrase: ");
		}

		if (line == null || line.hasOption("help")) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("pswgen", options);
		} else {
			final String services = line.getOptionValue("services", CoreConstants.SERVICES_FILENAME);
			final String host = line.getOptionValue("host", "localhost");
			final int port = Integer.parseInt(line.getOptionValue("port", "9000"));

			final PswGenServer pswGenServer = new PswGenServer(host, port,
					PswGenServerMain.getSecurityToken(line.hasOption("regenerate")));
			pswGenServer.loadServices(services, passphrase);
			pswGenServer.start();
		}
	}

	/**
	 * Gets the security token to use. If <code>regenerate</code> is set to <code>true</code>, a new token is
	 * generated every time.
	 * 
	 * @param regenerate
	 *            Whether to generate a new token every time or to use the stored token if possible.
	 * @return The token to use.
	 */
	private static String getSecurityToken(final boolean regenerate) {
		final Preferences preferences = Preferences.userRoot().node(ServerConstants.SERVER_PACKAGE_NAME);
		String token = preferences.get("token", null);
		if (token == null || regenerate) {
			token = "+t+" + (new BigInteger(256, new SecureRandom()).toString(32)) + "+";

			preferences.put("token", token);
		}
		return token;
	}
}

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.dknapps.pswgencore.model.ServiceInfo;
import de.dknapps.pswgencore.model.ServiceInfoList;
import de.dknapps.pswgencore.util.CommonJsonWriter;
import de.dknapps.pswgencore.util.EncryptionHelper;
import de.dknapps.pswgencore.util.FileHelper;
import de.dknapps.pswgencore.util.PasswordFactory;
import de.dknapps.pswgenserver.util.CommonJsonReaderWriterFactoryGsonImpl;

/**
 * The PswGenServer.
 * 
 */
public class PswGenServer {
	/**
	 * The logger.
	 * 
	 */
	private static final Logger LOGGER = Logger.getLogger(PswGenServer.class.getCanonicalName());

	/**
	 * {@value #JSON_UTF8}
	 * 
	 */
	private static final String JSON_UTF8 = "application/json; charset=UTF-8";
	/**
	 * The {@link #HEADER_CONTENT_TYPE} header.
	 * 
	 */
	private static final String HEADER_CONTENT_TYPE = "Content-Type";

	/**
	 * The {@value #SECURITY_TOKEN_HEADER} header that must contain the {@link #securityToken}.
	 * 
	 */
	private static final String SECURITY_TOKEN_HEADER = "X-TOKEN";

	/**
	 * The security token that must be present on any request.
	 * 
	 */
	private final String securityToken;
	/**
	 * The actual HTTP server.
	 * 
	 */
	private final HttpServer httpServer;

	/**
	 * The passphrase that is used for decryption.
	 * 
	 */
	private String passphrase;
	/**
	 * The {@link ServiceInfoList} containing all the services.
	 * 
	 */
	private ServiceInfoList services;

	/**
	 * Initializes the PswGenServer.
	 * 
	 * @param host
	 *            The host to bind the server to.
	 * @param port
	 *            The port to bind the server to.
	 * @param securityToken
	 *            The security token that must be set in the request headers in order to retrieve the data.
	 * @throws IOException
	 *             If any I/O error occurs.
	 */
	public PswGenServer(final String host, final int port, final String securityToken) throws IOException {
		this.httpServer = HttpServer.create(new InetSocketAddress(host, port), 0);

		LOGGER.info("Using security token: " + securityToken);

		this.securityToken = securityToken;

		this.httpServer.createContext("/service-list", new ServiceListHandler());
		this.httpServer.createContext("/service", new SpecificServiceHandler());
		this.httpServer.setExecutor(null);
	}

	/**
	 * Loads the given services file and decrypts it with the given passphrase.
	 * 
	 * @param servicesFile
	 *            The file to load.
	 * @param passphrase
	 *            The passphrase that is used for decrypting the service file.
	 */
	public void loadServices(final String servicesFile, final char[] passphrase) {
		if (servicesFile == null || (!new File(servicesFile).isFile() && new File(servicesFile).exists())) {
			throw new IllegalArgumentException("Services must not be null or anything else than a file!");
		}

		LOGGER.info("Loading service file from <" + servicesFile + ">.");

		this.services = FileHelper.getInstance(new CommonJsonReaderWriterFactoryGsonImpl())
				.loadServiceInfoList(new File(servicesFile));
		final EncryptionHelper encryptionHelper = new EncryptionHelper(passphrase,
				services.getSaltAsHexString(), services.getInitializerAsHexString());
		this.services.decrypt(encryptionHelper);

		this.passphrase = new String(passphrase);

		LOGGER.info("Service file loaded!");
	}

	/**
	 * Starts the server.
	 * 
	 */
	public void start() {
		this.httpServer.start();
	}

	/**
	 * Handles all requests on <code>/service-list</code>.
	 * 
	 */
	private class ServiceListHandler implements HttpHandler {
		/**
		 * {@inheritDoc}
		 * 
		 * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
		 */
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			LOGGER.info("Handling service list request.");

			if (!exchange.getRequestHeaders().containsKey(SECURITY_TOKEN_HEADER)
					|| !exchange.getRequestHeaders().getFirst(SECURITY_TOKEN_HEADER)
							.equals(PswGenServer.this.securityToken)) {
				LOGGER.info("Service list request handled! Missing header " + SECURITY_TOKEN_HEADER
						+ " or header contains invalid token!");

				exchange.sendResponseHeaders(403, 0);
				exchange.getResponseBody().close();

				return;
			}

			final Collection<ServiceInfo> serviceInfos = PswGenServer.this.services.getServices();

			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
			final CommonJsonWriter jsonWriter = new CommonJsonReaderWriterFactoryGsonImpl()
					.getJsonWriter(writer);

			jsonWriter.beginArray();
			for (final ServiceInfo serviceInfo : serviceInfos) {
				jsonWriter.value(serviceInfo.getServiceAbbreviation());
			}
			jsonWriter.endArray();

			jsonWriter.close();

			final byte[] result = out.toByteArray();

			LOGGER.info("Service list request handled! Sending data.");

			exchange.getResponseHeaders().set(PswGenServer.HEADER_CONTENT_TYPE, PswGenServer.JSON_UTF8);
			exchange.sendResponseHeaders(200, result.length);
			final OutputStream responseBody = exchange.getResponseBody();
			responseBody.write(result);
			responseBody.close();
		}
	}

	/**
	 * Handles all requests on <code>/service?&lt;service-name&gt;</code>
	 * 
	 */
	private class SpecificServiceHandler implements HttpHandler {
		/**
		 * {@inheritDoc}
		 * 
		 * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
		 */
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			LOGGER.info("Handling specific service request.");

			if (!exchange.getRequestHeaders().containsKey(SECURITY_TOKEN_HEADER)
					|| !exchange.getRequestHeaders().getFirst(SECURITY_TOKEN_HEADER)
							.equals(PswGenServer.this.securityToken)) {
				LOGGER.info("Specific service request handled! Missing header " + SECURITY_TOKEN_HEADER
						+ " or header contains invalid token!");

				exchange.sendResponseHeaders(403, 0);
				exchange.getResponseBody().close();

				return;
			}

			final ServiceInfo serviceInfo = PswGenServer.this.services
					.getServiceInfo(exchange.getRequestURI().getQuery());

			LOGGER.info("Requested service " + serviceInfo.getServiceAbbreviation() + ".");

			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
			final CommonJsonWriter jsonWriter = new CommonJsonReaderWriterFactoryGsonImpl()
					.getJsonWriter(writer);

			jsonWriter.beginObject();
			jsonWriter.name("additionalInfo").value(serviceInfo.getAdditionalInfo());
			jsonWriter.name("additionalLoginInfo").value(serviceInfo.getAdditionalLoginInfo());
			jsonWriter.name("loginInfo").value(serviceInfo.getLoginInfo());
			jsonWriter.name("loginUrl").value(serviceInfo.getLoginUrl());
			jsonWriter.name("password")
					.value(PasswordFactory.getPassword(serviceInfo, PswGenServer.this.passphrase));
			jsonWriter.name("serviceAbbreviation").value(serviceInfo.getServiceAbbreviation());
			jsonWriter.endObject();

			jsonWriter.close();

			final byte[] result = out.toByteArray();

			LOGGER.info("Specific service request handled! Sending data.");

			exchange.getResponseHeaders().set(PswGenServer.HEADER_CONTENT_TYPE, PswGenServer.JSON_UTF8);
			exchange.sendResponseHeaders(200, result.length);
			final OutputStream responseBody = exchange.getResponseBody();
			responseBody.write(result);
			responseBody.close();
		}
	}
}

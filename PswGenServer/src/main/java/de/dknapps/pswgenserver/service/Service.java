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
package de.dknapps.pswgenserver.service;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A simple POJO that holds all relevant information about any service.
 * 
 */
@Data
@EqualsAndHashCode(exclude = "password")
@ToString(exclude = "password")
public class Service {
	/**
	 * The name of the service.
	 * 
	 */
	private final String name;
	/**
	 * The string that was set for the URL of the service.
	 * 
	 */
	private final String urlString;
	/**
	 * The login information (e.g. the username).
	 * 
	 */
	private final String loginInfo;
	/**
	 * The password of the service.
	 * 
	 */
	private final String password;
	/**
	 * The {@link URL} representation of {@link #urlString}, if it is a correct URL. Otherwise
	 * <code>null</code>.
	 * 
	 */
	private URL url;

	/**
	 * Constructor of Service.
	 * 
	 * @param name
	 *            The {@link #name} to set.
	 * @param urlString
	 *            {@link #urlString} to set. This is used to construct {@link #url} if it is a correct URL.
	 * @param loginInfo
	 *            The {@link #loginInfo} to set.
	 * @param password
	 *            The {@link #password} to set.
	 */
	public Service(final String name, final String urlString, final String loginInfo, final String password) {
		this.name = name;
		try {
			this.url = new URL(urlString);
		} catch (final MalformedURLException ex) {
			this.url = null;
		}
		this.urlString = urlString;
		this.loginInfo = loginInfo;
		this.password = password;
	}

	/**
	 * Constructor of Service.
	 * 
	 * <p>
	 * This invokes {@link #Service(String, String, String, String)} with the provided parameters.
	 * </p>
	 * 
	 * @param name
	 *            Is passed to {@link #Service(String, String, String, String)}.
	 * @param loginInfo
	 *            Is passed to {@link #Service(String, String, String, String)}.
	 * @param urlString
	 *            Is passed to {@link #Service(String, String, String, String)}.
	 * @see de.dknapps.pswgenserver.service.Service#Service(java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public Service(final String name, final String loginInfo, final String urlString) {
		this(name, urlString, loginInfo, null);
	}
}

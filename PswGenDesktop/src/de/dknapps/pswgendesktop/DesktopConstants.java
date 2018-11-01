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
package de.dknapps.pswgendesktop;

/**
 * <p>
 * Hält die Konstanten und markiert das Top-Level-Package von PswGenDesktop.
 * </p>
 */
public class DesktopConstants {

	/** Der angezeigte Anwendungsname für PswGenDesktop */
	public static final String APPLICATION_NAME = "PswGenDesktop";

	/** Das Top-Level-Package von PswGenDesktop */
	public static final String APPLICATION_PACKAGE_NAME = DesktopConstants.class.getPackage().getName();

	/** Resource Name des Icons von PswGenDesktop */
	public static final String APPLICATION_IMAGE_RESOURCE_NAME = APPLICATION_PACKAGE_NAME.replace('.', '/')
			+ "/PswGen-128x128.png";

	/** URL zur HTML-Datei für den About-Dialog von PswGenDesktop */
	public static final String ABOUT_DIALOG_HTML_FILENAME = "about.html";

}

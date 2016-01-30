/*******************************************************************************
 * PswGen - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGen on your mobile  
 *
 *     Copyright (C) 2005, 2016 Uwe Damken
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
package de.dknapps.pswgen.util;

import java.awt.Image;
import java.net.URL;
import java.util.MissingResourceException;

import javax.swing.ImageIcon;

/**
 * <p>
 * ImageHelper ist ein Singleton und hilft dabei, Bilder zu laden. Durch die zentrale Klasse ist es möglich,
 * hier später mal ein Caching einzubauen.
 * </p>
 */
public class ImageHelper {

	/** Die eine und einzige Instanz dieser Klasse */
	private static ImageHelper instance = null;

	/**
	 * Singleton => privater Konstruktor
	 */
	private ImageHelper() {
		// Nichts zu tun
	}

	/**
	 * Liefert die eine und einzige Instanz.
	 */
	public static synchronized ImageHelper getInstance() {
		if (instance == null) {
			instance = new ImageHelper();
		}
		return instance;
	}

	/**
	 * Liefert ein Icon, welches ggf. zuvor geladen wird.
	 */
	public ImageIcon getImageIcon(String imageResourceName) {
		URL imageUrl = getClass().getClassLoader().getResource(imageResourceName);
		if (imageUrl == null) {
			throw new MissingResourceException("Image '" + imageResourceName + "' not found",
					ImageIcon.class.getName(), imageResourceName);
		}
		return new ImageIcon(imageUrl);
	}

	/**
	 * Liefert ein Icon als Image, welches ggf. zuvor geladen wird.
	 */
	public Image getImage(String imageResourceName) {
		return getImageIcon(imageResourceName).getImage();
	}

}

package net.sf.pswgen.util;

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

import java.awt.Image;
import java.net.URL;
import java.util.MissingResourceException;

import javax.swing.ImageIcon;

/**
 * <p>
 * ImageHelper ist ein Singleton und hilft dabei, Bilder zu laden. Durch die zentrale Klasse ist es möglich,
 * hier später mal ein Caching einzubauen.
 * </p>
 * <p>
 * Copyright (C) 2005-2013 Uwe Damken
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

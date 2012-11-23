package net.sf.pswgen.util;

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
 * (c) 2005-2012, by Uwe Damken
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
	 * Liefert ein Icon als Image, welches ggf. zuvor geladen wird.
	 */
	public Image getIconImage(String iconImageResourceName) {
		URL imageUrl = getClass().getClassLoader().getResource(iconImageResourceName);
		if (imageUrl == null) {
			throw new MissingResourceException("IconImage '" + iconImageResourceName + "' not found",
					ImageIcon.class.getName(), iconImageResourceName);
		}
		return (new ImageIcon(imageUrl)).getImage();
	}

}

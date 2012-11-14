package net.sf.pswgen.base.util;

import java.util.logging.Logger;

/**
 * <p>
 * Gestattet den anwendungsglobalen Zugriff auf alle querschnittlichen Dienste.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
public class Services {

	/** Die eine und einzige Instanz dieser Klasse */
	private static Services instance = null;

	/**
	 * Konstanten der jeweiligen Anwendung in denen <code>Services</code> genutzt wird
	 */
	private ConstantsProvider constants = null;

	/** Der Logger dieser Anwendung */
	private Logger logger = null;

	/**
	 * Konstruktor ist nicht öffentlich zugreifbar => getInstance() nutzen
	 */
	private Services(final ConstantsProvider constants) {
		super();
		this.constants = constants;
	}

	/**
	 * Liefert die eine und einzige Instanz, wenn sie zuvor instantiiert und initialisiert wurde.
	 */
	public static synchronized Services getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Services.getInstance(constants) has to be called first");
		}
		return instance;
	}

	/**
	 * Instantiiert, initialisiert und liefert die eine und einzige Instanz.
	 */
	public static synchronized Services getInstance(final ConstantsProvider constants) {
		if (instance != null) {
			throw new IllegalStateException("Services.getInstance(constants) may be called only once");
		}
		instance = new Services(constants);
		instance.initialize();
		return instance;
	}

	/**
	 * Initialisiert die eine und einzige Instanz.
	 */
	private void initialize() {
		String pkg = constants.getApplicationPackageName();
		logger = Logger.getLogger(pkg + ".Logger", pkg + ".Messages");
	}

	/**
	 * Liefert den für diese Anwendung verwendeten Logger.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @return Returns the constants.
	 */
	public ConstantsProvider getConstants() {
		return constants;
	}
}

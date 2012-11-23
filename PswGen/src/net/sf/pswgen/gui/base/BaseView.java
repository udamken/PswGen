package net.sf.pswgen.gui.base;

import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sf.pswgen.util.Constants;
import net.sf.pswgen.util.ImageHelper;

/**
 * <p>
 * Generische Basisklasse für die Views meiner Anwendungen.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
public abstract class BaseView extends JFrame {

	private static final long serialVersionUID = -2867571588508896513L;

	private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

	private static final Cursor WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);

	/** Der Controller für diese View */
	protected BaseCtl ctl = null;

	/** Hey, it's me ... für die Listener */
	private BaseView me = this;

	/**
	 * Konstruktor
	 */
	protected BaseView(BaseCtl ctl) {
		super();
		this.ctl = ctl;
		initialize();
	}

	/**
	 * Initialisiert die View und ruft createContentPane() der abgeleiteten Klasse auf.
	 */
	public void initialize() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ctl.windowClosing(me);
			}
		});
		setResizable(true);
		setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		setIconImage(ImageHelper.getInstance().getIconImage(Constants.APPLICATION_ICON_RESOURCE_NAME));
		setTitle(Constants.APPLICATION_NAME);
		setContentPane(createContentPane());
	}

	/**
	 * Erzeugt das ContentPane dieses Fensters, die Methode muss von abgeleiteten Klassen überschrieben
	 * werden.
	 */
	public abstract JPanel createContentPane();

	/**
	 * @return Returns the ctl.
	 */
	public BaseCtl getCtl() {
		return ctl;
	}

	public void setWaitCursor() {
		setCursor(WAIT_CURSOR);
	}

	public void setDefaultCursor() {
		setCursor(DEFAULT_CURSOR);
	}

}

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
package de.dknapps.pswgendesktop.gui.base;

import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.dknapps.pswgencore.CoreConstants;
import de.dknapps.pswgencore.util.ImageHelper;
import de.dknapps.pswgendesktop.DesktopConstants;

/**
 * <p>
 * Generische Basisklasse für die Views meiner Anwendungen.
 * </p>
 */
public abstract class BaseView extends JFrame {

	private static final long serialVersionUID = -2867571588508896513L;

	private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

	private static final Cursor WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);

	/** Der Controller für diese View */
	protected BaseCtl ctl = null;

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
				ctl.windowClosing(BaseView.this);
			}
		});
		setResizable(true);
		setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		setIconImage(ImageHelper.getInstance().getImage(DesktopConstants.APPLICATION_IMAGE_RESOURCE_NAME));
		setTitle(DesktopConstants.APPLICATION_NAME + " " + CoreConstants.APPLICATION_VERSION);
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

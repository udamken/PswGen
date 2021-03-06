/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2018 Uwe Damken
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

import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import de.dknapps.pswgencore.util.DomainException;
import de.dknapps.pswgendesktop.DesktopConstants;

/**
 * <p>
 * Generische Basisklasse für die Controller meiner Anwendungen.
 * </p>
 */
public class BaseCtl {

	/** Alle von diesem Controller gesteuerten Fenster */
	private List<Window> windows = new ArrayList<>();

	/**
	 * Konstruiert den Controller.
	 */
	public BaseCtl() {
		super();
	}

	/**
	 * Reagiert auf den Wunsch des Nutzers, das Fenster zu schließen.
	 */
	public void windowClosing(final BaseView view) {
		view.dispose(); // führt zum Aufruf von viewClosed
		removeWindow(view); // raus aus der Sammlung
	}

	/**
	 * Reagiert auf den Wunsch des Nutzers, den Dialog zu schließen.
	 */
	public void windowClosing(final BaseDialog dialog) {
		dialog.dispose();
		removeWindow(dialog); // raus aus der Sammlung
	}

	/**
	 * Fehler in Form eines Throwables behandeln (Logging und Fehler anzeigen).
	 */
	public void handleException(final Exception e) {
		if (e instanceof DomainException) {
			String msg = getGuiText(e.getMessage());
			JOptionPane.showMessageDialog(null, msg, DesktopConstants.APPLICATION_NAME,
					JOptionPane.WARNING_MESSAGE);
		} else {
			Logger.getGlobal().log(Level.SEVERE, "Exception caught: ", e);
			JOptionPane.showMessageDialog(null, e.toString(), DesktopConstants.APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Nimmt ein Fenster in die Sammlung der von diesem Controller gesteuerten Fenster auf.
	 */
	public boolean addWindow(final Window window) {
		return windows.add(window);
	}

	/**
	 * Nimmt ein Fenster aus der Sammlung der von diesem Controller gesteuerten Fenster.
	 */
	public boolean removeWindow(final Window window) {
		return windows.remove(window);
	}

	/**
	 * Liefert ein GUI-Text zu einem Widget oder einer GUI-Meldung.
	 */
	public String getGuiText(String name) {
		return WidgetFactory.getInstance().getGuiText(name);
	}

}
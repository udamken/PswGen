package net.sf.pswgen.gui.base;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2015 Uwe Damken

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import net.sf.pswgen.util.Constants;
import net.sf.pswgen.util.DomainException;

/**
 * <p>
 * Generische Basisklasse für die Controller meiner Anwendungen.
 * </p>
 * <p>
 * Copyright (C) 2005-2015 Uwe Damken
 * </p>
 */
public class BaseCtl {

	/** Alle von diesem Controller gesteuerten Views */
	public List<BaseView> views = new ArrayList<BaseView>();

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
		removeView(view); // raus aus der Sammlung
	}

	/**
	 * Fehler in Form eines Throwables behandeln (Logging und Fehler anzeigen).
	 */
	public void handleThrowable(final Throwable t) {
		t.printStackTrace();
		if (t instanceof DomainException) {
			String msg = getGuiText(t.getMessage());
			JOptionPane.showMessageDialog(null, msg, Constants.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);

		} else {
			JOptionPane.showMessageDialog(null, t.toString(), Constants.APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Nimmt ein View in die Sammlung der von diesem Controller gesteuerten Views auf.
	 */
	public boolean addView(final BaseView view) {
		return views.add(view);
	}

	/**
	 * Nimmt ein View in die Sammlung der von diesem Controller gesteuerten Views auf.
	 */
	public boolean removeView(final BaseView view) {
		return views.remove(view);
	}

	/**
	 * Liefert ein GUI-Text zu einem Widget oder einer GUI-Meldung.
	 */
	public String getGuiText(String name) {
		return WidgetFactory.getInstance().getGuiText(name);
	}

}
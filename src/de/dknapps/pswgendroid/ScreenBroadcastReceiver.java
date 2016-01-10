package de.dknapps.pswgendroid;

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

import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.dknapps.pswgen.util.Constants;

/**
 * <p>
 * Dieser BroadcastReceiver wird benutzt, um festzustellen, ob das Gerät inaktiv geworden, was in der Regel
 * dann der Fall ist, wenn der Bildschirm ausgeschaltet ist.
 * </p>
 * <p>
 * Copyright (C) 2014-2015 Uwe Damken
 * </p>
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(Constants.APPLICATION_PACKAGE_NAME + ".Logger");

	@Override
	public void onReceive(Context context, Intent intent) {
		LOGGER.log(Level.INFO, this.getClass().getName() + " received " + intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			PswGenAdapter.unloadServiceInfoList();
		}
	}
}

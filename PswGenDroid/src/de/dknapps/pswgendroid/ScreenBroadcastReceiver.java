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
package de.dknapps.pswgendroid;

import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * <p>
 * Dieser BroadcastReceiver wird benutzt, um festzustellen, ob das Gerät inaktiv geworden, was in der Regel
 * dann der Fall ist, wenn der Bildschirm ausgeschaltet ist.
 * </p>
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(DroidConstants.LOGGER_NAME);

	@Override
	public void onReceive(Context context, Intent intent) {
		LOGGER.log(Level.INFO, this.getClass().getName() + " received " + intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			PswGenAdapter.unloadServiceInfoList();
		}
	}
}

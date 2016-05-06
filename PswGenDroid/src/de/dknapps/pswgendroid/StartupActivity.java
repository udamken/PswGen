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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import de.dknapps.pswgencore.CoreConstants;

/**
 * <p>
 * Diese Activity wird beim Start der Anwendung aufgerufen und dient dazu, die Datei mit den Diensten zu
 * importieren und nach Eingabe der Passphrase zu laden.
 * </p>
 */
public class StartupActivity extends Activity implements PassphraseDialog.Listener {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(DroidConstants.LOGGER_NAME);

	/** Der BroadcastReceiver, der über SCREEN_OFF-Intents informiert wird */
	private BroadcastReceiver screenBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		EditText editFilepath = (EditText) findViewById(R.id.filepath);
		// Das ist auf dem Samsung S4 mini /storage/emulated/0/Download
		String defaultFilepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
				+ "Download" + File.separator + CoreConstants.SERVICES_FILENAME;
		SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_filename),
				Context.MODE_PRIVATE);
		String filepath = prefs.getString(getString(R.string.preference_filepath), defaultFilepath);
		editFilepath.setText(filepath);
		// Manche Intents, und so auch der SCREEN_OFF-Intent müssen im Code registriert werden, eine Angabe in
		// AndroidManifest.xml wäre wirklungslos. Nähere Informationen dazu finden sich in folgenden Links:
		// http://stackoverflow.com/questions/3651772/main-difference-between-manifest-and-programmatic-registering-of-broadcastreceiv
		// http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
		// Außerdem wird die Information über diesen Intent auch nur während der Applikation benötigt!
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		screenBroadcastReceiver = new ScreenBroadcastReceiver();
		registerReceiver(screenBroadcastReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(screenBroadcastReceiver); // der Ordnung halber
		super.onDestroy();
	}

	/**
	 * Fragt die Passphrase über einen Dialog ab, lädt die Dienste und verifiziert die Passphrase. Danach wird
	 * onPassphraseDialogPositive() oder onPassphraseDialogNegative() aufgerufen.
	 */
	public void onClickButtonOpenServices(final View buttonOpenServices) {
		EditText editFilepath = (EditText) findViewById(R.id.filepath);
		String filepath = editFilepath.getText().toString();
		// Dateipfad direkt speichern statt eines Einstellungsdialogs
		SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_filename),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(getString(R.string.preference_filepath), filepath);
		editor.commit();
		// Dann den Dialog zur Passphrase-Eingabe öffnen
		DialogFragment passphraseDialog = new PassphraseDialog();
		passphraseDialog.show(getFragmentManager(), "passphrase_dialog");
	}

	/**
	 * Öffnet die Hilfe-URL im Browser.
	 */
	public void onClickButtonOpenHelp(final View buttonOpenHelp) {
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.help_url)));
			startActivity(browserIntent);
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(this, e);
		}
	}

	/**
	 * Öffnet den About-Dialog.
	 */
	public void onClickButtonOpenAbout(final View buttonOpenAbout) {
		try {
			Intent aboutIntent = new Intent(this, AboutActivity.class);
			startActivity(aboutIntent);
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(this, e);
		}
	}

	/**
	 * Kopiert die per File angegebene Quelldatei in den übergebenen FileOutputStream.
	 */
	public static void copyFile(File sourceFile, FileOutputStream targetStream) throws IOException {
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(sourceFile));
		BufferedOutputStream writer = new BufferedOutputStream(targetStream);
		try {
			byte[] buff = new byte[8192];
			int numChars;
			while ((numChars = reader.read(buff, 0, buff.length)) != -1) {
				writer.write(buff, 0, numChars);
			}
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, CoreConstants.MSG_EXCP_SERVICES, e);
			}
		}
	}

	/**
	 * Nachdem die Diensteliste geladen wurden, zur Anzeige der Liste verzweigen.
	 */
	@Override
	public void onClickPassphraseDialogButtonPositive() {
		Intent listIntent = new Intent(this, ServiceListActivity.class);
		startActivity(listIntent);
	}

	/**
	 * Wenn im PassphraseDialog Abbrechen gedrückt wurde, ist hier nichts mehr zu tun.
	 */
	@Override
	public void onClickPassphraseDialogButtonNegative() {
	}

}

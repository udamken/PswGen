package de.dknapps.pswgendroid;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2016 Uwe Damken

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
import de.dknapps.pswgen.util.Constants;

/**
 * <p>
 * Diese Activity wird beim Start der Anwendung aufgerufen und dient dazu, die Datei mit den Diensten zu
 * importieren und nach Eingabe der Passphrase zu laden.
 * </p>
 * <p>
 * Copyright (C) 2014-2015 Uwe Damken
 * </p>
 */
public class StartupActivity extends Activity implements PassphraseDialog.Listener {

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(Constants.APPLICATION_PACKAGE_NAME + ".Logger");

	/** Der BroadcastReceiver, der über SCREEN_OFF-Intents informiert wird */
	private BroadcastReceiver screenBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		EditText editFilepath = (EditText) findViewById(R.id.filepath);
		// Das ist auf dem Samsung S4 mini /storage/emulated/0/Download
		String defaultFilepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
				+ "Download" + File.separator + Constants.SERVICES_FILENAME;
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
				LOGGER.log(Level.WARNING, Constants.MSG_EXCP_SERVICES, e);
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

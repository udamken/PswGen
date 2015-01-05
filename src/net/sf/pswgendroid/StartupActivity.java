package net.sf.pswgendroid;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2014 Uwe Damken

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.pswgen.util.Constants;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		EditText editImportFilepath = (EditText) findViewById(R.id.import_filepath);
		// Das ist auf dem Samsung S4 mini /storage/emulated/0/Download
		String defaultImportFilepath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + "Download" + File.separator + Constants.SERVICES_FILENAME;
		editImportFilepath.setText(defaultImportFilepath);
		// Manche Intents, und so auch der SCREEN_OFF-Intent müss im Code registriert werden, eine Angabe in
		// AndroidManifest.xml wäre wirklungslos. Nähere Informationen dazu finden sich in folgenden Links:
		// http://stackoverflow.com/questions/3651772/main-difference-between-manifest-and-programmatic-registering-of-broadcastreceiv
		// http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		BroadcastReceiver broadcastReceiver = new ScreenBroadcastReceiver();
		registerReceiver(broadcastReceiver, filter);
	}

	/**
	 * Importiert die Datei mit den Diensten von einem externen Speicher in den anwendungsspezifischen
	 * Speicherbereich, bzw. in den Bereich, der dem Zertifikat dieser Anwendung zugeordnet ist.
	 */
	public void onClickButtonImportServices(final View buttonImportServices) {
		EditText editImportFilepath = (EditText) findViewById(R.id.import_filepath);
		String importFilepath = editImportFilepath.getText().toString();
		try {
			File importFile = new File(importFilepath);
			if (importFile.exists()) {
				deleteFile(Constants.SERVICES_FILENAME); // Bisherige Datei kommentarlos löschen
				FileOutputStream out = openFileOutput(Constants.SERVICES_FILENAME, MODE_PRIVATE);
				copyFile(importFile, out);
				importFile.delete();
				String msg = MessageFormat.format(getString(R.string.file_imported), importFile.getPath(),
						Constants.SERVICES_FILENAME);
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			} else {
				String msg = MessageFormat.format(getString(R.string.file_missing), importFile.getPath());
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(this, e);
		}
	}

	/**
	 * Fragt die Passphrase über einen Dialog ab. Nach Eingabe und Bestätigunt der Passphrase wird
	 * onPassphraseDialogPositiveClick() aufgerufen.
	 */
	public void onClickButtonOpenServices(final View buttonOpenServices) {
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
	 * Prüft die vom Dialog übergebene Passphrase, den Verifizierungs- und den Versions-String und öffnet ggf.
	 * die Liste der aus der Datei geladenen Dienste.
	 */
	@Override
	public void onPassphraseDialogPositiveClick(DialogInterface dialog, String passphrase) {
		try {
			PswGenAdapter.loadServiceInfoList(openFileInput(Constants.SERVICES_FILENAME), passphrase);
			Intent listIntent = new Intent(this, ServiceListActivity.class);
			startActivity(listIntent);
		} catch (FileNotFoundException e) {
			String msg = MessageFormat.format(getString(R.string.file_missing), Constants.SERVICES_FILENAME);
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(this, e);
		}
	}

	@Override
	public void onPassphraseDialogNegativeClick(DialogInterface dialog) {
		// FIXME dkn Ist hier noch was zu tun?
	}

}

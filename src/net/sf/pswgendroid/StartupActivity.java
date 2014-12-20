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
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.pswgen.util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StartupActivity extends Activity {

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
	}

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
				// FIXME dkn Nachricht aus den Ressourcen holen
				Toast.makeText(
						this,
						"<" + importFile.getPath() + "> nach <" + Constants.SERVICES_FILENAME
								+ "> importiert.", Toast.LENGTH_LONG).show();
			} else {
				// FIXME dkn Nachricht aus den Ressourcen holen
				Toast.makeText(this, "<" + importFile.getPath() + "> existiert nicht.", Toast.LENGTH_LONG)
						.show();
			}
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(this, e);
		}
	}

	public void onClickButtonOpenServices(final View buttonOpenServices) {
		try {
			EditText editTextPassphrase = (EditText) findViewById(R.id.passphrase);
			String passphrase = editTextPassphrase.getText().toString();
			PswGenAdapter.loadServiceInfoList(openFileInput(Constants.SERVICES_FILENAME), passphrase);
			Intent listIntent = new Intent(this, ServiceListActivity.class);
			startActivity(listIntent);
		} catch (FileNotFoundException e) {
			// FIXME dkn Nachricht aus den Ressourcen holen
			Toast.makeText(this, "<" + Constants.SERVICES_FILENAME + "> existiert nicht.", Toast.LENGTH_LONG)
					.show();
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(this, e);
		}
	}

	public void onClickButtonOpenHelp(final View buttonOpenHelp) {
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_help)));
			startActivity(browserIntent);
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(this, e);
		}
	}

	public void onClickButtonOpenAbout(final View buttonOpenAbout) {
		try {
			Intent aboutIntent = new Intent(this, AboutActivity.class);
			startActivity(aboutIntent);
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(this, e);
		}
	}

	public static void copyFile(File sourceFile, FileOutputStream targetStream) throws IOException {
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(sourceFile));
		BufferedOutputStream writer = new BufferedOutputStream(targetStream);
		try {
			byte[] buff = new byte[8192];
			int numChars;
			while ((numChars = reader.read(buff, 0, buff.length)) != -1) {
				writer.write(buff, 0, numChars);
			}
		} catch (IOException ex) {
			// FIXME dkn Nachricht aus den Ressourcen holen
			throw new IOException("IOException beim Importieren von <" + sourceFile.getPath() + "> nach <"
					+ Constants.SERVICES_FILENAME + ">.");
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

}

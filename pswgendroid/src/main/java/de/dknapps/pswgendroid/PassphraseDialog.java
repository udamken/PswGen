/*
 * #%L
 * PswGen
 * %%
 * Copyright (C) 2005 - 2016 Uwe Damken
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.dknapps.pswgendroid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;

import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * <p>
 * Dieser Dialog fragt die Passphrase ab und ruft danach die Callback-Methoden des Listener-Interfaces auf,
 * das von der Activity implementiert wird, die den Dialog öffnet..
 * </p>
 */
public class PassphraseDialog extends DialogFragment {

	/**
	 * Alle Activity-Klassen, die diesen Dialog öffnen, müssen dieses Interface implementieren, damit sie
	 * informiert werden. wenn OK oder Abbrechen gedrückt wurde.
	 */
	public interface Listener {

		/**
		 * Es wurde OK gedrückt, die Passphrase validiert und die Diensteliste neu geladen.
		 */
		public void onClickPassphraseDialogButtonPositive();

		/**
		 * Es wurde Abbrechen gedrückt.
		 */
		public void onClickPassphraseDialogButtonNegative();
	}

	/** Das Argument zur Übergabe des Dateipfades vom Start zum PassphraseDialog */
	public static final String ARG_ITEM_ID = "item_id";

	/** Die View für die Eingabe der Passphrase */
	private EditText editTextPassphrase;

	/** Die aktuelle Instanz, die beim Drücken von OK oder Abbrechen informiert wird. */
	private Listener listener;

	/**
	 * Die öffnende Activity-Klasse als Listener-Instanz eintragen, die Activity muss das Listener-Interface
	 * implementieren.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activity-Klassen, die dieses Fragment nutzen, müssen {@link Listener} implementieren
		if (!(activity instanceof Listener)) {
			throw new ClassCastException(activity.getClass().getName() + " must implement "
					+ Listener.class.getName());
		}

		listener = (Listener) activity;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_passphrase, null);
		editTextPassphrase = (EditText) view.findViewById(R.id.passphrase);
		final AlertDialog passphraseDialog = builder.setView(view) //
				.setTitle(R.string.title_passphrase) //
				.setPositiveButton(R.string.button_ok, null) // wird wegen der Prüfung unten überschrieben
				.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dismiss();
						listener.onClickPassphraseDialogButtonNegative();
					}
				}).create();
		passphraseDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = passphraseDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						SharedPreferences prefs = getActivity().getSharedPreferences(
								getString(R.string.preferences_filename), Context.MODE_PRIVATE);
						String filepath = prefs.getString(getString(R.string.preference_filepath), null);
						try {
							FileInputStream input = new FileInputStream(filepath);
							String passphrase = editTextPassphrase.getText().toString();
							PswGenAdapter.loadServiceInfoList(input, passphrase);
							passphraseDialog.dismiss(); // Nur wenn alles okay ist, Dialog schließen ...
							listener.onClickPassphraseDialogButtonPositive(); // ... und weitermelden
						} catch (FileNotFoundException e) {
							String msg = MessageFormat.format(getString(R.string.file_missing), filepath);
							Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							PswGenAdapter.handleThrowable(getActivity(), e);
						}
					}
				});
			}
		});
		setCancelable(false); // passphraseDialog.setCancelable(false) bewirkt nichts
		passphraseDialog.setCanceledOnTouchOutside(false);
		return passphraseDialog;
	}
}

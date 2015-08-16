package net.sf.pswgendroid;

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import net.sf.pswgen.util.Constants;

/**
 * <p>
 * Dieser Dialog fragt die Passphrase ab und ruft danach die Callback-Methoden des Listener-Interfaces auf,
 * das von der Activity implementiert wird, die den Dialog öffnet..
 * </p>
 * <p>
 * Copyright (C) 2015 Uwe Damken
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
			throw new ClassCastException(
					activity.getClass().getName() + " must implement " + Listener.class.getName());
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
						try {
							FileInputStream input = getActivity().openFileInput(Constants.SERVICES_FILENAME);
							String passphrase = editTextPassphrase.getText().toString();
							PswGenAdapter.loadServiceInfoList(input, passphrase);
							passphraseDialog.dismiss(); // Nur wenn alles okay ist, Dialog schließen ...
							listener.onClickPassphraseDialogButtonPositive(); // ... und weitermelden
						} catch (FileNotFoundException e) {
							String msg = MessageFormat.format(getString(R.string.file_missing),
									Constants.SERVICES_FILENAME);
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

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * <p>
 * Dieser Dialog fragt die Passphrase ab und ruft danach die Callback-Methoden des Listener-Interfaces auf,
 * das von der Activity implementiert wird, die den Dialog öffnet..
 * </p>
 * <p>
 * Copyright (C) 2014-2015 Uwe Damken
 * </p>
 */
public class PassphraseDialog extends DialogFragment {

	/**
	 * Alle Activity-Klassen, die diesen Dialog öffnen, müssen dieses Interface implementieren, damit sie
	 * informiert werden. wenn OK oder Abbrechen gedrückt wurde.
	 */
	public interface Listener {

		/**
		 * Es wurde OK gedrückt, die (hoffentlich) eingegebene Passphrase wird mitgeliefert.
		 */
		public void onPassphraseDialogPositiveClick(DialogInterface dialog, String passphrase);

		/**
		 * Es wurde Abbrechen gedrückt.
		 */
		public void onPassphraseDialogNegativeClick(DialogInterface dialog);
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
		if (activity instanceof Listener) {
			listener = (Listener) activity;
		} else {
			throw new ClassCastException(activity.getClass().getName() + " must implement "
					+ Listener.class.getName());
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_passphrase, null);
		editTextPassphrase = (EditText) view.findViewById(R.id.passphrase);
		builder.setView(view).setTitle(R.string.title_passphrase)
				.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						String passphrase = editTextPassphrase.getText().toString();
						listener.onPassphraseDialogPositiveClick(dialog, passphrase);
					}
				}).setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						PassphraseDialog.this.getDialog().cancel();
						listener.onPassphraseDialogNegativeClick(dialog);
					}
				});
		return builder.create();
	}

}

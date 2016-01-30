/*******************************************************************************
 * PswGen - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGen on your mobile  
 *
 *     Copyright (C) 2005, 2016 Uwe Damken
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PasswordDialog extends DialogFragment {

	/** Das Argument zur Übergabe des Passworts */
	public static final String ARG_PASSWORD = "password";

	/** Das Argument zur Übergabe der Erläuterung zum Passwort */
	public static final String ARG_PASSWORD_EXPLANATION = "password_explanation";

	/** Felder in der Passwortanzeige */

	private TextView textViewPassword;
	private TextView textViewPasswordExplanation;

	/**
	 * Die öffnende Activity-Klasse als Listener-Instanz eintragen, die Activity muss das Listener-Interface
	 * implementieren.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_password, null);
		textViewPassword = (TextView) view.findViewById(R.id.password);
		textViewPassword.setText(getArguments().getString(ARG_PASSWORD));
		textViewPasswordExplanation = (TextView) view.findViewById(R.id.password_explanation);
		textViewPasswordExplanation.setText(getArguments().getString(ARG_PASSWORD_EXPLANATION));
		final AlertDialog passwordDialog = builder.setView(view) //
				.setTitle(R.string.title_password) //
				.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dismiss();
					}
				}).create();
		return passwordDialog;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!PswGenAdapter.isServiceInfoListLoaded()) { // Zwischendurch SCREEN_OFF gewesen?
			dismiss();
		}
	}

}

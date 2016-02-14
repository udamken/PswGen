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

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.dknapps.pswgen.model.ServiceInfo;
import de.dknapps.pswgen.util.PasswordFactory;
import de.dknapps.pswgendroid.R;

public class ServiceDetailFragment extends Fragment {

	/** Das Argument zur Übergabe des Dienstekürzels von der Liste zur Detailanzeige */
	public static final String ARG_ITEM_ID = "item_id";

	/** Der aktuell in der Detailanzeige dargestellte Dienst */
	private ServiceInfo currentServiceInfo;

	/** Felder in der Detailanzeige */

	private TextView textViewServiceAbbreviation;
	private TextView textViewAdditionalInfo;
	private TextView textViewLoginUrl;
	private TextView textViewLoginInfo;
	private TextView textViewAdditionalLoginInfo;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen
	 * orientation changes).
	 */
	public ServiceDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		if (!PswGenAdapter.isServiceInfoListLoaded()) { // Zwischendurch SCREEN_OFF gewesen?
			showEmptyCurrentServiceInfo(); // Anzeigefelder des Dienstes löschen
			if (getActivity() instanceof ServiceDetailActivity) {
				// Wenn die aufrufende Activity keine ServiceDetailActivity ist, dann ist es
				// die ServiceListActivity und in dem Fall soll nur dort die Abfrage der
				// Passphrase erfolgen, damit das nicht zweimal (hier und dort) geschieht
				DialogFragment passphraseDialog = new PassphraseDialog();
				passphraseDialog.show(getActivity().getFragmentManager(), "passphrase_dialog");
			}
		} else {
			loadAndShowCurrentServiceInfo(); // Dienst gemäß des übergebenen Arguments laden
		}
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_service_detail, container, false);

		textViewServiceAbbreviation = ((TextView) rootView.findViewById(R.id.service_detail));
		textViewAdditionalInfo = ((TextView) rootView.findViewById(R.id.additional_info));
		textViewLoginUrl = ((TextView) rootView.findViewById(R.id.login_url));
		textViewLoginInfo = ((TextView) rootView.findViewById(R.id.login_info));
		textViewAdditionalLoginInfo = ((TextView) rootView.findViewById(R.id.additional_login_info));

		return rootView;
	}

	/**
	 * Öffnet die Login-URL im Browser und kopiert die Login-Informationen in die Zwischenablage.
	 *
	 * FIXME dkn Die callingActivity kann durch getActivity() ersetzt werden.
	 */
	public void onClickButtonOpenUrl(final Activity callingActivity, @SuppressWarnings("unused")
	final View buttonOpenUrl) {
		try {
			String loginUrl = currentServiceInfo.getLoginUrl();
			if (!loginUrl.startsWith("http://") && !loginUrl.startsWith("https://")) {
				loginUrl = "http://" + loginUrl;
			}
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl));
			startActivity(browserIntent);
			copyLoginInfo(callingActivity);
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(callingActivity, e);
		}
	}

	/**
	 * Kopiert die Login-Informationen in die Zwischenablage.
	 *
	 * FIXME dkn Die callingActivity kann durch getActivity() ersetzt werden.
	 */
	public void onClickButtonCopyLoginInfo(final Activity callingActivity, @SuppressWarnings("unused")
	final View buttonOpenUrl) {
		try {
			copyLoginInfo(callingActivity);
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(callingActivity, e);
		}
	}

	/**
	 * Generiert das Passwort und kopiert es in die Zwischenablage.
	 *
	 * FIXME dkn Die callingActivity kann durch getActivity() ersetzt werden.
	 */
	public void onClickButtonCopyPassword(final Activity callingActivity, @SuppressWarnings("unused")
	final View buttonOpenUrl) {
		try {
			String password = getValidatedOrGeneratedPassword();
			ClipboardManager clipboard = (ClipboardManager) callingActivity
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setPrimaryClip(ClipData.newPlainText(null, password));
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(callingActivity, e);
		}
	}

	/**
	 * Generiert das Passwort und zeigt es an.
	 *
	 * FIXME dkn Die callingActivity kann durch getActivity() ersetzt werden.
	 */
	public void onClickButtonDisplayPassword(final Activity callingActivity, @SuppressWarnings("unused")
	final View buttonOpenUrl) {
		try {
			Bundle arguments = new Bundle();
			String password = getValidatedOrGeneratedPassword();
			String passwordExplanation = getPasswordExplanation(password);
			arguments.putString(PasswordDialog.ARG_PASSWORD, password);
			arguments.putString(PasswordDialog.ARG_PASSWORD_EXPLANATION, passwordExplanation);
			DialogFragment passwordDialog = new PasswordDialog();
			passwordDialog.setArguments(arguments);
			passwordDialog.show(getActivity().getFragmentManager(), "password_dialog");
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(callingActivity, e);
		}
	}

	/**
	 * Dienst (erneut) raussuchen und anzeigen.
	 */
	public void onClickPassphraseDialogButtonPositive() {
		showCurrentServiceInfo();
	}

	/**
	 * Die Passphrase hätte erneut eingegeben werden müssen, das ist aber nicht erfolgt, darum gehts zurück
	 * zum Anfang, zur StartupActivity.
	 */
	public void onClickPassphraseDialogButtonNegative() {
		Intent startupIntent = new Intent(getActivity(), StartupActivity.class);
		startActivity(startupIntent);
	}

	/**
	 * Liefert true, wenn gerade ein Dienst angezeigt wird.
	 */
	public boolean hasCurrentServiceInfo() {
		return currentServiceInfo != null;
	}

	/**
	 * Lädt den aktuellen Dienst anhand des als Argumente erhaltenen Dienstekürzels und zeigt ihn an oder
	 * löscht die Anzeigefelder.
	 */
	private void loadAndShowCurrentServiceInfo() {
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			currentServiceInfo = PswGenAdapter.getServiceInfo(getArguments().getString(ARG_ITEM_ID));
		}
		showCurrentServiceInfo();
	}

	/**
	 * Zeigt den aktuellen Dienst an, wenn vorhanden, oder löscht die Anzeigefelder.
	 */
	private void showCurrentServiceInfo() {
		if (hasCurrentServiceInfo()) {
			textViewServiceAbbreviation.setText(currentServiceInfo.getServiceAbbreviation());
			textViewAdditionalInfo.setText(currentServiceInfo.getAdditionalInfo());
			textViewLoginUrl.setText(currentServiceInfo.getLoginUrl());
			textViewLoginInfo.setText(currentServiceInfo.getLoginInfo());
			textViewAdditionalLoginInfo.setText(currentServiceInfo.getAdditionalLoginInfo());
		} else {
			showEmptyCurrentServiceInfo();
		}
	}

	/**
	 * Löscht die Anzeigefelder.
	 */
	private void showEmptyCurrentServiceInfo() {
		textViewServiceAbbreviation.setText(null);
		textViewAdditionalInfo.setText(null);
		textViewLoginUrl.setText(null);
		textViewLoginInfo.setText(null);
		textViewAdditionalLoginInfo.setText(null);
	}

	/**
	 * Kopiert die Login-Informationen in die Zwischenablage.
	 *
	 * FIXME dkn Die callingActivity kann durch getActivity() ersetzt werden.
	 */
	private void copyLoginInfo(final Activity callingActivity) {
		ClipboardManager clipboard = (ClipboardManager) callingActivity
				.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setPrimaryClip(ClipData.newPlainText(null, currentServiceInfo.getLoginInfo()));
	}

	/**
	 * Liefert das eingegebene oder ein generiertes Passwort. Sobald entweder das Passwort oder das
	 * wiederholte Passwort eingegeben wurden, müssen sie übereinstimmen, sonst wird eine Exception geworfen,
	 * die zu einer Fehlermeldung führt. Eine Eingabe hat also in jedem Fall Vorrang vor der Generierung.
	 */
	private String getValidatedOrGeneratedPassword() {
		return PasswordFactory.getPassword(currentServiceInfo, PswGenAdapter.getValidatedPassphrase());
	}

	/**
	 * Liefert eine immer lesbare Erläuterung zum übergebenen Passwort.
	 */
	private String getPasswordExplanation(String password) {
		final String prefixLowercaseLetters = getString(R.string.prefix_lowercase_letters);
		final String prefixUppercaseLetters = getString(R.string.prefix_uppercase_letters);
		final String prefixDigits = getString(R.string.prefix_digits);
		final String prefixSpecialChars = getString(R.string.prefix_special_chars);
		return PasswordFactory.getPasswordExplanation(password, prefixLowercaseLetters,
				prefixUppercaseLetters, prefixDigits, prefixSpecialChars);
	}

}

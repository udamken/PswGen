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

import net.sf.pswgen.model.ServiceInfo;
import net.sf.pswgen.util.PasswordFactory;
import android.app.Activity;
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
import android.widget.Toast;

/**
 * <p>
 * Das Fragment für die Detailanzeige eines Dienstes, es ist bei großen Bildschirmen in die
 * {@link ServiceListActivity} eingebunden und wird dann gleichzeitig mit der Liste der Dienste angezeigt. Bei
 * kleineren Bildschirmen erfolgt die Anzeige separat in der {@link ServiceDetailActivity}.
 * </p>
 * <p>
 * Copyright (C) 2014 Uwe Damken
 * </p>
 */
public class ServiceDetailFragment extends Fragment {

	/** Das Argument zur Übergabe des Dienstekürzels von der Liste zur Detailanzeige */
	public static final String ARG_ITEM_ID = "item_id";

	/** Der aktuell in der Detailanzeige dargestellte Dienst */
	private ServiceInfo currentServiceInfo;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen
	 * orientation changes).
	 */
	public ServiceDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Ausgewählten Dienst anhand des übergebenen Dienstekürzels holen
			currentServiceInfo = PswGenAdapter.getServiceInfo(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_service_detail, container, false);

		// Werte des Dienstes in die Detailanzeige übernehmen, wenn ein Dienst ausgewählt ist
		if (currentServiceInfo != null) {
			((TextView) rootView.findViewById(R.id.service_detail)).setText(currentServiceInfo
					.getServiceAbbreviation());
			((TextView) rootView.findViewById(R.id.additional_info)).setText(currentServiceInfo
					.getAdditionalInfo());
			((TextView) rootView.findViewById(R.id.login_url)).setText(currentServiceInfo.getLoginUrl());
			((TextView) rootView.findViewById(R.id.login_info)).setText(currentServiceInfo.getLoginInfo());
			((TextView) rootView.findViewById(R.id.additional_login_info)).setText(currentServiceInfo
					.getAdditionalLoginInfo());
		}

		return rootView;
	}

	/**
	 * Öffnet die Login-URL im Browser und kopiert die Login-Informationen in die Zwischenablage.
	 */
	public void onClickButtonOpenUrl(final Activity callingActivity, final View buttonOpenUrl) {
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
	 */
	public void onClickButtonCopyLoginInfo(final Activity callingActivity, final View buttonOpenUrl) {
		try {
			copyLoginInfo(callingActivity);
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(callingActivity, e);
		}
	}

	/**
	 * Generiert das Passwort und kopiert es in die Zwischenablage.
	 */
	public void onClickButtonCopyPassword(final Activity callingActivity, final View buttonOpenUrl) {
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
	 */
	public void onClickButtonDisplayPassword(final Activity callingActivity, final View buttonOpenUrl) {
		try {
			String password = getValidatedOrGeneratedPassword();
			Toast.makeText(callingActivity, password, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(callingActivity, e);
		}
	}

	/**
	 * Kopiert die Login-Informationen in die Zwischenablage.
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

}

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
import net.sf.pswgen.util.Constants;
import net.sf.pswgen.util.DomainException;
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
 * A fragment representing a single Service detail screen. This fragment is either contained in a
 * {@link ServiceListActivity} in two-pane mode (on tablets) or a {@link ServiceDetailActivity} on handsets.
 */
public class ServiceDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private ServiceInfo mItem;

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
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = PswGenAdapter.getServiceInfo(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_service_detail, container, false);

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.service_detail)).setText(mItem.getServiceAbbreviation());
			((TextView) rootView.findViewById(R.id.additional_info)).setText(mItem.getAdditionalInfo());
			((TextView) rootView.findViewById(R.id.login_url)).setText(mItem.getLoginUrl());
			((TextView) rootView.findViewById(R.id.login_info)).setText(mItem.getLoginInfo());
			((TextView) rootView.findViewById(R.id.additional_login_info)).setText(mItem
					.getAdditionalLoginInfo());
		}

		return rootView;
	}

	public void onClickButtonOpenUrl(final Activity callingActivity, final View buttonOpenUrl) {
		try {
			String loginUrl = mItem.getLoginUrl();
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

	public void onClickButtonCopyLoginInfo(final Activity callingActivity, final View buttonOpenUrl) {
		try {
			copyLoginInfo(callingActivity);
		} catch (Exception e) {
			PswGenAdapter.handleThrowable(callingActivity, e);
		}
	}

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
		clipboard.setPrimaryClip(ClipData.newPlainText(null, mItem.getLoginInfo()));
	}

	/**
	 * Liefert das eingegebene oder ein generiertes Passwort. Sobald entweder das Passwort oder das
	 * wiederholte Passwort eingegeben wurden, müssen sie übereinstimmen, sonst wird eine Exception geworfen,
	 * die zu einer Fehlermeldung führt. Eine Eingabe hat also in jedem Fall Vorrang vor der Generierung.
	 * 
	 * TODO dkn SpecialCharacters sollten eigentlich mitgespeichert werden, oder? TODO dkn Die Differenzierung
	 * gespeichertes/generiertes Passwort in die PasswordFactory?
	 */
	private String getValidatedOrGeneratedPassword() {
		String password = mItem.getPassword();
		final String passwordRepeated = mItem.getPasswordRepeated();
		if (password.length() == 0 && passwordRepeated.length() == 0) { // Beide leer? => generieren
			ensureAtLeastDefaultSpecialCharacters();
			validateServiceAbbreviation(mItem.getServiceAbbreviation());
			password = PasswordFactory.getPassword(mItem, PswGenAdapter.getValidatedPassphrase());
		} else {
			if (!password.equals(passwordRepeated)) { // Mismatch?
				throw new DomainException("PasswordMismatchMsg");
			}
		}
		return password;
	}

	/**
	 * Sonderzeichen müssen gesetzt sein, und wenn es nur eine Default-Auswahl ist.
	 */
	private void ensureAtLeastDefaultSpecialCharacters() {
		if (mItem.getSpecialCharacters() == null || mItem.getSpecialCharacters().length() == 0) {
			mItem.setSpecialCharacters(Constants.SPECIAL_CHARS);
		}
	}

	/**
	 * Eingabewert des Dienstekürzels überprüfen.
	 */
	private void validateServiceAbbreviation(final String serviceAbbreviation) {
		if (serviceAbbreviation.length() == 0) {
			throw new DomainException("ServiceAbbreviationEmptyMsg");
		}
	}

}

/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2018 Uwe Damken
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

import android.app.Activity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import de.dknapps.pswgencore.model.ServiceInfo;
import de.dknapps.pswgencore.util.PasswordFactory;

public class ServiceDetailFragment extends Fragment {

    private ServiceMaintenanceViewModel viewModel;

    /**
     * Die für die Tastatur bereitgestellte Login-Information.
     */
    private static String providedLoginInfo = null;
    /**
     * Das für die Tastatur bereitgestellte Password.
     */
    private static String providedPassword = null;
    /**
     * State of picking an input method.
     */
    private InputMethodPickingState inputMethodPickingState = InputMethodPickingState.NONE;
    /**
     * Der aktuell in der Detailanzeige dargestellte Dienst
     */
    private ServiceInfo currentServiceInfo;
    /**
     * Felder in der Detailanzeige
     */

    private TextView textViewServiceAbbreviation;
    private TextView textViewAdditionalInfo;
    private TextView textViewLoginUrl;
    private TextView textViewLoginInfo;
    private TextView textViewAdditionalLoginInfo;
    private TextView textViewLabelUseOldPassphrase;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen
     * orientation changes).
     */
    public ServiceDetailFragment() {
    }

    /**
     * Liefert die für die Tastatur bereitgestellte Login-Information.
     */
    public static String getProvidedLoginInfo() {
        return providedLoginInfo;
    }

    /**
     * Liefert das für die Tastatur bereitgestellte Password.
     */
    public static String getProvidedPassword() {
        return providedPassword;
    }

    /**
     * Setzt die für die Tastatur bereitstellten Login-Information und das Passwort zurück.
     */
    public static void resetProvidedLoginInformationPassword() {
        providedLoginInfo = null;
        providedPassword = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        viewModel = ViewModelProviders.of(getActivity()).get(ServiceMaintenanceViewModel.class);
    }

    @Override
    public void onResume() {
        if (!PswGenAdapter.isServiceInfoListLoaded()) { // Zwischendurch SCREEN_OFF gewesen?
            showEmptyCurrentServiceInfo(); // Anzeigefelder des Dienstes löschen
        } else {
            loadAndShowCurrentServiceInfo(); // Dienst gemäß des übergebenen Arguments laden
        }
        super.onResume();
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
     * Lädt den aktuellen Dienst anhand des als Argumente erhaltenen Dienstekürzels und zeigt ihn an oder
     * löscht die Anzeigefelder.
     */
    private void loadAndShowCurrentServiceInfo() {
        currentServiceInfo = PswGenAdapter.getServiceInfo(viewModel.getCurrentServiceAbbreviation().getValue());
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
            textViewLabelUseOldPassphrase.setVisibility((currentServiceInfo.isUseOldPassphrase()) ? View.VISIBLE : View.INVISIBLE);
        } else {
            showEmptyCurrentServiceInfo();
        }
    }

    /**
     * Liefert true, wenn gerade ein Dienst angezeigt wird.
     */
    public boolean hasCurrentServiceInfo() {
        return currentServiceInfo != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_service_detail, container, false);

        textViewServiceAbbreviation = ((TextView) rootView.findViewById(R.id.service_detail));
        textViewAdditionalInfo = ((TextView) rootView.findViewById(R.id.additional_info));
        textViewLoginUrl = ((TextView) rootView.findViewById(R.id.login_url));
        textViewLoginInfo = ((TextView) rootView.findViewById(R.id.login_info));
        textViewAdditionalLoginInfo = ((TextView) rootView.findViewById(R.id.additional_login_info));
        textViewLabelUseOldPassphrase = ((TextView) rootView.findViewById(R.id.label_use_old_passphrase));

        rootView.findViewById(R.id.button_open_and_provide).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickButtonOpenAndProvide(v);
            }
        });
        rootView.findViewById(R.id.button_provide).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickButtonProvide(v);
            }
        });
        rootView.findViewById(R.id.button_open_url).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickButtonOpenUrl(v);
            }
        });
        rootView.findViewById(R.id.button_copy_login_info).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickButtonCopyLoginInfo(v);
            }
        });
        rootView.findViewById(R.id.button_copy_password).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickButtonCopyPassword(v);
            }
        });
        rootView.findViewById(R.id.button_display_password).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickButtonDisplayPassword(v);
            }
        });

        return rootView;
    }

    /**
     * Öffnet die Login-URL im Browser und kopiert die Login-Informationen in die Zwischenablage.
     */
    public void onClickButtonOpenUrl(final View buttonOpenUrl) {
        try {
            openUrl(currentServiceInfo.getLoginUrl());
            copyToClipboard(getActivity(), currentServiceInfo.getLoginInfo());
        } catch (Exception e) {
            PswGenAdapter.handleThrowable(getActivity(), e);
        }
    }

    /**
     * Öffnet die übergebene URL im Browser.
     */
    private void openUrl(String loginUrl) {
        if (!loginUrl.startsWith("http://") && !loginUrl.startsWith("https://")) {
            loginUrl = "http://" + loginUrl;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl));
        startActivity(browserIntent);
    }

    /**
     * Kopiert den übergebenen String in die Zwischenablage.
     */
    private void copyToClipboard(final Activity callingActivity, final String value) {
        ClipboardManager clipboard = (ClipboardManager) callingActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(null, value));
    }

    /**
     * Öffnet die Login-URL im Browser, lässt den Nutzer die Tastatur wechseln und stellt Login-Informationen und Passwort für die
     * Tastatur bereit.
     */
    public void onClickButtonOpenAndProvide(final View buttonOpenAndProvide) {
        try {
            inputMethodPickingState = InputMethodPickingState.INITIATING; // => Öffnen der URL in onWindowFocusChanged()
            provideLoginInfoAndPassword();
            // Das Öffnen der URL erfolgt nach dem Schließen der Tastaturauswahl in onWindowFocusChanged()
        } catch (Exception e) {
            PswGenAdapter.handleThrowable(getActivity(), e);
        }
    }

    /**
     * Lässt den Nutzer die Tastatur wechseln und stellt Login-Informationen und Passwort für die Tastatur bereit.
     */
    private void provideLoginInfoAndPassword() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();
        providedLoginInfo = currentServiceInfo.getLoginInfo();
        providedPassword = getValidatedOrGeneratedPassword();
    }

    /**
     * Liefert das eingegebene oder ein generiertes Passwort. Sobald entweder das Passwort oder das
     * wiederholte Passwort eingegeben wurden, müssen sie übereinstimmen, sonst wird eine Exception geworfen,
     * die zu einer Fehlermeldung führt. Eine Eingabe hat also in jedem Fall Vorrang vor der Generierung.
     */
    private String getValidatedOrGeneratedPassword() {
        String passphrase = (currentServiceInfo.isUseOldPassphrase()) ?
                PswGenAdapter.getOldPassphrase() :
                PswGenAdapter.getValidatedPassphrase();
        return PasswordFactory.getPassword(currentServiceInfo, passphrase);
    }

    /**
     * Prüft beim Fokuswechsel, ob gerade die Tastaturauswahl aktiv ist, bzw. war und ruft danach das Öffnen einer URL auf. Ohne
     * diesen Umstand wird manchmal die Tastaturauswahl geöffnet, aber vom gestarteten Browser sofort verdeckt. Die Idee ist
     * dieser Antwort entnommen: https://stackoverflow.com/a/14069079/2532583
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        if (inputMethodPickingState == InputMethodPickingState.INITIATING) {
            inputMethodPickingState = InputMethodPickingState.ONGOING;
        } else if (inputMethodPickingState == InputMethodPickingState.ONGOING) {
            inputMethodPickingState = InputMethodPickingState.NONE;
            openUrl(currentServiceInfo.getLoginUrl());
        }
    }

    /**
     * Lässt den Nutzer die Tastatur wechseln und stellt Login-Informationen und Passwort für die
     * Tastatur bereit.
     */
    public void onClickButtonProvide(final View buttonProvide) {
        try {
            provideLoginInfoAndPassword();
        } catch (Exception e) {
            PswGenAdapter.handleThrowable(getActivity(), e);
        }
    }

    /**
     * Kopiert die Login-Informationen in die Zwischenablage.
     */
    public void onClickButtonCopyLoginInfo(final View buttonOpenUrl) {
        try {
            copyToClipboard(getActivity(), currentServiceInfo.getLoginInfo());
        } catch (Exception e) {
            PswGenAdapter.handleThrowable(getActivity(), e);
        }
    }

    /**
     * Generiert das Passwort und kopiert es in die Zwischenablage.
     */
    public void onClickButtonCopyPassword(final View buttonOpenUrl) {
        try {
            copyToClipboard(getActivity(), getValidatedOrGeneratedPassword());
        } catch (Exception e) {
            PswGenAdapter.handleThrowable(getActivity(), e);
        }
    }

    /**
     * Generiert das Passwort und zeigt es an.
     */
    public void onClickButtonDisplayPassword(final View buttonOpenUrl) {
        try {
            Bundle arguments = new Bundle();
            String password = getValidatedOrGeneratedPassword();
            String passwordExplanation = getPasswordExplanation(password);
            arguments.putString(PasswordDialog.ARG_PASSWORD, password);
            arguments.putString(PasswordDialog.ARG_PASSWORD_EXPLANATION, passwordExplanation);
            DialogFragment passwordDialog = new PasswordDialog();
            passwordDialog.setArguments(arguments);
            passwordDialog.show(getActivity().getSupportFragmentManager(), "password_dialog");
        } catch (Exception e) {
            PswGenAdapter.handleThrowable(getActivity(), e);
        }
    }

    /**
     * Liefert eine immer lesbare Erläuterung zum übergebenen Passwort.
     */
    private String getPasswordExplanation(String password) {
        final String prefixLowercaseLetters = getString(R.string.prefix_lowercase_letters);
        final String prefixUppercaseLetters = getString(R.string.prefix_uppercase_letters);
        final String prefixDigits = getString(R.string.prefix_digits);
        final String prefixSpecialChars = getString(R.string.prefix_special_chars);
        return PasswordFactory.getPasswordExplanation(password, prefixLowercaseLetters, prefixUppercaseLetters, prefixDigits,
                prefixSpecialChars);
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

    private enum InputMethodPickingState {NONE, INITIATING, ONGOING}

}

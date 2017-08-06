/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile
 *
 *     Copyright (C) 2005-2017 Uwe Damken
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

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.IBinder;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

/**
 * <p>
 * Implementiert eine virtuelle Tastatur, um Anmeldeinformationen und Passworte nicht über die Zwischenablage kopieren zu müssen.
 *
 * https://code.tutsplus.com/tutorials/create-a-custom-keyboard-on-android--cms-22615
 * </p>
 */
public class PswGenInputMethodEditor extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    @Override
    public View onCreateInputView() {
        KeyboardView keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboardView.setKeyboard(new Keyboard(this, R.xml.service));
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        switch (primaryCode) {
            case (int) '1':
                sendKeyEvents(ic, ServiceDetailFragment.getProvidedLoginInfo());
                break;
            case (int) '2':
                sendKeyEvents(ic, ServiceDetailFragment.getProvidedPassword());
                break;
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                // FALL-THRU: Fertig bewirkt auch das Zurücksetzen der Tastatur und der bereitgestellten Daten
            case Keyboard.KEYCODE_CANCEL:
                ServiceDetailFragment.resetProvidedLoginInformationPassword();
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                final IBinder token = this.getWindow().getWindow().getAttributes().token;
                imm.switchToLastInputMethod(token);
                break;
            default:
                // should not happen
        }
    }

    private void sendKeyEvents(InputConnection ic, String input) {
        if (input == null) {
            return;
        }
        KeyCharacterMap kcm = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
        for (KeyEvent event : kcm.getEvents(input.toCharArray())) {
            ic.sendKeyEvent(event);
        }
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }
}

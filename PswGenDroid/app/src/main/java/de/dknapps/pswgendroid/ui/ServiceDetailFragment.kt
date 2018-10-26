/************************************************************************************
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
 ************************************************************************************/
package de.dknapps.pswgendroid.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProviders
import de.dknapps.pswgencore.util.PasswordFactory
import de.dknapps.pswgendroid.R
import de.dknapps.pswgendroid.adapter.PswGenAdapter
import de.dknapps.pswgendroid.event.WindowFocusChangedEvent
import de.dknapps.pswgendroid.model.ServiceMaintenanceViewModel
import de.dknapps.pswgendroid.model.ServiceMaintenanceViewModel.InputMethodPickingState.*
import kotlinx.android.synthetic.main.service_detail_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ServiceDetailFragment : androidx.fragment.app.Fragment() {

    companion object {

        /**
         * Login information provided to the keyboard.
         */
        var providedLoginInfo: String? = null
            private set

        /**
         * Password provided to the keyboard.
         */
        var providedPassword: String? = null
            private set

        fun newInstance() = ServiceDetailFragment()

        /**
         * Reset provide login information and password to null.
         */
        fun resetProvidedLoginInformationPassword() {
            providedLoginInfo = null
            providedPassword = null
        }

    }

    private lateinit var viewModel: ServiceMaintenanceViewModel

    /**
     * State of picking an input method.
     */
    var inputMethodPickingState = NONE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.service_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ServiceMaintenanceViewModel::class.java)

        buttonOpenAndProvide.setOnClickListener { onClickButtonOpenAndProvide() }
        buttonProvide.setOnClickListener { onClickButtonProvide() }
        buttonOpenUrl.setOnClickListener { onClickButtonOpenUrl() }
        buttonCopyLoginInfo.setOnClickListener { onClickButtonCopyLoginInfo() }
        buttonCopyPassword.setOnClickListener { onClickButtonCopyPassword() }
        buttonDisplayPassword.setOnClickListener { onClickButtonDisplayPassword() }
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        with(viewModel) {
            serviceAbbreviation.text = currentServiceInfo?.serviceAbbreviation
            additionalInfo.text = currentServiceInfo?.additionalInfo
            loginUrl.text = currentServiceInfo?.loginUrl
            loginInfo.text = currentServiceInfo?.loginInfo
            additionalLoginInfo.text = currentServiceInfo?.additionalLoginInfo
            labelUseOldPassphrase.visibility =
                    if (currentServiceInfo != null && currentServiceInfo!!.isUseOldPassphrase) View.VISIBLE else View.INVISIBLE
        }
        super.onResume()
    }

    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    /**
     * When focus changes it is checked whether the keyboard chooser is or was active. And if so the login url is
     * opened in the browser. Without the keyboard chooser gets opened but hidden at once by the browser. Solution
     * inspired by: https://stackoverflow.com/a/14069079/2532583
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWindowFocusChanged(event: WindowFocusChangedEvent) {
        if (inputMethodPickingState == INITIATING) {
            inputMethodPickingState = ONGOING
        } else if (inputMethodPickingState == ONGOING) {
            inputMethodPickingState = NONE
            openUrl(viewModel.currentServiceInfo!!.loginUrl)
        }
    }

    /**
     * Let user choose the app's keyboard to provide login information and password and open login url in browser.
     */
    private fun onClickButtonOpenAndProvide() {
        try {
            inputMethodPickingState = INITIATING
            provideLoginInfoAndPassword()
            // url will be opened in onWindowFocusChanged() after the keyboard has been chosen
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }

    }

    /**
     * Let user choose the app's keyboard to provide login information and password.
     */
    private fun onClickButtonProvide() {
        try {
            provideLoginInfoAndPassword()
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }

    }

    /**
     * Open login url in browser and copy login information into the clipboard.
     */
    fun onClickButtonOpenUrl() {
        try {
            openUrl(viewModel.currentServiceInfo!!.loginUrl)
            copyToClipboard(activity!!, viewModel.currentServiceInfo!!.loginInfo)
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }

    }

    /**
     * Copies login information into the clipboard.
     */
    private fun onClickButtonCopyLoginInfo() {
        try {
            copyToClipboard(activity!!, viewModel.currentServiceInfo!!.loginInfo)
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }

    }

    /**
     * Copies entered or generated password into the clipboard.
     */
    private fun onClickButtonCopyPassword() {
        try {
            copyToClipboard(activity!!, getValidatedOrGeneratedPassword())
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }

    }

    /**
     * Display entered or generated password with an explanation.
     */
    private fun onClickButtonDisplayPassword() {
        // TODO Display password as fragment?
        try {
            val arguments = Bundle()
            val password = getValidatedOrGeneratedPassword()
            val passwordExplanation = getPasswordExplanation(password)
//            arguments.putString(PasswordDialog.ARG_PASSWORD, password)
//            arguments.putString(PasswordDialog.ARG_PASSWORD_EXPLANATION, passwordExplanation)
//            val passwordDialog = PasswordDialog()
//            passwordDialog.setArguments(arguments)
//            passwordDialog.show(activity!!.supportFragmentManager, "password_dialog")
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }
    }

    /**
     * Let user choose the keyboard and provide login information and entered or generated password to it.
     */
    private fun provideLoginInfoAndPassword() {
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showInputMethodPicker()
        providedLoginInfo = viewModel.currentServiceInfo!!.getLoginInfo()
        providedPassword = getValidatedOrGeneratedPassword()
    }

    /**
     * Returns an entered or generated password. If the entered password does not match the repeated password
     * an exception is thrown. An entered password has a higher priority than a generated password.
     */
    private fun getValidatedOrGeneratedPassword(): String {
        val passphrase = if (viewModel.currentServiceInfo!!.isUseOldPassphrase)
            viewModel.oldPassphrase
        else
            viewModel.validatedPassphrase
        // TODO Add delegate to PswGenAdapter?
        return PasswordFactory.getPassword(viewModel.currentServiceInfo, passphrase)
    }

    /**
     * Open url in browser.
     */
    private fun openUrl(loginUrl: String) {
        var loginUrl = loginUrl
        if (!loginUrl.startsWith("http://") && !loginUrl.startsWith("https://")) {
            loginUrl = "http://$loginUrl"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl))
        startActivity(browserIntent)
    }

    /**
     * Returns a readable explanation for the given password.
     */
    private fun getPasswordExplanation(password: String): String {
        val prefixLowercaseLetters = getString(R.string.prefix_lowercase_letters)
        val prefixUppercaseLetters = getString(R.string.prefix_uppercase_letters)
        val prefixDigits = getString(R.string.prefix_digits)
        val prefixSpecialChars = getString(R.string.prefix_special_chars)
        return PasswordFactory.getPasswordExplanation(
            password, prefixLowercaseLetters, prefixUppercaseLetters, prefixDigits,
            prefixSpecialChars
        )
    }

    /**
     * Copies the given string into the clipboard.
     */
    private fun copyToClipboard(callingActivity: Activity, value: String) {
        val clipboard = callingActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newPlainText(null, value)
    }

}

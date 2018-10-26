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
package de.dknapps.pswgendroid.ui.startup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dknapps.pswgendroid.R
import kotlinx.android.synthetic.main.startup_fragment.*
import java.io.File
import de.dknapps.pswgencore.CoreConstants
import de.dknapps.pswgencore.util.FileHelper
import de.dknapps.pswgendroid.adapter.CommonJsonReaderWriterFactoryAndroidImpl
import de.dknapps.pswgendroid.adapter.PswGenAdapter
import de.dknapps.pswgendroid.event.OpenAboutClickedEvent
import org.greenrobot.eventbus.EventBus

class StartupFragment : androidx.fragment.app.Fragment() {

    lateinit var prefs: SharedPreferences

    companion object {
        fun newInstance() = StartupFragment()
    }

    private lateinit var viewModel: ServiceMaintenanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.startup_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ServiceMaintenanceViewModel::class.java)
        // TODO: Use the ViewModel
        prefs = activity!!.getSharedPreferences(getString(R.string.preferences_filename), Context.MODE_PRIVATE)

        val defaultFilepath = (Environment.getExternalStorageDirectory().absolutePath + File.separator
                + "Upload" + File.separator + CoreConstants.OTHER_SERVICES_FILENAME) // something like /storage/emulated/0/Download
        filepath.setText(prefs.getString(getString(R.string.preference_filepath), defaultFilepath))

        val defaultOtherFilepath = (Environment.getExternalStorageDirectory().absolutePath + File.separator
                + "Download" + File.separator + CoreConstants.SERVICES_FILENAME) // something like /storage/emulated/0/Download
        otherFilepath.setText(prefs.getString(getString(R.string.preference_other_filepath), defaultOtherFilepath))

        // TODO Add SCREEN_OFF Broadcast Receiver

        buttonOpenServices.setOnClickListener { onClickButtonOpenServices() }
        buttonOpenImeSettings.setOnClickListener { onClickButtonOpenImeSettings() }
        buttonOpenHelp.setOnClickListener { onClickButtonOpenHelp() }
        buttonOpenAbout.setOnClickListener { onClickButtonOpenAbout() }
    }

    /**
     * SLoads services from file and stores the given filepaths on success.
     */
    private fun onClickButtonOpenServices() {
        try {
            val servicesFile = File(filepath.text.toString())
            val otherServicesFile = File(otherFilepath.text.toString())
            val fileHelper = FileHelper.getInstance(CommonJsonReaderWriterFactoryAndroidImpl())
            PswGenAdapter.loadServiceInfoList(servicesFile, otherServicesFile, passphrase.text.toString())!!
            viewModel.validatedPassphrase = passphrase.text.toString()
            viewModel.oldPassphrase = oldPassphrase.text.toString()
            val editor = prefs.edit()
            editor.putString(getString(R.string.preference_filepath), filepath.text.toString())
            editor.putString(getString(R.string.preference_other_filepath), otherFilepath.text.toString())
            editor.commit()
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }
    }

    /**
     * Open settings to activate keyboard for copying login information and password without clipboard.
     */
    private fun onClickButtonOpenImeSettings() {
        startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
    }

    /**
     * Send intent to open up help url in browser.
     */
    private fun onClickButtonOpenHelp() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.help_url)))
        startActivity(browserIntent)
    }

    /**
     * Send event to switch to about fragment.
     */
    private fun onClickButtonOpenAbout() {
        EventBus.getDefault().post(OpenAboutClickedEvent());
    }

}

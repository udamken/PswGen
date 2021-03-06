/************************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile
 *
 *     Copyright (C) 2005-2019 Uwe Damken
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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import de.dknapps.pswgencore.CoreConstants
import de.dknapps.pswgendroid.R
import de.dknapps.pswgendroid.R.string.msg_file_last_modified
import de.dknapps.pswgendroid.adapter.PswGenAdapter
import de.dknapps.pswgendroid.event.OpenAboutClickedEvent
import de.dknapps.pswgendroid.event.ProgressEndedEvent
import de.dknapps.pswgendroid.event.ServiceListLoadedEvent
import de.dknapps.pswgendroid.event.ProgressStartingEvent
import de.dknapps.pswgendroid.model.ServiceMaintenanceViewModel
import de.dknapps.pswgendroid.util.TextChangedListener
import kotlinx.android.synthetic.main.startup_fragment.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import kotlin.concurrent.thread

class StartupFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance() = StartupFragment()
    }

    private lateinit var viewModel: ServiceMaintenanceViewModel

    private lateinit var prefs: SharedPreferences

    private val filepathTextChangedListener = TextChangedListener {
        filepathInfo.text = deriveInfo(it.toString())
    }

    private val otherFilepathTextChangedListener = TextChangedListener {
        otherFilepathInfo.text = deriveInfo(it.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.startup_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        viewModel = ViewModelProviders.of(requireActivity()).get(ServiceMaintenanceViewModel::class.java)

        prefs = requireActivity().getSharedPreferences(getString(R.string.preferences_filename), Context.MODE_PRIVATE)

        val defaultFilepath = (Environment.getExternalStorageDirectory().absolutePath + File.separator
                + "Upload" + File.separator + CoreConstants.OTHER_SERVICES_FILENAME) // something like /storage/emulated/0/Download
        filepath.setText(prefs.getString(getString(R.string.preference_filepath), defaultFilepath))

        val defaultOtherFilepath = (Environment.getExternalStorageDirectory().absolutePath + File.separator
                + "Download" + File.separator + CoreConstants.SERVICES_FILENAME) // something like /storage/emulated/0/Download
        otherFilepath.setText(prefs.getString(getString(R.string.preference_other_filepath), defaultOtherFilepath))

        buttonOpenServices.setOnClickListener { onClickButtonOpenServices() }
        buttonOpenImeSettings.setOnClickListener { onClickButtonOpenImeSettings() }
        buttonOpenHelp.setOnClickListener { onClickButtonOpenHelp() }
        buttonOpenAbout.setOnClickListener { onClickButtonOpenAbout() }
    }

    override fun onResume() {
        super.onResume()
        // Passphrases are deleted for security reasons otherwise screen lock leads to "unloading" the services
        // and returns to startup fragment but the passphrase would still be there which were not very helpful.
        passphrase.setText("")
        oldPassphrase.setText("")

        filepath.addTextChangedListener(filepathTextChangedListener)
        otherFilepath.addTextChangedListener(otherFilepathTextChangedListener)

        // update because of possible outside changes
        filepathTextChangedListener.onTextChanged(filepath.text.toString())
        otherFilepathTextChangedListener.onTextChanged(otherFilepath.text.toString())
    }

    override fun onPause() {
        filepath.removeTextChangedListener(filepathTextChangedListener)
        otherFilepath.removeTextChangedListener(otherFilepathTextChangedListener)
        super.onPause()
    }

    /**
     * SLoads services from file and stores the given filepaths on success.
     */
    private fun onClickButtonOpenServices() {
        EventBus.getDefault().post(ProgressStartingEvent())
        // action to be done in a thread to show progress bar before, view updates and errors on main ui thread again
        thread(start = true) {
            try {
                viewModel.servicesFile = File(filepath.text.toString())
                val otherServicesFile = File(otherFilepath.text.toString())
                viewModel.services = PswGenAdapter.loadServiceInfoList(
                    viewModel.servicesFile!!,
                    otherServicesFile,
                    passphrase.text.toString()
                )
                viewModel.validatedPassphrase = passphrase.text.toString()
                viewModel.oldPassphrase = oldPassphrase.text.toString()
                val editor = prefs.edit()
                editor.putString(getString(R.string.preference_filepath), filepath.text.toString())
                editor.putString(getString(R.string.preference_other_filepath), otherFilepath.text.toString())
                editor.apply()
                EventBus.getDefault().post(ServiceListLoadedEvent())
            } catch (e: Exception) {
                requireActivity().runOnUiThread() {
                    PswGenAdapter.handleException(requireActivity(), e)
                }
            } finally {
                EventBus.getDefault().post(ProgressEndedEvent())
            }
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
        EventBus.getDefault().post(OpenAboutClickedEvent())
    }

    /**
     * Returns displayable information about the file specified by the given filepath.
     */
    @SuppressLint("StringFormatInvalid")
    private fun deriveInfo(filePath: String): String {
        val file = File(filePath)
        return if (!file.exists()) {
            getString(R.string.msg_file_cannot_be_found)
        } else if (!file.canRead()) {
            getString(R.string.msg_file_cannot_be_read)
        } else {
            getString(msg_file_last_modified, CoreConstants.TIMESTAMP_FORMAT.format(file.lastModified()))
        }
    }

}

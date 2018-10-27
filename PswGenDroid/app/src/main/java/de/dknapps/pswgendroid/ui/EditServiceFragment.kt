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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import de.dknapps.pswgencore.model.ServiceInfo
import de.dknapps.pswgendroid.R
import de.dknapps.pswgendroid.adapter.PswGenAdapter
import de.dknapps.pswgendroid.model.ServiceMaintenanceViewModel
import kotlinx.android.synthetic.main.edit_service_fragment.*

class EditServiceFragment : androidx.fragment.app.Fragment() {

    companion object {

        fun newInstance() = EditServiceFragment()

    }

    private lateinit var viewModel: ServiceMaintenanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_service_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity!! as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProviders.of(activity!!).get(ServiceMaintenanceViewModel::class.java)

        buttonClearService.setOnClickListener { onClickButtonClearService() }
        buttonRemoveService.setOnClickListener { onClickButtonRemoveService() }
        buttonStoreService.setOnClickListener { onClickButtonStoreService() }
    }

    override fun onResume() {
        super.onResume()
        // When the screen gets locked services are unloaded. Therefore we return to previous fragment
        // if there is currently no service selected (probably because of screen lock).
        if (viewModel.currentServiceInfo == null) {
            activity!!.supportFragmentManager.popBackStack()
        } else {
            putServiceToView(viewModel.currentServiceInfo!!)
        }
    }

    /**
     * Clear all fields of the currently edited service.
     */
    private fun onClickButtonClearService() {
        try {
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }

    }

    /**
     * Remove service with edited service abbreviation from list and store all services to file.
     */
    private fun onClickButtonRemoveService() {
        try {
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }

    }

    /**
     * Add edited service to list or replace it and store all services to file.
     */
    private fun onClickButtonStoreService() {
        try {
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }

    }

    /**
     * Copy values to be displayed into UI (method name identical with PswGenDesktop).
     */
    private fun putServiceToView(si: ServiceInfo) {
        serviceAbbreviation.setText(si.serviceAbbreviation)
        additionalInfo.setText(si.additionalInfo)
        loginUrl.setText(si.loginUrl)
        loginInfo.setText(si.loginInfo)
        additionalLoginInfo.setText(si.additionalLoginInfo)
        labelUseOldPassphrase.visibility = if (si.isUseOldPassphrase) View.VISIBLE else View.INVISIBLE
        lastUpdate.setText(si.lastUpdate)
    }

    /**
     * Return service with values from UI (method name identical with PswGenDesktop).
     */
    private fun getServiceFromView(): ServiceInfo {
        val si = ServiceInfo(serviceAbbreviation.text.toString())
        si.additionalInfo = additionalInfo.text.toString()
        si.loginUrl = loginUrl.text.toString()
        si.loginInfo = loginInfo.text.toString()
        si.additionalLoginInfo = additionalLoginInfo.text.toString()
        si.isUseOldPassphrase = labelUseOldPassphrase.visibility == View.VISIBLE
        // last update is set not set from the view but from outside
        return si
    }

}

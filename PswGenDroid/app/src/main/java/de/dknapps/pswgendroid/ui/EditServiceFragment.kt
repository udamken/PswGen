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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import de.dknapps.pswgencore.model.ServiceInfo
import de.dknapps.pswgencore.util.DomainException
import de.dknapps.pswgendroid.R
import de.dknapps.pswgendroid.adapter.PswGenAdapter
import de.dknapps.pswgendroid.event.ServiceDeletedEvent
import de.dknapps.pswgendroid.model.ServiceMaintenanceViewModel
import kotlinx.android.synthetic.main.edit_service_fragment.*
import org.greenrobot.eventbus.EventBus


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
        if (viewModel.editedServiceInfo == null) {
            activity!!.supportFragmentManager.popBackStack()
        } else {
            putServiceToView(viewModel.editedServiceInfo!!)
        }
    }

    override fun onPause() {
        viewModel.editedServiceInfo = getServiceFromView()
        super.onPause()
    }

    /**
     * Clear all fields of the currently edited service.
     */
    private fun onClickButtonClearService() {
        try {
            if (cancelOnDirty()) { // cancel action?
                return
            }
            clearService()
        } catch (e: Exception) {
            PswGenAdapter.handleThrowable(activity!!, e)
        }
    }

    /**
     * Remove service with edited service abbreviation from list and store all services to file.
     */
    private fun onClickButtonRemoveService() {
        try {
            val abbreviation = serviceAbbreviation.text.toString()
            validateServiceAbbreviation(abbreviation)
            AlertDialog.Builder(activity!!) //
                .setTitle(R.string.app_name) //
                .setMessage("$abbreviation${getText(R.string.RemoveServiceMsg)}") //
                .setPositiveButton(R.string.yes) { _, _ ->
                    try {
                        viewModel.services!!.removeServiceInfo(abbreviation)
                        PswGenAdapter.saveServiceInfoList(
                            viewModel.servicesFile!!,
                            viewModel.services!!,
                            viewModel.validatedPassphrase!!
                        )
                        EventBus.getDefault().post(ServiceDeletedEvent());
                    } catch (e: Exception) {
                        PswGenAdapter.handleThrowable(activity!!, e)
                    }
                } //
                .setNegativeButton(R.string.no, null) //
                .show()
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

    /**
     * Returns true if the action should be cancelled or false if changes should be stored or discarded.
     */
    private fun cancelOnDirty(): Boolean {
        // TODO Implement cancelOnDirty ... supportNavigateUp must check back stack edit service fragment
//        if (mainView.isDirty()) {
//            val chosenOption = JOptionPane.showConfirmDialog(
//                mainView,
//                mainView.getServiceAbbreviation() + getGuiText("SaveChangesMsg"),
//                DesktopConstants.APPLICATION_NAME, JOptionPane.YES_NO_CANCEL_OPTION
//            )
//            if (chosenOption == JOptionPane.YES_OPTION) { // Geänderte Werte speichern?
//                storeService(mainView)
//            } else if (chosenOption == JOptionPane.CANCEL_OPTION || chosenOption == JOptionPane.CLOSED_OPTION) {
//                return true
//            }
//        }
        return false
    }

    /**
     * Clear service fields and reset additional info to the current date.
     */
    private fun clearService() {
        val si = ServiceInfo()
        si.resetAdditionalInfo()
        putServiceToView(si)
    }

    /**
     * Throws a domain exception if the given abbreviation is empty.
     */
    private fun validateServiceAbbreviation(abbreviaton: String) {
        if (abbreviaton.isEmpty()) {
            throw DomainException("ServiceAbbreviationEmptyMsg")
        }
    }

}

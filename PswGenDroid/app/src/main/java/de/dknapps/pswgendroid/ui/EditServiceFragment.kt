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
import de.dknapps.pswgencore.CoreConstants
import de.dknapps.pswgencore.model.ServiceInfo
import de.dknapps.pswgencore.util.ConverterHelper
import de.dknapps.pswgencore.util.DomainException
import de.dknapps.pswgencore.util.EmptyHelper
import de.dknapps.pswgendroid.R
import de.dknapps.pswgendroid.adapter.PswGenAdapter
import de.dknapps.pswgendroid.model.ServiceMaintenanceViewModel
import de.dknapps.pswgendroid.util.TextChangedListener
import kotlinx.android.synthetic.main.edit_service_fragment.*


class EditServiceFragment : androidx.fragment.app.Fragment() {

    companion object {

        fun newInstance() = EditServiceFragment()

    }

    private lateinit var viewModel: ServiceMaintenanceViewModel

    private val dirtyTextChangedListener = TextChangedListener { viewModel.isDirty = true }

    private val dirtyOnClickListener = View.OnClickListener { viewModel.isDirty = true }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_service_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProviders.of(requireActivity()).get(ServiceMaintenanceViewModel::class.java)

        buttonClearService.setOnClickListener { onClickButtonClearService() }
        buttonRemoveService.setOnClickListener { onClickButtonRemoveService() }
        buttonStoreService.setOnClickListener { onClickButtonStoreService() }
        buttonUseNewPassphrase.setOnClickListener { onClickButtonUseNewPassphrase() }
    }

    override fun onResume() {
        super.onResume()
        // When the screen gets locked services are unloaded. Therefore we return to previous fragment
        // if there is currently no service selected (probably because of screen lock).
        if (viewModel.editedServiceInfo == null) {
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            putServiceToView(viewModel.editedServiceInfo!!)
            addAllDirtyListener()
        }
    }

    override fun onPause() {
        removeAllDirtyListener()
        viewModel.editedServiceInfo = getServiceFromView()
        super.onPause()
    }

    /**
     * Clear all fields of the currently edited service.
     */
    private fun onClickButtonClearService() {
        if (viewModel.isDirty) {
            AlertDialog.Builder(requireActivity()) //
                .setTitle(R.string.app_name) //
                .setMessage(R.string.DiscardChangesMsg) //
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    clearService()
                } //
                .setNegativeButton(android.R.string.cancel, null) //
                .show()
        } else {
            clearService()
        }
    }

    /**
     * Remove service with edited service abbreviation from list and store all services to file.
     */
    private fun onClickButtonRemoveService() {
        try {
            val abbreviation = serviceAbbreviation.text.toString()
            validateServiceAbbreviation(abbreviation)
            AlertDialog.Builder(requireActivity()) //
                .setTitle(R.string.app_name) //
                .setMessage("$abbreviation${getText(R.string.RemoveServiceMsg)}") //
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    removeServiceAnyway(abbreviation)
                } //
                .setNegativeButton(android.R.string.cancel, null) //
                .show()
        } catch (e: Exception) {
            PswGenAdapter.handleException(requireActivity(), e)
        }
    }

    /**
     * Add edited service to list or replace it and store all services to file.
     */
    private fun onClickButtonStoreService() {
        try {
            val abbreviation = serviceAbbreviation.text.toString()
            validateServiceAbbreviation(abbreviation)
            if (viewModel.services!!.getServiceInfo(abbreviation) != null) { // service does exist?
                AlertDialog.Builder(requireActivity()) //
                    .setTitle(R.string.app_name) //
                    .setMessage("$abbreviation${getText(R.string.OverwriteServiceMsg)}") //
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        storeServiceAnyway()
                    } //
                    .setNegativeButton(android.R.string.no, null) //
                    .show()
            } else {
                storeServiceAnyway()
            }
        } catch (e: Exception) {
            PswGenAdapter.handleException(requireActivity(), e)
        }
    }

    /**
     * Fill additional info with current date and change service to no longer use the old passphrase.
     */
    private fun onClickButtonUseNewPassphrase() {
        val si = getServiceFromView()
        si.resetAdditionalInfo()
        si.isUseOldPassphrase = false
        putServiceToView(si)
    }

    /**
     * Add a listener to all fields that sets a dirty tag if the content was changed.
     */
    private fun addAllDirtyListener() {
        serviceAbbreviation.addTextChangedListener(dirtyTextChangedListener)
        additionalInfo.addTextChangedListener(dirtyTextChangedListener)
        loginUrl.addTextChangedListener(dirtyTextChangedListener)
        loginInfo.addTextChangedListener(dirtyTextChangedListener)
        additionalLoginInfo.addTextChangedListener(dirtyTextChangedListener)
        useSmallLetters.setOnClickListener(dirtyOnClickListener)
        smallLettersCount.addTextChangedListener(dirtyTextChangedListener)
        smallLettersStartIndex.addTextChangedListener(dirtyTextChangedListener)
        smallLettersEndIndex.addTextChangedListener(dirtyTextChangedListener)
        useCapitalLetters.setOnClickListener(dirtyOnClickListener)
        capitalLettersCount.addTextChangedListener(dirtyTextChangedListener)
        capitalLettersStartIndex.addTextChangedListener(dirtyTextChangedListener)
        capitalLettersEndIndex.addTextChangedListener(dirtyTextChangedListener)
        useDigits.setOnClickListener(dirtyOnClickListener)
        digitsCount.addTextChangedListener(dirtyTextChangedListener)
        digitsStartIndex.addTextChangedListener(dirtyTextChangedListener)
        digitsEndIndex.addTextChangedListener(dirtyTextChangedListener)
        useSpecialCharacters.setOnClickListener(dirtyOnClickListener)
        specialCharacters.addTextChangedListener(dirtyTextChangedListener)
        specialCharactersCount.addTextChangedListener(dirtyTextChangedListener)
        specialCharactersStartIndex.addTextChangedListener(dirtyTextChangedListener)
        specialCharactersEndIndex.addTextChangedListener(dirtyTextChangedListener)
        totalCharacterCount.addTextChangedListener(dirtyTextChangedListener)
        password.addTextChangedListener(dirtyTextChangedListener)
        passwordRepeated.addTextChangedListener(dirtyTextChangedListener)
        // useOldPassphrase: dirty tag is set in onClickButtonUseNewPassphrase()
        // lastUpdate: no dirty tag to be set, is read-only
    }

    /**
     * Remove the listener that sets a dirty tag if the content was changed from all fields.
     */
    private fun removeAllDirtyListener() {
        serviceAbbreviation.removeTextChangedListener(dirtyTextChangedListener)
        additionalInfo.removeTextChangedListener(dirtyTextChangedListener)
        loginUrl.removeTextChangedListener(dirtyTextChangedListener)
        loginInfo.removeTextChangedListener(dirtyTextChangedListener)
        additionalLoginInfo.removeTextChangedListener(dirtyTextChangedListener)
        useSmallLetters.setOnClickListener(null)
        smallLettersCount.removeTextChangedListener(dirtyTextChangedListener)
        smallLettersStartIndex.removeTextChangedListener(dirtyTextChangedListener)
        smallLettersEndIndex.setOnClickListener(null)
        useCapitalLetters.setOnClickListener(dirtyOnClickListener)
        capitalLettersCount.removeTextChangedListener(dirtyTextChangedListener)
        capitalLettersStartIndex.removeTextChangedListener(dirtyTextChangedListener)
        capitalLettersEndIndex.removeTextChangedListener(dirtyTextChangedListener)
        useDigits.setOnClickListener(dirtyOnClickListener)
        digitsCount.removeTextChangedListener(dirtyTextChangedListener)
        digitsStartIndex.removeTextChangedListener(dirtyTextChangedListener)
        digitsEndIndex.removeTextChangedListener(dirtyTextChangedListener)
        useSpecialCharacters.setOnClickListener(dirtyOnClickListener)
        specialCharacters.removeTextChangedListener(dirtyTextChangedListener)
        specialCharactersCount.removeTextChangedListener(dirtyTextChangedListener)
        specialCharactersStartIndex.removeTextChangedListener(dirtyTextChangedListener)
        specialCharactersEndIndex.removeTextChangedListener(dirtyTextChangedListener)
        totalCharacterCount.removeTextChangedListener(dirtyTextChangedListener)
        password.removeTextChangedListener(dirtyTextChangedListener)
        passwordRepeated.removeTextChangedListener(dirtyTextChangedListener)
        // useOldPassphrase: dirty tag is set in onClickButtonUseNewPassphrase()
        // lastUpdate: no dirty tag to be set, is read-only
    }

    /**
     * Remove service from (by marking it as deleted) regardless whether it exists or not.
     */
    private fun removeServiceAnyway(abbreviation: String) {
        try {
            viewModel.services!!.removeServiceInfo(abbreviation)
            PswGenAdapter.saveServiceInfoList(
                viewModel.servicesFile!!,
                viewModel.services!!,
                viewModel.validatedPassphrase!!
            )
            clearService()
        } catch (e: Exception) {
            PswGenAdapter.handleException(requireActivity(), e)
        }
    }

    /**
     * Get service from view and store it regardless whether it already exists or not.
     */
    private fun storeServiceAnyway() {
        try {
            val si = getServiceFromView()
            si.isDeleted = false
            si.resetTimeMillis()
            viewModel.services!!.putServiceInfo(si)
            PswGenAdapter.saveServiceInfoList(
                viewModel.servicesFile!!,
                viewModel.services!!,
                viewModel.validatedPassphrase!!
            )
            putServiceToView(si) // update timestamp
            viewModel.isDirty = false
        } catch (e: Exception) {
            PswGenAdapter.handleException(requireActivity(), e)
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
        useSmallLetters.isChecked = si.isUseSmallLetters
        smallLettersCount.setText(ConverterHelper.toString(si.smallLettersCount))
        smallLettersStartIndex.setText(ConverterHelper.toString(si.smallLettersStartIndex))
        smallLettersEndIndex.setText(ConverterHelper.toString(si.smallLettersEndIndex))
        useCapitalLetters.isChecked = si.isUseCapitalLetters
        capitalLettersCount.setText(ConverterHelper.toString(si.capitalLettersCount))
        capitalLettersStartIndex.setText(ConverterHelper.toString(si.capitalLettersStartIndex))
        capitalLettersEndIndex.setText(ConverterHelper.toString(si.capitalLettersEndIndex))
        useDigits.isChecked = si.isUseDigits
        digitsCount.setText(ConverterHelper.toString(si.digitsCount))
        digitsStartIndex.setText(ConverterHelper.toString(si.digitsStartIndex))
        digitsEndIndex.setText(ConverterHelper.toString(si.digitsEndIndex))
        useSpecialCharacters.isChecked = si.isUseSpecialCharacters
        specialCharacters.setText(si.specialCharacters)
        ensureAtLeastDefaultSpecialCharacters()
        specialCharactersCount.setText(ConverterHelper.toString(si.specialCharactersCount))
        specialCharactersStartIndex.setText(ConverterHelper.toString(si.specialCharactersStartIndex))
        specialCharactersEndIndex.setText(ConverterHelper.toString(si.specialCharactersEndIndex))
        totalCharacterCount.setText(ConverterHelper.toString(si.totalCharacterCount))
        password.setText(si.password)
        passwordRepeated.setText(si.passwordRepeated)
        if (si.isUseOldPassphrase) {
            labelUseOldPassphrase.visibility = View.VISIBLE
            buttonStoreService.visibility = View.INVISIBLE
            buttonUseNewPassphrase.visibility = View.VISIBLE
        } else {
            labelUseOldPassphrase.visibility = View.INVISIBLE
            buttonStoreService.visibility = View.VISIBLE
            buttonUseNewPassphrase.visibility = View.INVISIBLE
        }
        lastUpdate.text = si.lastUpdate
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
        si.isUseSmallLetters = useSmallLetters.isChecked
        si.smallLettersCount = ConverterHelper.toInt(smallLettersCount.text.toString())
        si.smallLettersStartIndex = ConverterHelper.toInt(smallLettersStartIndex.text.toString())
        si.smallLettersEndIndex = ConverterHelper.toInt(smallLettersEndIndex.text.toString())
        si.isUseCapitalLetters = useCapitalLetters.isChecked
        si.capitalLettersCount = ConverterHelper.toInt(capitalLettersCount.text.toString())
        si.capitalLettersStartIndex = ConverterHelper.toInt(capitalLettersStartIndex.text.toString())
        si.capitalLettersEndIndex = ConverterHelper.toInt(capitalLettersEndIndex.text.toString())
        si.isUseDigits = useDigits.isChecked
        si.digitsCount = ConverterHelper.toInt(digitsCount.text.toString())
        si.digitsStartIndex = ConverterHelper.toInt(digitsStartIndex.text.toString())
        si.digitsEndIndex = ConverterHelper.toInt(digitsEndIndex.text.toString())
        si.isUseSpecialCharacters = useSpecialCharacters.isChecked
        ensureAtLeastDefaultSpecialCharacters()
        si.specialCharacters = specialCharacters.text.toString()
        si.specialCharactersCount = ConverterHelper.toInt(specialCharactersCount.text.toString())
        si.specialCharactersStartIndex = ConverterHelper.toInt(specialCharactersStartIndex.text.toString())
        si.specialCharactersEndIndex = ConverterHelper.toInt(specialCharactersEndIndex.text.toString())
        si.totalCharacterCount = ConverterHelper.toInt(totalCharacterCount.text.toString())
        si.password = password.text.toString()
        si.passwordRepeated = passwordRepeated.text.toString()
        si.isUseOldPassphrase = labelUseOldPassphrase.visibility == View.VISIBLE
        // last update is set not set from the view but from outside
        return si
    }

    /**
     * Ensure special characters don't fall empty by setting a default set of special characters if needed.
     */
    private fun ensureAtLeastDefaultSpecialCharacters() {
        if (EmptyHelper.isEmpty(specialCharacters.text.toString())) {
            specialCharacters.setText(CoreConstants.SPECIAL_CHARS)
        }
    }

    /**
     * Clear service fields and reset additional info to the current date.
     */
    private fun clearService() {
        val si = ServiceInfo()
        si.resetAdditionalInfo()
        putServiceToView(si)
        viewModel.isDirty = false
    }

    /**
     * Throws a domain exception if the given abbreviation is empty.
     */
    private fun validateServiceAbbreviation(abbreviation: String) {
        if (abbreviation.isEmpty()) {
            throw DomainException("ServiceAbbreviationEmptyMsg")
        }
    }

}

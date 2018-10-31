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
import de.dknapps.pswgendroid.model.ServiceMaintenanceViewModel
import kotlinx.android.synthetic.main.display_password_fragment.*

class DisplayPasswordFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance() = DisplayPasswordFragment()
    }

    private lateinit var viewModel: ServiceMaintenanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.display_password_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProviders.of(requireActivity()).get(ServiceMaintenanceViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        // When the screen gets locked services are unloaded. Therefore we return to previous fragment
        // if there is currently no service or no password available (probably because of screen lock).
        if (!viewModel.retrieveService() || viewModel.password == null) {
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            putServiceToView(viewModel.currentServiceInfo)
            password.text = viewModel.password!!
            passwordExplanation.text = viewModel.passwordExplanation!!
        }
    }

    /**
     * Copy values to be displayed into UI (method name identical with PswGenDesktop).
     */
    private fun putServiceToView(si: ServiceInfo) {
        serviceAbbreviation.text = si.serviceAbbreviation
        loginInfo.text = si.loginInfo
        password.text = si.additionalLoginInfo
    }

}

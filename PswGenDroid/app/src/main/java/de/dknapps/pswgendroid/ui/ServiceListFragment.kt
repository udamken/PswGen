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

import android.R.id.text1
import android.R.layout.simple_list_item_activated_1
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProviders
import de.dknapps.pswgencore.model.ServiceInfo
import de.dknapps.pswgendroid.model.ServiceMaintenanceViewModel

class ServiceListFragment : androidx.fragment.app.ListFragment() {

    companion object {
        fun newInstance() = ServiceListFragment()
    }

    private lateinit var viewModel: ServiceMaintenanceViewModel

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.about_fragment, container, false)
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ServiceMaintenanceViewModel::class.java)

        listAdapter = ArrayAdapter<ServiceInfo>(
            activity!!,
            simple_list_item_activated_1, text1, viewModel.services!!.getServices(false)
        )
    }
}

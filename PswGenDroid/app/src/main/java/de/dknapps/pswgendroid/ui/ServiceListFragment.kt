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
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProviders
import de.dknapps.pswgencore.model.ServiceInfo
import de.dknapps.pswgendroid.R
import de.dknapps.pswgendroid.event.ServiceSelectedEvent
import de.dknapps.pswgendroid.model.ServiceMaintenanceViewModel
import org.greenrobot.eventbus.EventBus

class ServiceListFragment : ListFragment() {

    companion object {
        fun newInstance() = ServiceListFragment()
    }

    private lateinit var viewModel: ServiceMaintenanceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.options_menu, menu)
        val searchMenuItem = menu!!.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // action not handled, call default mechanism
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (listAdapter!! as ArrayAdapter<ServiceInfo>).filter.filter(newText)
                return true // action handled, no more to do
            }

        })
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.about_fragment, container, false)
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ServiceMaintenanceViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        // When the screen gets locked services are unloaded. Therefore we return to previous fragment
        // if there are currently no service loaded (probably because of screen lock).
        if (viewModel.services == null) {
            activity!!.supportFragmentManager.popBackStack()
        } else {
            listAdapter = ArrayAdapter<ServiceInfo>(
                activity!!,
                simple_list_item_activated_1, text1, viewModel.services!!.getServices(false)
            )
        }
    }

    override fun onListItemClick(listView: ListView?, view: View?, position: Int, id: Long) {
        super.onListItemClick(listView, view, position, id)
        viewModel.currentServiceInfo = listAdapter.getItem(position)!! as ServiceInfo
        EventBus.getDefault().post(ServiceSelectedEvent());
    }

}

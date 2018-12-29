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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import de.dknapps.pswgendroid.R
import de.dknapps.pswgendroid.event.*
import de.dknapps.pswgendroid.model.ServiceMaintenanceViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ServiceMaintenanceActivity : AppCompatActivity() {

    private lateinit var viewModel: ServiceMaintenanceViewModel

    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.resetModel()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ServiceMaintenanceViewModel::class.java)

        setContentView(R.layout.service_maintenance_activity)

        // If this is the first call the startup fragment should be opened
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, StartupFragment.newInstance())
                .commitNow()
        }

        // Get informed when screen got locked to "unload" the services and to return to startup fragment
        // for security reasons. ServiceListFragment and ServiceDetailFragment implement the return.
        registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))

    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    public override fun onDestroy() {
        unregisterReceiver(screenOffReceiver)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (viewModel.isDirty) {
            AlertDialog.Builder(this) //
                .setTitle(R.string.app_name) //
                .setMessage(R.string.DiscardChangesMsg) //
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.isDirty = false
                    supportFragmentManager.popBackStack()
                } //
                .setNegativeButton(android.R.string.cancel, null) //
                .show()
            false
        } else {
            supportFragmentManager.popBackStack()
            true
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenAboutClickedEvent(event: OpenAboutClickedEvent) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AboutFragment.newInstance())
            .addToBackStack(AboutFragment::class.java.name)
            .commit()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onServiceListLoadedEvent(event: ServiceListLoadedEvent) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ServiceListFragment.newInstance())
            .addToBackStack(ServiceListFragment::class.java.name)
            .commit()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onServiceSelectedEvent(event: ServiceSelectedEvent) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ServiceDetailFragment.newInstance())
            .addToBackStack(ServiceDetailFragment::class.java.name)
            .commit()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDisplayPasswordClickedEvent(event: DisplayPasswordClickedEvent) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, DisplayPasswordFragment.newInstance())
            .addToBackStack(DisplayPasswordFragment::class.java.name)
            .commit()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEditServiceClickedEvent(event: EditServiceClickedEvent) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, EditServiceFragment.newInstance())
            .addToBackStack(EditServiceFragment::class.java.name)
            .commit()
    }

    /**
     * @see ServiceDetailFragment.onWindowFocusChanged what this is needed for.
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        EventBus.getDefault().post(WindowFocusChangedEvent())
    }

}
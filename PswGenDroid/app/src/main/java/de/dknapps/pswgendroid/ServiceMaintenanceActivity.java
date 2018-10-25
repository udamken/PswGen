/*******************************************************************************
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
 *******************************************************************************/
package de.dknapps.pswgendroid;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

/**
 * <p>
 * Diese Activity stellt in Fragments die Liste der Dienste {@link ServiceListFragment} oder die Detailansicht {@link ServiceDetailFragment} dar.
 * </p>
 */
public class ServiceMaintenanceActivity extends FragmentActivity implements PassphraseDialog.Listener {

    /**
     * Das eingebettete Fragment für die Anzeige der Diensteliste
     */
    private ServiceListFragment serviceListFragment = new ServiceListFragment();

    /**
     * Das eingebettete Fragment für die Detailanzeige eines Dienstes
     */
    private ServiceDetailFragment serviceDetailFragment = new ServiceDetailFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_maintenance);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.service_maintencance, serviceListFragment)
                .commit();
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ServiceMaintenanceViewModel model = ViewModelProviders.of(this).get(ServiceMaintenanceViewModel.class);
        model.getCurrentServiceAbbreviation().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String serviceAbbreviation) {
                onChangedCurrentServiceAbbreviation();
            }
        });
    }

    private void onChangedCurrentServiceAbbreviation() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.service_maintencance, serviceDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(serviceListFragment);
        return true;
    }

    @Override
    public boolean onNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (serviceDetailFragment != null) {
            serviceDetailFragment.onWindowFocusChanged(hasFocus);
        }
    }

    /**
     * Eingehende On-Click-Events aus dem PassphraseDialog an das {@link ServiceListFragment} übergeben.
     */

    @Override
    public void onClickPassphraseDialogButtonPositive() {
        serviceListFragment.onClickPassphraseDialogButtonPositive();
    }

    @Override
    public void onClickPassphraseDialogButtonNegative() {
        serviceListFragment.onClickPassphraseDialogButtonNegative();
    }

}

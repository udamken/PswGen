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

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import de.dknapps.pswgendroid.util.ObjectObserver;
import de.dknapps.pswgendroid.util.ObservableObject;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.Observable;
import java.util.Observer;

/**
 * <p>
 * Diese Activity stellt in Fragments die Liste der Dienste {@link ServiceListFragment} oder die Detailansicht {@link ServiceDetailFragment} dar.
 * </p>
 */
public class ServiceMaintenanceActivity extends FragmentActivity implements PassphraseDialog.Listener {

    ServiceMaintenanceViewModel viewModel = null;

    /**
     * Das eingebettete Fragment für die Anzeige der Diensteliste
     */
    private ServiceListFragment serviceListFragment = new ServiceListFragment();

    /**
     * Das eingebettete Fragment für die Detailansicht eines Dienstes
     */
    private ServiceDetailFragment serviceDetailFragment = new ServiceDetailFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_maintenance);
        ServiceMaintenanceViewModel viewModel = ViewModelProviders.of(this)
                .get(ServiceMaintenanceViewModel.class);
        if (getSupportFragmentManager().getFragments().isEmpty()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.service_maintencance, serviceListFragment)
                    .commitAllowingStateLoss();
        }

        // Wenn über ServiceListFragment.onListItemClick() ein Dienstekürzel ausgewählt wurde, wird in die Detailansicht gewechselt.
        viewModel.getCurrentServiceAbbreviation().addObjectObserver(new ObjectObserver<String>() {

            @Override
            public void onChange(ObservableObject<String> observableObject, String newValue) {
                onChangedCurrentServiceAbbreviation();
            }

        });
    }

    private void onChangedCurrentServiceAbbreviation() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.service_maintencance, serviceDetailFragment).addToBackStack(null)
                .commitAllowingStateLoss();
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

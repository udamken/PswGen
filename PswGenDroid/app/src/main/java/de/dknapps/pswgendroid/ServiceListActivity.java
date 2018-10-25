/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2016 Uwe Damken
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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

/**
 * <p>
 * Diese Activity stellt in Fragments die Liste der Dienste {@link ServiceListFragment} oder die Detailansicht {@link ServiceDetailFragment} dar.
 * </p>
 * <p>
 * Diese Activity implementiert {@link ServiceListFragment.Listener}, um die Auswahl von Einträgen mitgeteilt
 * zu bekommen.
 * </p>
 */
public class ServiceListActivity extends Activity implements ServiceListFragment.Listener, PassphraseDialog.Listener {

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
        setContentView(R.layout.activity_service_list);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.service_list, serviceListFragment)
                .commit();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Callback method from {@link ServiceListFragment.Listener} indicating that the item with the given ID
     * was selected.
     */
    @Override
    public void onItemSelected(String id) {
        Bundle arguments = new Bundle();
        arguments.putString(ServiceDetailFragment.ARG_ITEM_ID, id);
        serviceDetailFragment = new ServiceDetailFragment();
        serviceDetailFragment.setArguments(arguments);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.service_list, serviceDetailFragment)
                .addToBackStack(null)
                .commit();
        // Up-Button im Action Bar anzeigen
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
        getFragmentManager().popBackStack();
        return true;
    }

    /**
     * Eingehende On-Click-Events an das {@link ServiceDetailFragment} übergeben.
     */

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (serviceDetailFragment != null) {
            serviceDetailFragment.onWindowFocusChanged(hasFocus);
        }
    }

    public void onClickButtonOpenAndProvide(final View buttonOpenAndProvide) {
        serviceDetailFragment.onClickButtonOpenAndProvide(this, buttonOpenAndProvide);
    }

    public void onClickButtonProvide(final View buttonProvide) {
        serviceDetailFragment.onClickButtonProvide(this, buttonProvide);
    }

    public void onClickButtonOpenUrl(final View buttonOpenUrl) {
        serviceDetailFragment.onClickButtonOpenUrl(this, buttonOpenUrl);
    }

    public void onClickButtonCopyLoginInfo(final View buttonOpenUrl) {
        serviceDetailFragment.onClickButtonCopyLoginInfo(this, buttonOpenUrl);
    }

    public void onClickButtonCopyPassword(final View buttonOpenUrl) {
        serviceDetailFragment.onClickButtonCopyPassword(this, buttonOpenUrl);
    }

    public void onClickButtonDisplayPassword(final View buttonOpenUrl) {
        serviceDetailFragment.onClickButtonDisplayPassword(this, buttonOpenUrl);
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

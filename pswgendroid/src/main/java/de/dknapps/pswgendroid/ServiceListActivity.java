/*
 * #%L
 * PswGen
 * %%
 * Copyright (C) 2005 - 2016 Uwe Damken
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.dknapps.pswgendroid;

import android.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

/**
 * <p>
 * Diese Activity stellt die Liste der Dienste dar. Auf Geräten mit größeren Bildschirmen wird die Liste neben
 * den Details eines Dienstes angezeigt. Bei kleinen Bildschirmen wird bei Auswahl eines Dienstes zur
 * Detailanzeige zur {@link ServiceDetailActivity} verzweigt.
 * </p>
 * <p>
 * Die Liste der Einträge wird letztlich im {@link ServiceListFragment} angzeigt, eingebunden in diese
 * Activity, und die Dienstdetails im {@link ServiceDetailFragment}, welches entweder in dieser Activity oder
 * in {@link ServiceDetailActivity} eingebunden ist.
 * </p>
 * <p>
 * Diese Activity implementiert {@link ServiceListFragment.Listener}, um die Auswahl von Einträgen mitgeteilt
 * zu bekommen.
 * </p>
 */
public class ServiceListActivity extends FragmentActivity implements ServiceListFragment.Listener,
		PassphraseDialog.Listener {

	/** Gibt an, ob Liste und Details gleichzeitig angezeigt werden (bei großen Bildschirmen) */
	private boolean inTwoPaneMode;

	/** Das eingebettete Fragment für die Anzeige der Diensteliste */
	private ServiceListFragment serviceListFragment;

	/** Das eingebettete Fragment für die Detailanzeige eines Dienstes */
	private ServiceDetailFragment serviceDetailFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_list);
		serviceListFragment = (ServiceListFragment) getSupportFragmentManager().findFragmentById(
				R.id.service_list);
		if (findViewById(R.id.service_detail_container) != null) {
			// Den Detail-Containter gibt es nur bei großen Bildschirmen (res/values-large,
			// res/values-sw600dp), dann werden Liste und Details gleichzeitig angezeigt.
			inTwoPaneMode = true;
			// Bei gleichzeitiger Liste mit Details 'activate' auf den Listeneinträgen setzen.
			serviceListFragment.setActivateOnItemClick(true);
		}
	}

	/**
	 * Callback method from {@link ServiceListFragment.Listener} indicating that the item with the given ID
	 * was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (inTwoPaneMode) {
			// Bei gleichzeitiger Anzeige von Liste und Details die Details über eine
			// Fragment-Manager-Transaktion einblenden.
			Bundle arguments = new Bundle();
			arguments.putString(ServiceDetailFragment.ARG_ITEM_ID, id);
			serviceDetailFragment = new ServiceDetailFragment();
			serviceDetailFragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.service_detail_container, serviceDetailFragment).commit();

		} else {
			// Nur entweder Liste oder Details? Dann einfach die Detail-Activity starten.
			Intent detailIntent = new Intent(this, ServiceDetailActivity.class);
			detailIntent.putExtra(ServiceDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
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

	/**
	 * Eingehende On-Click-Events an das {@link ServiceDetailFragment} übergeben.
	 */

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
		if (areTwoPanesActive()) {
			serviceDetailFragment.onClickPassphraseDialogButtonPositive();
		}
	}

	/**
	 * Liefert true, wenn sich die Anwendung im Two-Pane-Modus befindet *und* tatsächlich ein Dienst zur
	 * Anzeige in den Details ausgewählt wurde.
	 */
	public boolean areTwoPanesActive() {
		return inTwoPaneMode && serviceDetailFragment != null
				&& serviceDetailFragment.hasCurrentServiceInfo();
	}

	@Override
	public void onClickPassphraseDialogButtonNegative() {
		serviceListFragment.onClickPassphraseDialogButtonNegative();
	}

}

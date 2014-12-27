package net.sf.pswgendroid;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2014 Uwe Damken

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/

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
 * Diese Activity implementiert {@link ServiceListFragment.Callbacks}, um die Auswahl von Einträgen mitgeteilt
 * zu bekommen.
 * </p>
 * <p>
 * Copyright (C) 2014 Uwe Damken
 * </p>
 */
public class ServiceListActivity extends FragmentActivity implements ServiceListFragment.Callbacks {

	/** Gibt an, ob Liste und Details gleichzeitig angezeigt werden (bei großen Bildschirmen) */
	private boolean inTwoPaneMode;

	/** The embedded fragment to handle the service list */
	private ServiceListFragment serviceListFragment;

	/** The embedded fragment to handle service details */
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
	 * Callback method from {@link ServiceListFragment.Callbacks} indicating that the item with the given ID
	 * was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (inTwoPaneMode) {
			// Bei gleichzeitiger Anzeige von Liste und Details die Dateils über eine
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

}

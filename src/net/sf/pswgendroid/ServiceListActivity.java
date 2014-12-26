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
 * An activity representing a list of Services. This activity has different presentations for handset and
 * tablet-size devices. On handsets, the activity presents a list of items, which when touched, lead to a
 * {@link ServiceDetailActivity} representing item details. On tablets, the activity presents the list of
 * items and item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a {@link ServiceListFragment} and the item
 * details (if present) is a {@link ServiceDetailFragment}.
 * <p>
 * This activity also implements the required {@link ServiceListFragment.Callbacks} interface to listen for
 * item selections.
 */
public class ServiceListActivity extends FragmentActivity implements ServiceListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
	 */
	private boolean mTwoPane;

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
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			serviceListFragment.setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link ServiceListFragment.Callbacks} indicating that the item with the given ID
	 * was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(ServiceDetailFragment.ARG_ITEM_ID, id);
			serviceDetailFragment = new ServiceDetailFragment();
			serviceDetailFragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.service_detail_container, serviceDetailFragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
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
	 * Delegate incoming onClick-Calls to the corresponding service detail fragment.
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

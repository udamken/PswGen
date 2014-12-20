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
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;

/**
 * An activity representing a single Service detail screen. This activity is only used on handset devices. On
 * tablet-size devices, item details are presented side-by-side with a list of items in a
 * {@link ServiceListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than a
 * {@link ServiceDetailFragment}.
 */
public class ServiceDetailActivity extends FragmentActivity {

	/**
	 * The embedded fragment to handle service details.
	 */
	private ServiceDetailFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			String id = getIntent().getStringExtra(ServiceDetailFragment.ARG_ITEM_ID);
			arguments.putString(ServiceDetailFragment.ARG_ITEM_ID, id);
			fragment = new ServiceDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.service_detail_container, fragment)
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this, new Intent(this, ServiceListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Delegate incoming onClick-Calls to the corresponding service detail fragment.
	 */

	public void onClickButtonOpenUrl(final View buttonOpenUrl) {
		fragment.onClickButtonOpenUrl(this, buttonOpenUrl);
	}

	public void onClickButtonCopyLoginInfo(final View buttonOpenUrl) {
		fragment.onClickButtonCopyLoginInfo(this, buttonOpenUrl);
	}

	public void onClickButtonCopyPassword(final View buttonOpenUrl) {
		fragment.onClickButtonCopyPassword(this, buttonOpenUrl);
	}

	public void onClickButtonDisplayPassword(final View buttonOpenUrl) {
		fragment.onClickButtonDisplayPassword(this, buttonOpenUrl);
	}

}

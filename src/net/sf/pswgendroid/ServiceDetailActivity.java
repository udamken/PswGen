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
 * <p>
 * Diese Activity stellt die Detailanzeige für Dienste dar, sie wird nur für Geräte mit kleineren Bildschirmen
 * verwendet. Auf Geräten mit größerem Bildschirm erledigt dies die {@link ServiceListActivity}, wobei dann
 * Diensteliste und Details eines Dienstes nebeneinander angezeigt werden.
 * </p>
 * <p>
 * Diese Activity ist vor allem ein Halter, eine Schale, für das {@link ServiceDetailFragment}.
 * </p>
 * <p>
 * Copyright (C) 2014-2015 Uwe Damken
 * </p>
 */
public class ServiceDetailActivity extends FragmentActivity implements PassphraseDialog.Listener {

	/** Das eingebettete Fragment für die Detailanzeige eines Dienstes */
	private ServiceDetailFragment serviceDetailFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_detail);

		// Up-Button im Action Bar anzeigen
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Nur wenn der Fragment-Status von der vorherigen Aktivity-Konfiguration vermerkt wurde, z.B. beim
		// Drehen des Bildschirm, ist savedInstanceState non-null. Dann wird das Fragment automatisch zu
		// seinem Container hinzugefügt. Sonst müssen wir es manuell tun. Nähere Informationen dazu siehe
		// http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null) {
			// ServiceDetailFragment erzeugen und per Transaktion des FragmentManagers der Activity zuordnen
			Bundle arguments = new Bundle();
			String id = getIntent().getStringExtra(ServiceDetailFragment.ARG_ITEM_ID);
			arguments.putString(ServiceDetailFragment.ARG_ITEM_ID, id);
			serviceDetailFragment = new ServiceDetailFragment();
			serviceDetailFragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.service_detail_container, serviceDetailFragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// Dies ID repräsentiert den Home- oder Up-Button. Bei dieser Activity wird der Up-Button gezeigt.
			// Mit NavUtils kann der Anwender in der Anwendungsstruktur eine Ebene nach oben navigieren. Mehr
			// Details zum Navigation Pattern unter Android finden sich hier:
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			NavUtils.navigateUpTo(this, new Intent(this, ServiceListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
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

	@Override
	public void onClickPassphraseDialogButtonPositive() {
		serviceDetailFragment.onClickPassphraseDialogButtonPositive();
	}

	@Override
	public void onClickPassphraseDialogButtonNegative() {
		serviceDetailFragment.onClickPassphraseDialogButtonNegative();
	}

}

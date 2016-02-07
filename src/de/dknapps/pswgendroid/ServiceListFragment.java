/*******************************************************************************
 * PswGen - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGen on your mobile  
 *
 *     Copyright (C) 2005, 2016 Uwe Damken
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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;
import de.dknapps.pswgen.model.ServiceInfo;

public class ServiceListFragment extends ListFragment implements OnQueryTextListener {

	/** Bei Geräten mit großem Bildschirm der Key für die Serialisierung der Position des aktiven Eintrags */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/** Das aktuelle Listener-Objekt, dass über Änderungen in der Eintragsselektion informiert wird */
	private Listener listener = DUMMY_LISTENER;

	/** Bei Geräten mit großem Bildschirm die Position des zurzeit selektierten Eintrags */
	private int activatedPosition = ListView.INVALID_POSITION;

	/** Filterbarer Adapter zur Liste der Dienste */
	private ArrayAdapter<ServiceInfo> arrayAdapter;

	/**
	 * Alle Activity-Klassen, die dieses Fragment verwenden, müssen dieses Interface implementieren, damit sie
	 * über Änderungen in der Eintragsselektion informiert werden.
	 */
	public interface Listener {

		/**
		 * Es wurde ein Eintrag ausgewählt.
		 */
		public void onItemSelected(String id);

	}

	/**
	 * Eine Dummy-Implementation von {@link Listener}, die nichts tut und nur verwendet wird, wenn dieses
	 * Fragment nicht mit einer Activity verbunden ist, wann auch immer das der Fall sein kann.
	 */
	private static final Listener DUMMY_LISTENER = new Listener() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Obligatorischer Default-Konstruktor für den FragmentManager zum Instantiieren des Fragments, z.B. beim
	 * Umdrehen des Bildschirms.
	 */
	public ServiceListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUpListAdapter(PswGenAdapter.getServicesAsList());
	}

	@Override
	public void onResume() {
		if (!PswGenAdapter.isServiceInfoListLoaded()) { // Zwischendurch SCREEN_OFF gewesen?
			setUpListAdapter(new ArrayList<ServiceInfo>()); // die Liste darf nicht mehr gezeigt werden
			DialogFragment passphraseDialog = new PassphraseDialog();
			passphraseDialog.show(getActivity().getFragmentManager(), "passphrase_dialog");
		}
		super.onResume();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Ggf. die vermerkte (ehemals) aktive Position reaktivieren
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activity-Klassen, die dieses Fragment nutzen, müssen {@link Listener} implementieren
		if (!(activity instanceof Listener)) {
			throw new ClassCastException(
					activity.getClass().getName() + " must implement " + Listener.class.getName());
		}

		listener = (Listener) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Zurück zur Dummy-Implementierung, es gibt keine zuständige Activity mehr
		listener = DUMMY_LISTENER;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Die Activity-Klasse, die das Fragment verwendet, über die Selektion informieren
		listener.onItemSelected(arrayAdapter.getItem(position).getServiceAbbreviation());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (hasActivatedPosition()) {
			// Aktivierte Position vermerken
			outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
		}
	}

	/**
	 * Liefert true, wenn ein Dienst ausgewählt wurde.
	 */
	public boolean hasActivatedPosition() {
		return activatedPosition != ListView.INVALID_POSITION;
	}

	/**
	 * Aktiviert den Activate-On-Click-Modus, ausgewählte Einträge bekommen dann den Status 'activated'.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// Im Modus CHOICE_MODE_SINGLE werden Einträge als 'activated' markiert
		getListView()
				.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (hasActivatedPosition()) { // vorher schon ein Dienst ausgewählt gewesen?
			getListView().setItemChecked(position, true);
		} else {
			getListView().setItemChecked(activatedPosition, false);
		}
		activatedPosition = position;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		arrayAdapter.getFilter().filter(newText);
		return true; // die Aktion wurde behandelt, es ist nichts mehr zu tun
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false; // die Aktion wurde nicht behandelt, es ist der Defaultmechnismus aufzurufen
	}

	/**
	 * Liste (erneut) anzeigen.
	 */
	public void onClickPassphraseDialogButtonPositive() {
		setUpListAdapter(PswGenAdapter.getServicesAsList());
	}

	/**
	 * Die Passphrase hätte erneut eingegeben werden müssen, das ist aber nicht erfolgt, darum gehts zurück
	 * zum Anfang, zur StartupActivity.
	 */
	public void onClickPassphraseDialogButtonNegative() {
		Intent startupIntent = new Intent(getActivity(), StartupActivity.class);
		startActivity(startupIntent);
	}

	/**
	 * Erstellt den filterbaren Adapter zur Liste der Dienste.
	 */
	private void setUpListAdapter(List<ServiceInfo> serviceInfoList) {
		arrayAdapter = new ArrayAdapter<ServiceInfo>(getActivity(),
				android.R.layout.simple_list_item_activated_1, android.R.id.text1, serviceInfoList);
		setListAdapter(arrayAdapter);
	}

}

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

import net.sf.pswgen.model.ServiceInfo;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;

/**
 * <p>
 * Dieses Fragment stellt die Diensteliste dar. Auf Geräten mit großen Bildschirmen bekommt der selektierte
 * Eintrag den Status 'activated'. Dadurch wird erkennbar, welcher Eintrag in {@link ServiceDetailFragment}
 * angezeigt wird..
 * </p>
 * <p>
 * Activity-Klassen, die dieses Fragment nutzen, müssen {@link Callbacks} implementieren.
 * </p>
 * <p>
 * Copyright (C) 2014 Uwe Damken
 * </p>
 */
public class ServiceListFragment extends ListFragment implements OnQueryTextListener {

	/** Bei Geräten mit großem Bildschirm der Key für die Serialisierung der Position des aktiven Eintrags */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/** Das aktuelle Callbacks-Objekt, dass über Änderungen in der Eintragsselektion informiert wird */
	private Callbacks callbacks = DUMMY_CALLBACKS;

	/** Bei Geräten mit großem Bildschirm die Position des zurzeit selektierten Eintrags */
	private int activatedPosition = ListView.INVALID_POSITION;

	/** Filterbarer Adapter zur Liste der Dienste */
	private ArrayAdapter<ServiceInfo> arrayAdapter;

	/**
	 * Alle Activity-Klassen, die dieses Fragment verwenden, müssen dieses Interface implementieren, damit sie
	 * über Änderungen in der Eintragsselektion informiert werden.
	 */
	public interface Callbacks {
		/**
		 * Callback für den Fall, dass ein Eintrag ausgewählt wurde.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * Eine Dummy-Implementation von {@link Callbacks}, die nichts tut und nur verwendet wird, wenn dieses
	 * Fragment nicht mit einer Activity verbunden ist, wann auch immer das der Fall sein kann.
	 */
	private static final Callbacks DUMMY_CALLBACKS = new Callbacks() {
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

		arrayAdapter = new ArrayAdapter<ServiceInfo>(getActivity(),
				android.R.layout.simple_list_item_activated_1, android.R.id.text1,
				PswGenAdapter.getServicesAsList());
		setListAdapter(arrayAdapter);
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

		// Activity-Klassen, die dieses Fragment nutzen, müssen {@link Callbacks} implementieren
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		callbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Zurück zur Dummy-Implementierung, es gibt keine zuständige Activity mehr
		callbacks = DUMMY_CALLBACKS;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Die Activity-Klasse, die das Fragment verwendet, über die Selektion informieren
		callbacks.onItemSelected(arrayAdapter.getItem(position).getServiceAbbreviation());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (activatedPosition != ListView.INVALID_POSITION) {
			// Aktivierte Position vermerken
			outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
		}
	}

	/**
	 * Aktiviert den Activate-On-Click-Modus, ausgewählte Einträge bekommen dann den Status 'activated'.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// Im Modus CHOICE_MODE_SINGLE werden Einträge als 'activated' markiert
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(activatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
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

}

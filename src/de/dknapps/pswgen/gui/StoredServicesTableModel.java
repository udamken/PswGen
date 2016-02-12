/*******************************************************************************
 * PswGen - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGen on your mobile  
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
package de.dknapps.pswgen.gui;

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import de.dknapps.pswgen.model.ServiceInfo;

/**
 * <p>
 * Table Model für die Tabelle der gespeicherten Dienste
 * </p>
 */
public class StoredServicesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -8594091408631595480L;

	public static final int COL_SERVICE_ABBREVIATION = 0;

	public static final int COL_ADDITIONAL_INFO = 1;

	public static final int COL_LOGIN_URL = 2;

	private String[] columnNames;

	ServiceInfo[] services;

	public StoredServicesTableModel(Collection<ServiceInfo> services, String[] columnNames) {
		super();
		this.columnNames = columnNames;
		setData(services);
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return services.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {
		if (c == COL_SERVICE_ABBREVIATION) {
			return String.class;
		} else if (c == COL_ADDITIONAL_INFO) {
			return String.class;
		} else if (c == COL_LOGIN_URL) {
			return String.class;
		}
		return String.class;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public ServiceInfo getServiceInfoAt(int rowIndex) {
		return services[rowIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ServiceInfo si = getServiceInfoAt(rowIndex);
		if (columnIndex == COL_SERVICE_ABBREVIATION) {
			return si.getServiceAbbreviation();
		} else if (columnIndex == COL_ADDITIONAL_INFO) {
			return si.getAdditionalInfo();
		} else if (columnIndex == COL_LOGIN_URL) {
			return si.getLoginUrl();
		}
		return null;
	}

	public void setData(Collection<ServiceInfo> services) {
		this.services = services.toArray(new ServiceInfo[0]);
		fireTableDataChanged();
	}

}

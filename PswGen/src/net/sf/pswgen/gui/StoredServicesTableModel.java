package net.sf.pswgen.gui;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them

 Copyright (C) 2005-2012  Uwe Damken

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *****************************************************************************/

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import net.sf.pswgen.model.ServiceInfo;

/**
 * <p>
 * Table Model für die Tabelle der gespeicherten Dienste
 * </p>
 * <p>
 * (c) 2007-2012, by Uwe Damken
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

package net.sf.pswgen.gui;

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

	public static final int COL_SERVICE_ABBREVIATION = 0;

	public static final int COL_ADDITIONAL_INFO = 1;

	public static final int COL_LOGIN_URL = 2;

	private static final long serialVersionUID = -8594091408631595480L;

	String[] columnNames = { "Dienstekürzel", "Zusatzinfos", "Internetseite" };

	ServiceInfo[] services;

	public StoredServicesTableModel(Collection<ServiceInfo> services) {
		super();
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

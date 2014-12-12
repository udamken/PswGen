package net.sf.pswgen.gui.base;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them

 Copyright (C) 2005-2013 Uwe Damken

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

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collections;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import net.sf.pswgen.util.Constants;
import net.sf.pswgen.util.ConverterHelper;

/**
 * <p>
 * Erzeugt Widgets, also GUI-Elemente, auf vereinfachte und für von mir geschriebene Anwendungen
 * standardisierte Weise.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class WidgetFactory {

	private static final String PREFS_SUBKEY_HEIGHT = ".height";

	private static final String PREFS_SUBKEY_WIDTH = ".width";

	/** Der Logger dieser Anwendung */
	private static final Logger LOGGER = Logger.getLogger(Constants.APPLICATION_PACKAGE_NAME + ".Logger");

	/** Die eine und einzige Instanz dieser Klasse */
	private static WidgetFactory instance = null;

	/** Hashtable mit Informationen zu allen Widgets, die hier erzeugt werden können */
	private Hashtable<String, WidgetInfo> widgetInfos = new Hashtable<String, WidgetInfo>();

	/** Benutzereinstellungen, die gegebenenfalls die Einstellungen aus den Properties überschreiben */
	private Preferences prefs = null;

	/** Konstruktor ist nicht öffentlich zugreifbar => getInstance() nutzen */
	private WidgetFactory() {
		super();
	}

	/**
	 * Liefert die eine und einzige Instanz dieser Klasse.
	 */
	public static synchronized WidgetFactory getInstance() {
		if (instance == null) { // Noch nicht instantiiert und initialisiert?
			instance = new WidgetFactory();
			instance.initialize();
		}
		return instance;
	}

	/**
	 * Initialisiert die eine und einzige Instanz.
	 */
	private void initialize() {
		prefs = Preferences.userRoot().node(Constants.APPLICATION_PACKAGE_NAME);
		ResourceBundle bundle = ResourceBundle.getBundle(Constants.APPLICATION_PACKAGE_NAME + ".Widgets");
		for (String key : Collections.list(bundle.getKeys())) {
			String value = bundle.getString(key);
			Matcher matcher = Pattern.compile("\\s*([^,]*)(\\s*,\\s*(\\d+),\\s*(\\d+))?\\s*").matcher(value);
			if (!matcher.matches()) {
				LOGGER.log(Level.WARNING, Constants.MSG_INVALID_WIDGET_INFO, new Object[] { key, value });
			} else {
				String text = matcher.group(1);
				String preferredWidth = matcher.group(3);
				String preferredHeight = matcher.group(4);
				text = (text.length() == 0) ? null : text;
				if (preferredWidth == null || preferredHeight == null) {
					widgetInfos.put(key, new WidgetInfo(text));
				} else {
					// Wenn Breite und Höhe in den Preferences stehen, gelten die vorangig
					int width = prefs.getInt(key + PREFS_SUBKEY_WIDTH, ConverterHelper.toInt(preferredWidth));
					int height = prefs.getInt(key + PREFS_SUBKEY_HEIGHT,
							ConverterHelper.toInt(preferredHeight));
					widgetInfos.put(key, new WidgetInfo(text, width, height));
				}
			}
		}
	}

	/**
	 * Liefert einen GUI-Text.
	 */
	public String getGuiText(String name) {
		WidgetInfo wi = getWidgetInfo(name);
		return (wi == null) ? name : wi.getText();
	}

	/**
	 * Liefert Informationen zu dem Widget mit dem Namen name oder einen Dummy-Eintrag, wenn der Name nicht
	 * bekannt ist.
	 */
	private WidgetInfo getWidgetInfo(final String name) {
		WidgetInfo wi = widgetInfos.get(name);
		if (wi == null) {
			wi = new WidgetInfo("<" + name + ">");
			LOGGER.log(Level.WARNING, Constants.MSG_NO_WIDGET_INFO, name);
		}
		return wi;
	}

	/**
	 * Liefert einen Button mit einem damit verbundenen ActionListener der für den Event ActionPerformed im
	 * Controller pre eine Methode name für die auslösende View view aufruft.
	 */
	public JButton getButton(final String widgetName) {
		WidgetInfo wi = getWidgetInfo(widgetName);
		JButton button = new JButton();
		button.setText(wi.getText());
		if (wi.getPreferredSize() != null) {
			button.setPreferredSize(wi.getPreferredSize());
		}
		return button;
	}

	/**
	 * Liefert ein Label.
	 */
	public JLabel getLabel(final String widgetName) {
		WidgetInfo wi = getWidgetInfo(widgetName);
		JLabel label = new JLabel();
		label.setText(wi.getText());
		if (wi.getPreferredSize() != null) {
			label.setPreferredSize(wi.getPreferredSize());
		}
		return label;
	}

	/**
	 * Liefert eine CheckBox.
	 */
	public JCheckBox getCheckBox(final String widgetName) {
		WidgetInfo wi = getWidgetInfo(widgetName);
		JCheckBox checkbox = new JCheckBox();
		checkbox.setText(wi.getText());
		if (wi.getPreferredSize() != null) {
			checkbox.setPreferredSize(wi.getPreferredSize());
		}
		return checkbox;
	}

	/**
	 * Liefert eine Tabelle.
	 */
	public JTable getTable(final String widgetName, AbstractTableModel model) {
		WidgetInfo wi = getWidgetInfo(widgetName);
		JTable table = new JTable(model);
		if (wi.getPreferredSize() != null) {
			table.setPreferredScrollableViewportSize(wi.getPreferredSize());
		}
		table.setFillsViewportHeight(true);
		return table;
	}

	/**
	 * Liefert ein Panel, das als ContentPane gestaltet ist und sich Größenveränderungen *nicht* in den
	 * User-Preference merkt.
	 */
	public JPanel getContentPane(final String widgetName) {
		return getContentPane(widgetName, false);
	}

	/**
	 * Liefert ein Panel, das als ContentPane gestaltet ist.
	 */
	public JPanel getContentPane(final String widgetName, final boolean saveSizeOnResize) {
		final WidgetInfo wi = getWidgetInfo(widgetName);
		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		if (wi.getPreferredSize() != null) {
			panel.setPreferredSize(wi.getPreferredSize());
		}
		if (saveSizeOnResize) {
			panel.addComponentListener(new ComponentAdapter() {

				@Override
				public void componentResized(ComponentEvent e) {
					if (e.getID() == ComponentEvent.COMPONENT_RESIZED) {
						prefs.putInt(widgetName + PREFS_SUBKEY_WIDTH, panel.getWidth());
						prefs.putInt(widgetName + PREFS_SUBKEY_HEIGHT, panel.getHeight());
					}
				}

			});
		}
		return panel;
	}

	/**
	 * Liefert ein Panel.
	 */
	public JPanel getPanel(final String widgetName) {
		WidgetInfo wi = getWidgetInfo(widgetName);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.RAISED), wi.getText(),
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		if (wi.getPreferredSize() != null) {
			panel.setPreferredSize(wi.getPreferredSize());
		}
		return panel;
	}

	/**
	 * Liefert ein FormattedTextField.
	 */
	public DbcIntegerField getIntegerField(final String widgetName) {
		WidgetInfo wi = getWidgetInfo(widgetName);
		DbcIntegerField field = new DbcIntegerField();
		if (wi.getPreferredSize() != null) {
			field.setPreferredSize(wi.getPreferredSize());
		}
		return field;
	}

	/**
	 * Liefert ein PasswordField.
	 */
	public JPasswordField getPasswordField(final String widgetName) {
		WidgetInfo wi = getWidgetInfo(widgetName);
		JPasswordField field = new JPasswordField();
		if (wi.getPreferredSize() != null) {
			field.setPreferredSize(wi.getPreferredSize());
		}
		return field;
	}

	/**
	 * Liefert ein TextField.
	 */
	public JTextField getTextField(final String widgetName) {
		WidgetInfo wi = getWidgetInfo(widgetName);
		JTextField field = new JTextField();
		if (wi.getPreferredSize() != null) {
			field.setPreferredSize(wi.getPreferredSize());
		}
		return field;
	}

	/**
	 * Liefert ein TextArea.
	 */
	public JTextArea getTextArea(final String widgetName) {
		WidgetInfo wi = getWidgetInfo(widgetName);
		JTextArea field = new JTextArea();
		field.setFont((new JTextField()).getFont()); // Font von anderen Typen übernehmen
		if (wi.getPreferredSize() != null) {
			field.setPreferredSize(wi.getPreferredSize());
		}
		return field;
	}

	/**
	 * Liefert ein ScrollPane.
	 */
	public JScrollPane getScrollPane(final String widgetName, final Component component) {
		WidgetInfo wi = getWidgetInfo(widgetName);
		JScrollPane pane = new JScrollPane(component);
		if (wi.getPreferredSize() != null) {
			pane.setPreferredSize(wi.getPreferredSize());
		}
		return pane;
	}

	/**
	 * Enthält Informationen für ein Widget, also ein GUI-Element.
	 * <p>
	 * Copyright (C) 2005-2013 Uwe Damken
	 */
	private class WidgetInfo {

		/** Der für das Widget anzuzeigende Text */
		private String text;

		/** Die bevorzugte Größe des Widgets */
		private Dimension preferredSize;

		/**
		 * Konstruktiert ein Widget mit einem Text.
		 */
		public WidgetInfo(final String text) {
			this.text = text;
		}

		/**
		 * Konstruktiert ein Widget mit allen Informationen.
		 */
		public WidgetInfo(final String text, final int preferredWidth, final int preferredHeight) {
			this.text = text;
			preferredSize = new Dimension(preferredWidth, preferredHeight);
		}

		/**
		 * @return Returns the text.
		 */
		public String getText() {
			return text;
		}

		/**
		 * @return Returns the preferredWidth.
		 */
		public Dimension getPreferredSize() {
			return preferredSize;
		}

	}

}
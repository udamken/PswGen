/*******************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile  
 *
 *     Copyright (C) 2005-2018 Uwe Damken
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
package de.dknapps.pswgendesktop.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import de.dknapps.pswgencore.CoreConstants;
import de.dknapps.pswgencore.model.ServiceInfo;
import de.dknapps.pswgencore.util.EmptyHelper;
import de.dknapps.pswgendesktop.gui.base.BaseCtl;
import de.dknapps.pswgendesktop.gui.base.BaseView;
import de.dknapps.pswgendesktop.gui.base.DbcIntegerField;
import de.dknapps.pswgendesktop.gui.base.GridBagConstraintsFactory;
import de.dknapps.pswgendesktop.gui.base.WidgetFactory;

/**
 * <p>
 * Dies ist die View des Hauptfensters von PswGenDesktop.
 * </p>
 */
public class MainView extends BaseView {

	/** Präfix für den Fenstertitel, wenn Werte verändert wurden */
	private static final String DIRTY_TAG_TITLE_PREFIX = "*";

	/** Version für die Serialisierung */
	private static final long serialVersionUID = -3013326579046715523L;

	/** Farbe für Label-Elemente, deren Felder das generierte Passwort beeinflussen */
	private static final Color COLOR_INFLUENCE = Color.BLUE;

	/** Farbe für Label-Elemente, die eine Warnung anzeigen */
	private static final Color COLOR_WARNING = Color.RED;

	private StoredServicesTableModel tableModelStoredServices;

	private JTextField serviceAbbreviationFilter;

	private JCheckBox serviceAbbreviationFilterCaseSensitive;

	private JCheckBox serviceAbbreviationFilterAsRegex;

	private JLabel regexContainsErrors;

	private JTable tableStoredServices;

	private TableRowSorter<StoredServicesTableModel> tableRowSorter;

	private JTextField serviceAbbreviation;

	private JTextField additionalInfo;

	private JTextField loginUrl;

	private JTextField loginInfo;

	private JTextArea additionalLoginInfo;

	private JCheckBox useSmallLetters;

	private JCheckBox useCapitalLetters;

	private JCheckBox useDigits;

	private JCheckBox useSpecialCharacters;

	private JTextField specialCharacters;

	private DbcIntegerField smallLettersCount;

	private DbcIntegerField smallLettersStartIndex;

	private DbcIntegerField smallLettersEndIndex;

	private DbcIntegerField capitalLettersCount;

	private DbcIntegerField capitalLettersStartIndex;

	private DbcIntegerField capitalLettersEndIndex;

	private DbcIntegerField digitsCount;

	private DbcIntegerField digitsStartIndex;

	private DbcIntegerField digitsEndIndex;

	private DbcIntegerField specialCharactersCount;

	private DbcIntegerField specialCharactersStartIndex;

	private DbcIntegerField specialCharactersEndIndex;

	private DbcIntegerField totalCharacterCount;

	private JPasswordField password;

	private JPasswordField passwordRepeated;

	private JCheckBox makePasswordVisible;

	private JLabel labelUseOldPassphrase;

	private JButton buttonStoreService;

	private JButton buttonUseNewPassphrase;

	private JLabel lastUpdate;

	/** Hey, it's me ... für die Listener */
	private MainView me = this;

	private JScrollPane scrollableTableStoredServices;

	/** Wurden Werte des aktuellen Dienstes verändert? */
	private boolean dirty;

	/**
	 * Konstruiert diese View mit einer Referenz auf den zugehörigen Controller.
	 */
	public MainView(BaseCtl ctl) {
		super(ctl);
	}

	/**
	 * This method initializes this
	 */
	@Override
	public void initialize() {
		super.initialize();
	}

	/**
	 * This method initializes mContentPane
	 */
	@Override
	public JPanel createContentPane() {
		// Fabriken holen
		GridBagConstraintsFactory gbcf = GridBagConstraintsFactory.getInstance();
		WidgetFactory wf = WidgetFactory.getInstance();
		// Widgets erzeugen
		JPanel panel = wf.getContentPane("MainFrame", true);
		panel.add(createPanelStoredServices(), gbcf.getTableConstraints(0, 1, 1, 1));
		panel.add(createPanelEditService(), gbcf.getTableConstraints(0, 2, 1, 1));
		return panel;
	}

	/**
	 * Panel zum "Gemerkte Dienste" anlegen
	 */
	private JPanel createPanelStoredServices() {
		// Fabriken holen
		GridBagConstraintsFactory gbcf = GridBagConstraintsFactory.getInstance();
		WidgetFactory wf = WidgetFactory.getInstance();
		// Widgets erzeugen
		JPanel panel = wf.getPanel("PanelStoredServices");
		serviceAbbreviationFilter = wf.getTextField("FieldServiceAbbreviation");
		serviceAbbreviationFilter.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				updateSearch();
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});
		serviceAbbreviationFilterCaseSensitive = wf
				.getCheckBox("CheckBoxServiceAbbreviationFilterCaseSensitive");
		serviceAbbreviationFilterCaseSensitive.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				updateSearch();
			}
		});
		serviceAbbreviationFilterAsRegex = wf.getCheckBox("CheckBoxServiceAbbreviationFilterAsRegex");
		serviceAbbreviationFilterAsRegex.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				updateSearch();
			}
		});
		regexContainsErrors = wf.getLabel("LabelRegexContainsErrors");
		regexContainsErrors.setForeground(Color.RED);
		tableModelStoredServices = new StoredServicesTableModel(((PswGenCtl) ctl).getServices().getServices(),
				new String[] { ctl.getGuiText("LabelServiceAbbreviation"),
						ctl.getGuiText("LabelAdditionalInfo"), ctl.getGuiText("LabelUseOldPassphrase"),
						ctl.getGuiText("LabelLoginUrl") });
		tableStoredServices = wf.getTable("TableStoredServices", tableModelStoredServices);
		tableStoredServices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableRowSorter = new TableRowSorter<StoredServicesTableModel>(tableModelStoredServices);
		tableRowSorter.setComparator(StoredServicesTableModel.COL_ADDITIONAL_INFO, new Comparator<String>() {

			@Override
			public int compare(String leftString, String rightString) {
				try {
					Date leftDate = CoreConstants.DATE_FORMAT_de_DE.parse(leftString);
					Date rightDate = CoreConstants.DATE_FORMAT_de_DE.parse(rightString);
					return leftDate.compareTo(rightDate);
				} catch (ParseException e) {
					return leftString.compareTo(rightString);
				}
			}

		});
		tableStoredServices.setRowSorter(tableRowSorter);
		// Ask to be notified of selection changes.
		ListSelectionModel rowSM = tableStoredServices.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting()) {// Ignore extra messages.
					return;
				}
				ListSelectionModel lsm = (ListSelectionModel) event.getSource();
				if (!lsm.isSelectionEmpty()) { // Ist überhaupt eine Zeile ausgewählt?
					int selectedRow = lsm.getMinSelectionIndex();
					int modelRow = tableStoredServices.convertRowIndexToModel(selectedRow);
					String serviceAbbreviation = tableModelStoredServices.getServiceInfoAt(modelRow)
							.getServiceAbbreviation();
					((PswGenCtl) ctl).valueChangedLoadServiceFromList(me, serviceAbbreviation);
				}
			}
		});
		scrollableTableStoredServices = new JScrollPane(tableStoredServices);
		// tableStoredServices.setFillsViewportHeight(true);
		// Widgets zufügen, erste Zeile
		int row = 0;
		panel.add(serviceAbbreviationFilter, gbcf.getFieldConstraints(0, row, 4, 1));
		// Nächste Zeile zum einfügen von Groß-/Kleinschreibung
		row++;
		panel.add(serviceAbbreviationFilterCaseSensitive, gbcf.getFieldConstraints(0, row, 2, 1));
		// Nächste Zeile zum einfügen der Regex-Suche
		row++;
		panel.add(serviceAbbreviationFilterAsRegex, gbcf.getFieldConstraints(0, row, 2, 1));
		panel.add(regexContainsErrors, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 2, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(scrollableTableStoredServices, gbcf.getTableConstraints(0, row, 4, 1));

		// Anzeige des regex-errors deaktivieren
		showRegexErrorMessage(false);

		// Panel zurückgeben
		return panel;
	}

	/**
	 * Panel zum "Passwort generieren" anlegen
	 */
	private JPanel createPanelEditService() {
		// Fabriken holen
		GridBagConstraintsFactory gbcf = GridBagConstraintsFactory.getInstance();
		WidgetFactory wf = WidgetFactory.getInstance();
		// Dirty-Listener erzeugen
		DocumentListener documentListener = new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				setDirty(true);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				setDirty(true);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				setDirty(true);
			}
		};
		ChangeListener changeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent event) {
				if (((AbstractButton) event.getSource()).getModel().isPressed()) {
					setDirty(true);
				}
			}
		};
		// Widgets erzeugen
		JPanel panel = wf.getPanel("PanelEditService");
		JLabel labelServiceAbbreviation = wf.getLabel("LabelServiceAbbreviation");
		labelServiceAbbreviation.setForeground(COLOR_INFLUENCE);
		serviceAbbreviation = wf.getTextField("FieldServiceAbbreviation");
		serviceAbbreviation.getDocument().addDocumentListener(documentListener);
		JButton buttonClearService = wf.getButton("ButtonClearService");
		buttonClearService.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedClearService(me);
			}
		});
		JLabel labelAdditionalInfo = wf.getLabel("LabelAdditionalInfo");
		labelAdditionalInfo.setForeground(COLOR_INFLUENCE);
		additionalInfo = wf.getTextField("FieldAdditionalInfo");
		additionalInfo.getDocument().addDocumentListener(documentListener);
		JButton buttonRemoveService = wf.getButton("ButtonRemoveService");
		buttonRemoveService.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedRemoveService(me);
			}
		});
		JLabel labelLoginUrl = wf.getLabel("LabelLoginUrl");
		loginUrl = wf.getTextField("FieldLoginUrl");
		loginUrl.getDocument().addDocumentListener(documentListener);
		JButton buttonOpenUrl = wf.getButton("ButtonOpenUrl");
		buttonOpenUrl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedOpenUrlInBrowser(me);
			}
		});
		JLabel labelLoginInfo = wf.getLabel("LabelLoginInfo");
		loginInfo = wf.getTextField("FieldLoginInfo");
		loginInfo.getDocument().addDocumentListener(documentListener);
		JButton buttonCopyLoginInfo = wf.getButton("ButtonCopyLoginInfo");
		buttonCopyLoginInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedCopyLoginInfo(me);
			}
		});
		JLabel labelAdditionalLoginInfo = wf.getLabel("LabelAdditionalLoginInfo");
		additionalLoginInfo = wf.getTextArea("FieldAdditionalLoginInfo");
		additionalLoginInfo.getDocument().addDocumentListener(documentListener);
		JScrollPane additionalLoginInfoScrollPane = wf.getScrollPane("ScrollPaneAdditionalLoginInfo",
				additionalLoginInfo);
		additionalLoginInfoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JButton buttonOpenHelp = wf.getButton("ButtonOpenHelp");
		buttonOpenHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedOpenHelpInBrowser(me);
			}
		});
		JButton buttonOpenAbout = wf.getButton("ButtonOpenAbout");
		buttonOpenAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedOpenAbout(me);
			}
		});
		JLabel labelCount = wf.getLabel("LabelCount");
		labelCount.setHorizontalAlignment(SwingConstants.CENTER);
		labelCount.setForeground(COLOR_INFLUENCE);
		JLabel labelStartIndex = wf.getLabel("LabelStartIndex");
		labelStartIndex.setHorizontalAlignment(SwingConstants.CENTER);
		labelStartIndex.setForeground(COLOR_INFLUENCE);
		JLabel labelEndIndex = wf.getLabel("LabelEndIndex");
		labelEndIndex.setHorizontalAlignment(SwingConstants.CENTER);
		labelEndIndex.setForeground(COLOR_INFLUENCE);
		useSmallLetters = wf.getCheckBox("CheckBoxSmallLetters");
		useSmallLetters.addChangeListener(changeListener);
		useSmallLetters.setForeground(COLOR_INFLUENCE);
		smallLettersCount = wf.getIntegerField("FieldSmallLettersCount");
		smallLettersCount.getDocument().addDocumentListener(documentListener);
		smallLettersStartIndex = wf.getIntegerField("FieldSmallLettersStartIndex");
		smallLettersStartIndex.getDocument().addDocumentListener(documentListener);
		smallLettersEndIndex = wf.getIntegerField("FieldSmallLettersEndIndex");
		smallLettersEndIndex.getDocument().addDocumentListener(documentListener);
		useCapitalLetters = wf.getCheckBox("CheckBoxCapitalLetters");
		useCapitalLetters.addChangeListener(changeListener);
		useCapitalLetters.setForeground(COLOR_INFLUENCE);
		capitalLettersCount = wf.getIntegerField("FieldCapitalLettersCount");
		capitalLettersCount.getDocument().addDocumentListener(documentListener);
		capitalLettersStartIndex = wf.getIntegerField("FieldCapitalLettersStartIndex");
		capitalLettersStartIndex.getDocument().addDocumentListener(documentListener);
		capitalLettersEndIndex = wf.getIntegerField("FieldCapitalLettersEndIndex");
		capitalLettersEndIndex.getDocument().addDocumentListener(documentListener);
		useDigits = wf.getCheckBox("CheckBoxDigits");
		useDigits.addChangeListener(changeListener);
		useDigits.setForeground(COLOR_INFLUENCE);
		digitsCount = wf.getIntegerField("FieldDigitsCount");
		digitsCount.getDocument().addDocumentListener(documentListener);
		digitsStartIndex = wf.getIntegerField("FieldDigitsStartIndex");
		digitsStartIndex.getDocument().addDocumentListener(documentListener);
		digitsEndIndex = wf.getIntegerField("FieldDigitsEndIndex");
		digitsEndIndex.getDocument().addDocumentListener(documentListener);
		useSpecialCharacters = wf.getCheckBox("CheckBoxSpecialCharacters");
		useSpecialCharacters.addChangeListener(changeListener);
		useSpecialCharacters.setForeground(COLOR_INFLUENCE);
		specialCharacters = wf.getTextField("FieldSpecialCharacters");
		specialCharacters.setForeground(COLOR_INFLUENCE);
		specialCharactersCount = wf.getIntegerField("FieldSpecialCharactersCount");
		specialCharactersCount.getDocument().addDocumentListener(documentListener);
		specialCharactersStartIndex = wf.getIntegerField("FieldSpecialCharactersStartIndex");
		specialCharactersStartIndex.getDocument().addDocumentListener(documentListener);
		specialCharactersEndIndex = wf.getIntegerField("FieldSpecialCharactersEndIndex");
		specialCharactersEndIndex.getDocument().addDocumentListener(documentListener);
		labelUseOldPassphrase = wf.getLabel("LabelUseOldPassphrase");
		labelUseOldPassphrase.setForeground(COLOR_WARNING);
		JLabel labelTotalCharacterCount = wf.getLabel("LabelTotalCharacterCount");
		labelTotalCharacterCount.setForeground(COLOR_INFLUENCE);
		totalCharacterCount = wf.getIntegerField("FieldTotalCharacterCount");
		totalCharacterCount.getDocument().addDocumentListener(documentListener);
		JButton buttonCopyPassword = wf.getButton("ButtonCopyPassword");
		buttonCopyPassword.setForeground(COLOR_INFLUENCE);
		buttonCopyPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedCopyPassword(me);
			}
		});
		JLabel labelPassword = wf.getLabel("LabelPassword");
		password = wf.getPasswordField("FieldPassword");
		password.getDocument().addDocumentListener(documentListener);
		JButton buttonDisplayPassword = wf.getButton("ButtonDisplayPassword");
		buttonDisplayPassword.setForeground(COLOR_INFLUENCE);
		buttonDisplayPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedDisplayPassword(me);
			}
		});
		JLabel labelPasswordRepeated = wf.getLabel("LabelPasswordRepeated");
		passwordRepeated = wf.getPasswordField("FieldPasswordRepeated");
		passwordRepeated.getDocument().addDocumentListener(documentListener);
		makePasswordVisible = wf.getCheckBox("CheckBoxMakePasswordVisible");
		makePasswordVisible.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (makePasswordVisible.isSelected()) {
					password.setEchoChar((char) 0);
					passwordRepeated.setEchoChar((char) 0);
				} else {
					password.setEchoChar('*');
					passwordRepeated.setEchoChar('*');
				}
			}

		});
		buttonStoreService = wf.getButton("ButtonStoreService");
		buttonStoreService.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedStoreService(me);
			}
		});
		buttonUseNewPassphrase = wf.getButton("ButtonUseNewPassphrase");
		buttonUseNewPassphrase.setForeground(COLOR_INFLUENCE);
		buttonUseNewPassphrase.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedUseNewPassphrase(me);
			}
		});
		JLabel labelLastUpdate = wf.getLabel("LabelLastUpdate");
		lastUpdate = wf.getLabel("LabelLastUpdate");
		// Widgets zufügen, erste Zeile
		int row = 0;
		panel.add(labelServiceAbbreviation, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(serviceAbbreviation, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonClearService, gbcf.getLabelConstraints(6, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelAdditionalInfo, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(additionalInfo, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonRemoveService, gbcf.getLabelConstraints(6, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelLoginUrl, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(loginUrl, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonOpenUrl, gbcf.getLabelConstraints(6, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelLoginInfo, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(loginInfo, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonCopyLoginInfo, gbcf.getLabelConstraints(6, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelAdditionalLoginInfo, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(additionalLoginInfoScrollPane,
				gbcf.getTableConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelCount, gbcf.getLabelConstraints(2, row));
		panel.add(labelStartIndex, gbcf.getLabelConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(labelEndIndex, gbcf.getLabelConstraints(GridBagConstraints.RELATIVE, row));
		// Leerzeile zufügen, nächste Zeile
		row++;
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(useSmallLetters, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(smallLettersCount, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(smallLettersStartIndex, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(smallLettersEndIndex, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(buttonOpenHelp, gbcf.getLabelConstraints(6, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(useCapitalLetters, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(capitalLettersCount, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(capitalLettersStartIndex, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(capitalLettersEndIndex, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(buttonOpenAbout, gbcf.getLabelConstraints(6, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(useDigits, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(digitsCount, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(digitsStartIndex, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(digitsEndIndex, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(useSpecialCharacters, gbcf.getLabelConstraints(0, row, 1, 1));
		panel.add(specialCharacters, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(specialCharactersCount, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(specialCharactersStartIndex,
				gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(specialCharactersEndIndex,
				gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(labelUseOldPassphrase, gbcf.getLabelConstraints(6, row, 1, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelTotalCharacterCount, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(totalCharacterCount, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 1, 1));
		panel.add(buttonCopyPassword, gbcf.getLabelConstraints(6, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelPassword, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(password, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonDisplayPassword, gbcf.getLabelConstraints(6, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelPasswordRepeated, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(passwordRepeated, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonStoreService, gbcf.getLabelConstraints(6, row));
		panel.add(buttonUseNewPassphrase, gbcf.getLabelConstraints(6, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(makePasswordVisible, gbcf.getFieldConstraints(2, row, 4, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		GridBagConstraints emptyLineConstraints = gbcf.getLabelConstraints(0, row, 2, 1);
		emptyLineConstraints.weighty = 0.1;
		panel.add(new JLabel(""), emptyLineConstraints);
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelLastUpdate, gbcf.getLabelConstraints(0, row, 2, 1));
		panel.add(lastUpdate, gbcf.getFieldConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		// Panel zurückgeben
		return panel;
	}

	public void updateSearch() {
		showRegexErrorMessage(false);

		final String text = serviceAbbreviationFilter.getText();
		if (EmptyHelper.isEmpty(text)) {
			tableRowSorter.setRowFilter(null);
			return;
		}

		if (serviceAbbreviationFilterAsRegex.isSelected()) {
			try {
				final Pattern pattern = Pattern.compile(text,
						serviceAbbreviationFilterCaseSensitive.isSelected() ? 0 : Pattern.CASE_INSENSITIVE);
				tableRowSorter.setRowFilter(new RowFilter<StoredServicesTableModel, Integer>() {
					@Override
					public boolean include(
							Entry<? extends StoredServicesTableModel, ? extends Integer> entry) {
						ServiceInfo si = entry.getModel().getServiceInfoAt(entry.getIdentifier());
						return pattern.matcher(si.getServiceAbbreviation()).matches();
					}
				});
			} catch (PatternSyntaxException ex) {
				showRegexErrorMessage(true, ex.getLocalizedMessage());

				tableRowSorter.setRowFilter(new RowFilter<StoredServicesTableModel, Integer>() {
					@Override
					public boolean include(
							Entry<? extends StoredServicesTableModel, ? extends Integer> entry) {
						return false;
					}
				});
			}
		} else {
			tableRowSorter.setRowFilter(new RowFilter<StoredServicesTableModel, Integer>() {
				@Override
				public boolean include(Entry<? extends StoredServicesTableModel, ? extends Integer> entry) {
					ServiceInfo si = entry.getModel().getServiceInfoAt(entry.getIdentifier());
					String sa;
					String startText;
					if (serviceAbbreviationFilterCaseSensitive.isSelected()) {
						sa = si.getServiceAbbreviation();
						startText = text;
					} else {
						sa = si.getServiceAbbreviation().toLowerCase();
						startText = text.toLowerCase();
					}

					// Generate as much percents as needed as a replacement for \| to not to affect any other
					// percent.
					StringBuilder replacementBuilder = new StringBuilder("%");
					while (startText.contains(replacementBuilder)) {
						replacementBuilder.append('%');
					}
					String replacement = replacementBuilder.toString();
					// Replace \| with the generated replacement.
					startText = startText.replaceAll("\\\\\\|", replacement);
					String[] startTexts = startText.split("\\|");
					for (int i = 0; i < startTexts.length; i++) {
						// Now, re-replace \| to |.
						startTexts[i] = startTexts[i].replaceAll(Pattern.quote(replacement), "|");
					}

					// Matching any service appreviation in a loop in order to match every search parameter.
					for (final String str : startTexts) {
						if (sa.startsWith(str)) {
							return true;
						}
					}
					return false;
				}
			});
		}
	}

	/**
	 * Aktualisiert die Tabelle mit den gespeicherten Diensten und zeigt sie neu an.
	 */
	public void updateStoredServices() {
		tableModelStoredServices.setData(((PswGenCtl) ctl).getServices().getServices());
	}

	/**
	 * @return Returns the capitalEndIndex.
	 */
	public int getCapitalLettersEndIndex() {
		return capitalLettersEndIndex.getIntValue();
	}

	/**
	 * @return Returns the capitalLettersCount.
	 */
	public int getCapitalLettersCount() {
		return capitalLettersCount.getIntValue();
	}

	/**
	 * @return Returns the capitalStartIndex.
	 */
	public int getCapitalLettersStartIndex() {
		return capitalLettersStartIndex.getIntValue();
	}

	/**
	 * @return Returns the digitsCount.
	 */
	public int getDigitsCount() {
		return digitsCount.getIntValue();
	}

	/**
	 * @return Returns the digitsEndIndex.
	 */
	public int getDigitsEndIndex() {
		return digitsEndIndex.getIntValue();
	}

	/**
	 * @return Returns the digitsStartIndex.
	 */
	public int getDigitsStartIndex() {
		return digitsStartIndex.getIntValue();
	}

	/**
	 * @return Returns the useCapitalLetters.
	 */
	public boolean getUseCapitalLetters() {
		return useCapitalLetters.isSelected();
	}

	/**
	 * @return Returns the useDigits.
	 */
	public boolean getUseDigits() {
		return useDigits.isSelected();
	}

	/**
	 * @return Returns the useSmallLetters.
	 */
	public boolean getUseSmallLetters() {
		return useSmallLetters.isSelected();
	}

	/**
	 * @return Returns the useSpecialCharacters.
	 */
	public boolean getUseSpecialCharacters() {
		return useSpecialCharacters.isSelected();
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return new String(password.getPassword());
	}

	/**
	 * @return Returns the passwordRepeated.
	 */
	public String getPasswordRepeated() {
		return new String(passwordRepeated.getPassword());
	}

	/**
	 * @return Returns the serviceAbbreviation.
	 */
	public String getServiceAbbreviation() {
		return serviceAbbreviation.getText();
	}

	/**
	 * @return Returns the smallEndIndex.
	 */
	public int getSmallLettersEndIndex() {
		return smallLettersEndIndex.getIntValue();
	}

	/**
	 * @return Returns the smallLettersCount.
	 */
	public int getSmallLettersCount() {
		return smallLettersCount.getIntValue();
	}

	/**
	 * @return Returns the smallStartIndex.
	 */
	public int getSmallLettersStartIndex() {
		return smallLettersStartIndex.getIntValue();
	}

	/**
	 * @return Returns the specialCharactersCount.
	 */
	public int getSpecialCharactersCount() {
		return specialCharactersCount.getIntValue();
	}

	/**
	 * @return Returns the specialCharactersEndIndex.
	 */
	public int getSpecialCharactersEndIndex() {
		return specialCharactersEndIndex.getIntValue();
	}

	/**
	 * @return Returns the specialCharactersStartIndex.
	 */
	public int getSpecialCharactersStartIndex() {
		return specialCharactersStartIndex.getIntValue();
	}

	/**
	 * @return Returns the totalCharacterCount.
	 */
	public int getTotalCharacterCount() {
		return totalCharacterCount.getIntValue();
	}

	/**
	 * @param password
	 */
	public void setPassword(final String password) {
		this.password.setText(password);
	}

	/**
	 * @param passwordRepeated
	 */
	public void setPasswordRepeated(final String passwordRepeated) {
		this.passwordRepeated.setText(passwordRepeated);
	}

	/**
	 * @param capitalLettersCount
	 *            The capitalLettersCount to set.
	 */
	public void setCapitalLettersCount(final int capitalLettersCount) {
		this.capitalLettersCount.setIntValue(capitalLettersCount);
	}

	/**
	 * @param capitalLettersEndIndex
	 *            The capitalLettersEndIndex to set.
	 */
	public void setCapitalLettersEndIndex(final int capitalLettersEndIndex) {
		this.capitalLettersEndIndex.setIntValue(capitalLettersEndIndex);
	}

	/**
	 * @param capitalLettersStartIndex
	 *            The capitalLettersStartIndex to set.
	 */
	public void setCapitalLettersStartIndex(final int capitalLettersStartIndex) {
		this.capitalLettersStartIndex.setIntValue(capitalLettersStartIndex);
	}

	/**
	 * @param digitsCount
	 *            The digitsCount to set.
	 */
	public void setDigitsCount(final int digitsCount) {
		this.digitsCount.setIntValue(digitsCount);
	}

	/**
	 * @param digitsEndIndex
	 *            The digitsEndIndex to set.
	 */
	public void setDigitsEndIndex(final int digitsEndIndex) {
		this.digitsEndIndex.setIntValue(digitsEndIndex);
	}

	/**
	 * @param digitsStartIndex
	 *            The digitsStartIndex to set.
	 */
	public void setDigitsStartIndex(final int digitsStartIndex) {
		this.digitsStartIndex.setIntValue(digitsStartIndex);
	}

	/**
	 * @param serviceAbbreviation
	 *            The serviceAbbreviation to set.
	 */
	public void setServiceAbbreviation(final String serviceAbbreviation) {
		this.serviceAbbreviation.setText(serviceAbbreviation);
	}

	/**
	 * @param smallLettersCount
	 *            The smallLettersCount to set.
	 */
	public void setSmallLettersCount(final int smallLettersCount) {
		this.smallLettersCount.setIntValue(smallLettersCount);
	}

	/**
	 * @param smallLettersEndIndex
	 *            The smallLettersEndIndex to set.
	 */
	public void setSmallLettersEndIndex(final int smallLettersEndIndex) {
		this.smallLettersEndIndex.setIntValue(smallLettersEndIndex);
	}

	/**
	 * @param smallLettersStartIndex
	 *            The smallLettersStartIndex to set.
	 */
	public void setSmallLettersStartIndex(final int smallLettersStartIndex) {
		this.smallLettersStartIndex.setIntValue(smallLettersStartIndex);
	}

	/**
	 * @param specialCharactersCount
	 *            The specialCharactersCount to set.
	 */
	public void setSpecialCharactersCount(final int specialCharactersCount) {
		this.specialCharactersCount.setIntValue(specialCharactersCount);
	}

	/**
	 * @param specialCharactersEndIndex
	 *            The specialCharactersEndIndex to set.
	 */
	public void setSpecialCharactersEndIndex(final int specialCharactersEndIndex) {
		this.specialCharactersEndIndex.setIntValue(specialCharactersEndIndex);
	}

	/**
	 * @param specialCharactersStartIndex
	 *            The specialCharactersStartIndex to set.
	 */
	public void setSpecialCharactersStartIndex(final int specialCharactersStartIndex) {
		this.specialCharactersStartIndex.setIntValue(specialCharactersStartIndex);
	}

	/**
	 * @param totalCharacterCount
	 *            The totalCharacterCount to set.
	 */
	public void setTotalCharacterCount(final int totalCharacterCount) {
		this.totalCharacterCount.setIntValue(totalCharacterCount);
	}

	/**
	 * @param useCapitalLetters
	 *            The useCapitalLetters to set.
	 */
	public void setUseCapitalLetters(final boolean useCapitalLetters) {
		this.useCapitalLetters.setSelected(useCapitalLetters);
	}

	/**
	 * @param useDigits
	 *            The useDigits to set.
	 */
	public void setUseDigits(final boolean useDigits) {
		this.useDigits.setSelected(useDigits);
	}

	/**
	 * @param useSmallLetters
	 *            The useSmallLetters to set.
	 */
	public void setUseSmallLetters(final boolean useSmallLetters) {
		this.useSmallLetters.setSelected(useSmallLetters);
	}

	/**
	 * @param useSpecialCharacters
	 *            The useSpecialCharacters to set.
	 */
	public void setUseSpecialCharacters(final boolean useSpecialCharacters) {
		this.useSpecialCharacters.setSelected(useSpecialCharacters);
	}

	public String getAdditionalInfo() {
		return additionalInfo.getText();
	}

	public void setAdditionalInfo(final String additionalInfo) {
		this.additionalInfo.setText(additionalInfo);
	}

	public String getLoginUrl() {
		return loginUrl.getText();
	}

	public void setLoginUrl(final String loginUrl) {
		this.loginUrl.setText(loginUrl);
	}

	public String getLoginInfo() {
		return loginInfo.getText();
	}

	public void setLoginInfo(final String loginInfo) {
		this.loginInfo.setText(loginInfo);
	}

	public String getAdditionalLoginInfo() {
		return additionalLoginInfo.getText();
	}

	public void setAdditionalLoginInfo(String additionalLoginInfo) {
		this.additionalLoginInfo.setText(additionalLoginInfo);
	}

	public String getSpecialCharacters() {
		return specialCharacters.getText();
	}

	public void setSpecialCharacters(String specialCharacters) {
		this.specialCharacters.setText(specialCharacters);
	}

	public boolean getUseOldPassphrase() {
		return labelUseOldPassphrase.isVisible();
	}

	public String getLastUpdate() {
		return lastUpdate.getText();
	}

	public void setLastUpdate(final String lastUpdate) {
		this.lastUpdate.setText(lastUpdate);
	}

	/**
	 * @return the dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @param dirty
	 *            the dirty to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		String title = getTitle();
		if (title.startsWith(DIRTY_TAG_TITLE_PREFIX)) {
			title = title.substring(DIRTY_TAG_TITLE_PREFIX.length());
		}
		setTitle(((dirty) ? DIRTY_TAG_TITLE_PREFIX : "") + title);
	}

	public boolean isRegexEnable() {
		return serviceAbbreviationFilterAsRegex.isSelected();
	}

	public void setRegexEnabled(boolean regexEnabled) {
		serviceAbbreviationFilterAsRegex.setSelected(regexEnabled);
	}

	private void showRegexErrorMessage(boolean show, String message) {
		regexContainsErrors.setVisible(show);
		regexContainsErrors.setToolTipText((EmptyHelper.isEmpty(message)) ? null : message);
	}

	private void showRegexErrorMessage(boolean show) {
		showRegexErrorMessage(show, null);
	}

	public void setUseOldPassphrase(boolean useOldPassphrase) {
		labelUseOldPassphrase.setVisible(useOldPassphrase);
		buttonStoreService.setVisible(!useOldPassphrase);
		buttonUseNewPassphrase.setVisible(useOldPassphrase);
	}

}

package net.sf.pswgen.gui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import net.sf.pswgen.gui.base.BaseCtl;
import net.sf.pswgen.gui.base.BaseView;
import net.sf.pswgen.gui.base.DbcIntegerField;
import net.sf.pswgen.gui.base.GridBagConstraintsFactory;
import net.sf.pswgen.gui.base.WidgetFactory;
import net.sf.pswgen.model.ServiceInfo;

/**
 * <p>
 * Dies ist die View des Hauptfensters von PswGen.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
public class MFView extends BaseView {

	/** Version für die Serialisierung */
	private static final long serialVersionUID = -3013326579046715523L;

	private StoredServicesTableModel tableModelStoredServices;

	private JPasswordField passphrase;

	private JPasswordField passphraseRepeated;

	private JTextField mServiceAbbreviationFilter;

	private JTable mTableStoredServices;

	private TableRowSorter<StoredServicesTableModel> mTableRowSorter;

	private JTextField serviceAbbreviation;

	private JTextField additionalInfo;

	private JTextField loginUrl;

	private JTextField loginInfo;

	private JTextField additionalLoginInfo;

	private JCheckBox useSmallLetters;

	private JCheckBox useCapitalLetters;

	private JCheckBox useDigits;

	private JCheckBox useSpecialCharacters;

	private DbcIntegerField smallLettersCount;

	private DbcIntegerField smallLettersStartIndex;

	private DbcIntegerField mSmallLettersEndIndex;

	private DbcIntegerField capitalLettersCount;

	private DbcIntegerField capitalLettersStartIndex;

	private DbcIntegerField capitalLettersEndIndex;

	private DbcIntegerField digitsCount;

	private DbcIntegerField specialCharactersCount;

	private DbcIntegerField digitsStartIndex;

	private DbcIntegerField digitsEndIndex;

	private DbcIntegerField specialCharactersStartIndex;

	private DbcIntegerField specialCharactersEndIndex;

	private DbcIntegerField totalCharacterCount;

	private JPasswordField password;

	private JPasswordField passwordRepeated;

	/** Hey, it's me ... für die Listener */
	private MFView me = this;

	private JScrollPane scrollableTableStoredServices;

	/**
	 * Konstruiert diese View mit einer Referenz auf den zugehörigen Controller.
	 */
	public MFView(BaseCtl ctl) {
		super(ctl);
		this.setResizable(false);
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
		JPanel panel = wf.getContentPane("MainFrame");
		panel.add(createPanelPassphrase(), gbcf.getConstraints(0, 0));
		panel.add(createPanelStoredServices(), gbcf.getConstraints(0, 1));
		panel.add(createPanelEditService(), gbcf.getConstraints(0, 2));
		return panel;
	}

	/**
	 * Panel zum "Passphrase" anlegen
	 */
	private JPanel createPanelPassphrase() {
		// Fabriken holen
		GridBagConstraintsFactory gbcf = GridBagConstraintsFactory.getInstance();
		WidgetFactory wf = WidgetFactory.getInstance();
		// Widgets erzeugen
		JPanel panel = wf.getPanel("PanelPassphrase");
		JLabel labelPassphrase = wf.getLabel("LabelPassphrase");
		passphrase = wf.getPasswordField("FieldPassphrase");
		JLabel labelPassphraseRepeated = wf.getLabel("LabelPassphraseRepeated");
		passphraseRepeated = wf.getPasswordField("FieldPassphraseRepeated");
		passphraseRepeated.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (!((PswGenCtl) ctl).focusLostValidatePassphrase(me)) {
					passphrase.requestFocus();
				}
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
		// Widgets zufügen, Zeile 0
		panel.add(labelPassphrase, gbcf.getConstraints(0, 0));
		panel.add(passphrase, gbcf.getConstraints(GridBagConstraints.RELATIVE, 0));
		// Widgets zufügen, Zeile 1
		panel.add(labelPassphraseRepeated, gbcf.getConstraints(0, 1));
		panel.add(passphraseRepeated, gbcf.getConstraints(GridBagConstraints.RELATIVE, 1));
		// Panel zurückgeben
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
		mServiceAbbreviationFilter = wf.getTextField("FieldServiceAbbreviation");
		mServiceAbbreviationFilter.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				final String text = mServiceAbbreviationFilter.getText();
				if (text.length() == 0) {
					mTableRowSorter.setRowFilter(null);
				} else {
					mTableRowSorter.setRowFilter(new RowFilter<StoredServicesTableModel, Integer>() {
						@Override
						public boolean include(
								Entry<? extends StoredServicesTableModel, ? extends Integer> entry) {
							ServiceInfo si = entry.getModel().getServiceInfoAt(entry.getIdentifier());
							return si.getServiceAbbreviation().toLowerCase().startsWith(text);
						}
					});
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});
		tableModelStoredServices = new StoredServicesTableModel(
				((PswGenCtl) ctl).getServices().getServices(), new String[] {
						ctl.getGuiText("LabelServiceAbbreviation"), ctl.getGuiText("LabelAdditionalInfo"),
						ctl.getGuiText("LabelLoginUrl") });
		mTableStoredServices = wf.getTable("TableStoredServices", tableModelStoredServices);
		mTableStoredServices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mTableRowSorter = new TableRowSorter<StoredServicesTableModel>(tableModelStoredServices);
		mTableRowSorter.setComparator(StoredServicesTableModel.COL_ADDITIONAL_INFO, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				return convertDateString(arg0).compareTo(convertDateString(arg1));
			}

			private String convertDateString(String s) {
				Pattern p = Pattern.compile("(\\d\\d)\\.(\\d\\d)\\.(\\d\\d\\d\\d)");
				Matcher m = p.matcher(s);
				return (m.matches()) ? m.group(3) + "-" + m.group(2) + "-" + m.group(1) : s;
			}

		});
		mTableStoredServices.setRowSorter(mTableRowSorter);
		// Ask to be notified of selection changes.
		ListSelectionModel rowSM = mTableStoredServices.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting()) {// Ignore extra messages.
					return;
				}
				ListSelectionModel lsm = (ListSelectionModel) event.getSource();
				if (!lsm.isSelectionEmpty()) { // Ist überhaupt eine Zeile ausgewählt?
					int selectedRow = lsm.getMinSelectionIndex();
					int modelRow = mTableStoredServices.convertRowIndexToModel(selectedRow);
					String serviceAbbreviation = tableModelStoredServices.getServiceInfoAt(modelRow)
							.getServiceAbbreviation();
					((PswGenCtl) ctl).valueChangedLoadServiceFromList(me, serviceAbbreviation);
				}
			}
		});
		scrollableTableStoredServices = new JScrollPane(mTableStoredServices);
		// tableStoredServices.setFillsViewportHeight(true);
		// Widgets zufügen, erste Zeile
		int row = 0;
		panel.add(mServiceAbbreviationFilter, gbcf.getConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(scrollableTableStoredServices, gbcf.getConstraints(0, row));
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
		// Widgets erzeugen
		JPanel panel = wf.getPanel("PanelEditService");
		JLabel labelServiceAbbreviation = wf.getLabel("LabelServiceAbbreviation");
		serviceAbbreviation = wf.getTextField("FieldServiceAbbreviation");
		JButton buttonLoadService = wf.getButton("ButtonLoadService");
		buttonLoadService.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedLoadService(me);
			}
		});
		JLabel labelAdditionalInfo = wf.getLabel("LabelAdditionalInfo");
		additionalInfo = wf.getTextField("FieldAdditionalInfo");
		JButton buttonRemoveService = wf.getButton("ButtonRemoveService");
		buttonRemoveService.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedRemoveService(me);
			}
		});
		JLabel labelLoginUrl = wf.getLabel("LabelLoginUrl");
		loginUrl = wf.getTextField("FieldLoginUrl");
		JButton buttonOpenUrl = wf.getButton("ButtonOpenUrl");
		buttonOpenUrl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedOpenUrlInBrowser(me);
			}
		});
		JLabel labelLoginInfo = wf.getLabel("LabelLoginInfo");
		loginInfo = wf.getTextField("FieldLoginInfo");
		JButton buttonCopyLoginInfo = wf.getButton("ButtonCopyLoginInfo");
		buttonCopyLoginInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedCopyLoginInfo(me);
			}
		});
		JLabel labelAdditionalLoginInfo = wf.getLabel("LabelAdditionalLoginInfo");
		additionalLoginInfo = wf.getTextField("FieldAdditionalLoginInfo");
		JLabel labelCount = wf.getLabel("LabelCount");
		JLabel labelStartIndex = wf.getLabel("LabelStartIndex");
		JLabel labelEndIndex = wf.getLabel("LabelEndIndex");
		useSmallLetters = wf.getCheckBox("CheckBoxSmallLetters");
		smallLettersCount = wf.getIntegerField("FieldSmallLettersCount");
		smallLettersStartIndex = wf.getIntegerField("FieldSmallLettersStartIndex");
		mSmallLettersEndIndex = wf.getIntegerField("FieldSmallLettersEndIndex");
		useCapitalLetters = wf.getCheckBox("CheckBoxCapitalLetters");
		capitalLettersCount = wf.getIntegerField("FieldCapitalLettersCount");
		capitalLettersStartIndex = wf.getIntegerField("FieldCapitalLettersStartIndex");
		capitalLettersEndIndex = wf.getIntegerField("FieldCapitalLettersEndIndex");
		useDigits = wf.getCheckBox("CheckBoxDigits");
		digitsCount = wf.getIntegerField("FieldDigitsCount");
		digitsStartIndex = wf.getIntegerField("FieldDigitsStartIndex");
		digitsEndIndex = wf.getIntegerField("FieldDigitsEndIndex");
		useSpecialCharacters = wf.getCheckBox("CheckBoxSpecialCharacters");
		specialCharactersCount = wf.getIntegerField("FieldSpecialCharactersCount");
		specialCharactersStartIndex = wf.getIntegerField("FieldSpecialCharactersStartIndex");
		specialCharactersEndIndex = wf.getIntegerField("FieldSpecialCharactersEndIndex");
		JLabel labelTotalCharacterCount = wf.getLabel("LabelTotalCharacterCount");
		totalCharacterCount = wf.getIntegerField("FieldTotalCharacterCount");
		JButton buttonCopyPassword = wf.getButton("ButtonCopyPassword");
		buttonCopyPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedCopyPassword(me);
			}
		});
		JLabel labelPassword = wf.getLabel("LabelPassword");
		password = wf.getPasswordField("FieldPassword");
		JButton buttonDisplayPassword = wf.getButton("ButtonDisplayPassword");
		buttonDisplayPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedDisplayPassword(me);
			}
		});
		JLabel labelPasswordRepeated = wf.getLabel("LabelPasswordRepeated");
		passwordRepeated = wf.getPasswordField("FieldPasswordRepeated");
		JButton buttonStoreService = wf.getButton("ButtonStoreService");
		buttonStoreService.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((PswGenCtl) ctl).actionPerformedStoreService(me);
			}
		});
		// Widgets zufügen, erste Zeile
		int row = 0;
		panel.add(labelServiceAbbreviation, gbcf.getConstraints(0, row));
		panel.add(serviceAbbreviation, gbcf.getConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonLoadService, gbcf.getConstraints(5, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelAdditionalInfo, gbcf.getConstraints(0, row));
		panel.add(additionalInfo, gbcf.getConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonRemoveService, gbcf.getConstraints(5, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelLoginUrl, gbcf.getConstraints(0, row));
		panel.add(loginUrl, gbcf.getConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonOpenUrl, gbcf.getConstraints(5, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelLoginInfo, gbcf.getConstraints(0, row));
		panel.add(loginInfo, gbcf.getConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonCopyLoginInfo, gbcf.getConstraints(5, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelAdditionalLoginInfo, gbcf.getConstraints(0, row));
		panel.add(additionalLoginInfo, gbcf.getConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelCount, gbcf.getConstraints(1, row));
		panel.add(labelStartIndex, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(labelEndIndex, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		// Leerzeile zufügen, nächste Zeile
		row++;
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(useSmallLetters, gbcf.getConstraints(0, row));
		panel.add(smallLettersCount, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(smallLettersStartIndex, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(mSmallLettersEndIndex, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(useCapitalLetters, gbcf.getConstraints(0, row));
		panel.add(capitalLettersCount, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(capitalLettersStartIndex, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(capitalLettersEndIndex, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(useDigits, gbcf.getConstraints(0, row));
		panel.add(digitsCount, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(digitsStartIndex, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(digitsEndIndex, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(useSpecialCharacters, gbcf.getConstraints(0, row));
		panel.add(specialCharactersCount, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(specialCharactersStartIndex, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(specialCharactersEndIndex, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelTotalCharacterCount, gbcf.getConstraints(0, row));
		panel.add(totalCharacterCount, gbcf.getConstraints(GridBagConstraints.RELATIVE, row));
		panel.add(buttonCopyPassword, gbcf.getConstraints(5, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelPassword, gbcf.getConstraints(0, row));
		panel.add(password, gbcf.getConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonDisplayPassword, gbcf.getConstraints(5, row));
		// Widgets zufügen, nächste Zeile
		row++;
		panel.add(labelPasswordRepeated, gbcf.getConstraints(0, row));
		panel.add(passwordRepeated, gbcf.getConstraints(GridBagConstraints.RELATIVE, row, 4, 1));
		panel.add(buttonStoreService, gbcf.getConstraints(5, row));
		// Panel zurückgeben
		return panel;
	}

	/**
	 * Aktualisiert die Tabelle mit den gespeicherten Diensten und zeigt sie neu an.
	 */
	public void updateStoredService() {
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
	 * @return Returns the passphrase.
	 */
	public String getPassphrase() {
		return new String(passphrase.getPassword());
	}

	/**
	 * @return Returns the passphraseRepeated.
	 */
	public String getPassphraseRepeated() {
		return new String(passphraseRepeated.getPassword());
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
		return mSmallLettersEndIndex.getIntValue();
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
	 * @param passphrase
	 *            The passphrase to set.
	 */
	public void setPassphrase(final String passphrase) {
		this.passphrase.setText(passphrase);
	}

	/**
	 * @param passphraseRepeated
	 *            The passphraseRepeated to set.
	 */
	public void setPassphraseRepeated(final String passphraseRepeated) {
		this.passphraseRepeated.setText(passphraseRepeated);
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
		mSmallLettersEndIndex.setIntValue(smallLettersEndIndex);
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

}
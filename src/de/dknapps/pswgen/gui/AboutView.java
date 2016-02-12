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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.dknapps.pswgen.ApplicationPackageNameMarker;
import de.dknapps.pswgen.gui.base.BaseCtl;
import de.dknapps.pswgen.gui.base.BaseView;
import de.dknapps.pswgen.gui.base.WidgetFactory;
import de.dknapps.pswgen.util.Constants;

/**
 * <p>
 * Dies ist die View des About-Dialogs.
 * </p>
 */
public class AboutView extends BaseView {

	/** Version für die Serialisierung */
	private static final long serialVersionUID = 5427014107496448188L;

	/** Hey, it's me ... für die Listener */
	@SuppressWarnings("unused")
	private AboutView me = this;

	/**
	 * Konstruiert diese View mit einer Referenz auf den zugehörigen Controller.
	 */
	public AboutView(BaseCtl ctl) {
		super(ctl);
	}

	/**
	 * This method initializes this
	 */
	@Override
	public void initialize() {
		super.initialize();
		setResizable(false);
	}

	/**
	 * This method initializes the content pane.
	 */
	@Override
	public JPanel createContentPane() {
		WidgetFactory wf = WidgetFactory.getInstance();
		JPanel panel = wf.getContentPane("PanelAboutPswGen");
		// JPanel panel = new JPanel();
		JEditorPane aboutPswGenEditorPane = new JEditorPane();
		aboutPswGenEditorPane.setEditable(false);
		aboutPswGenEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		aboutPswGenEditorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						Desktop.getDesktop().browse(event.getURL().toURI());
					} catch (IOException | URISyntaxException e) {
						throw new RuntimeException("Could not load HTML file for AboutView", e);
					}
				}
			}
		});
		URL aboutURL = ApplicationPackageNameMarker.class.getResource(Constants.ABOUT_DIALOG_HTML_FILENAME);
		try {
			aboutPswGenEditorPane.setPage(aboutURL);
		} catch (IOException e) {
			throw new RuntimeException("Could not load HTML file for AboutView", e);
		}

		// Put the editor pane in a scroll pane.
		JScrollPane aboutPswGenScrollPane = wf.getScrollPane("ScrollPaneAboutPswGen", aboutPswGenEditorPane);
		aboutPswGenScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(aboutPswGenScrollPane);
		return panel;
	}
}
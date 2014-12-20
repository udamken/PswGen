package net.sf.pswgen.gui;

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

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.pswgen.gui.base.BaseCtl;
import net.sf.pswgen.gui.base.BaseView;
import net.sf.pswgen.util.Constants;
import net.sf.pswgen.util.ImageHelper;

/**
 * <p>
 * Dies ist die View des About-Dialogs.
 * </p>
 * <p>
 * Copyright (C) 2005-2013 Uwe Damken
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
		JPanel panel = new JPanel();
		JLabel labelSplashImage = new JLabel(ImageHelper.getInstance().getImageIcon(
				Constants.ABOUT_IMAGE_RESOURCE_NAME));
		panel.add(labelSplashImage);
		return panel;
	}
}
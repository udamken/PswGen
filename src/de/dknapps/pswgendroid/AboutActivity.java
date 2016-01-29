package de.dknapps.pswgendroid;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them
 PswGenDroid - Generates your passwords managed by PswGen on your mobile  

 Copyright (C) 2005-2016 Uwe Damken

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

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * <p>
 * Diese Activity stellt einen About-Dialog dar, der z.B. die Lizenz anzeigt.
 * </p>
 * <p>
 * Copyright (C) 2014 Uwe Damken
 * </p>
 */
public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		WebView myTextview;

		myTextview = (WebView) findViewById(R.id.about_apache_license_text);
		myTextview.loadUrl(getString(R.string.about_apache_license_file_url));
	}
}

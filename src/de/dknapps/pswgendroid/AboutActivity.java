/*******************************************************************************
 * PswGen - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGen on your mobile  
 *
 *     Copyright (C) 2005, 2016 Uwe Damken
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
package de.dknapps.pswgendroid;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * <p>
 * Diese Activity stellt einen About-Dialog dar, der z.B. die Lizenz anzeigt.
 * </p>
 */
public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		TextView textViewAboutPswGenDroidTitle = (TextView) findViewById(R.id.about_pswgendroid_title);
		String versionName = "Version N/A";
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// Schweigend ignorieren
		}
		textViewAboutPswGenDroidTitle.setText(getText(R.string.app_name) + " " + versionName);
		WebView webViewAboutApacheLicenseText = (WebView) findViewById(R.id.about_apache_license_text);
		webViewAboutApacheLicenseText.loadUrl(getString(R.string.about_apache_license_file_url));
	}
}

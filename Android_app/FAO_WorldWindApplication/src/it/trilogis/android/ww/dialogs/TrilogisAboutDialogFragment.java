/*
 * Copyright (C) 2014 Trilogis S.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.trilogis.android.ww.dialogs;


import it.trilogis.android.fao.R;
import it.trilogis.android.fao.constants.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

/**
 * @author Nicola Dorigatti
 */
public class TrilogisAboutDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.trilogis_about_layout, null);
		WebView webView = (WebView) view.findViewById(R.id.webview);
		webView.loadUrl(Constants.TRILOGIS_ABOUT_URL);
		builder.setView(view).setPositiveButton(getString(android.R.string.ok), null).setTitle("Trilogis Srl");

		return builder.create();
	}
}

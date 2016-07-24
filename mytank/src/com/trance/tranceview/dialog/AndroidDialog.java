package com.trance.tranceview.dialog;

import android.app.ProgressDialog;
import android.content.Context;

public class AndroidDialog extends ProgressDialog{

	public AndroidDialog(Context context) {
		super(context);
		this.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
}

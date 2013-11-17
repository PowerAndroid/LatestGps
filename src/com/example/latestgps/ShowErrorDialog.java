package com.example.latestgps;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ShowErrorDialog extends DialogFragment {

	private Dialog mdialog;

	public ShowErrorDialog() {
		super();
		mdialog = null;

	}

	public void setDialog(Dialog dialog) {

		mdialog = dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return mdialog;
	}
}

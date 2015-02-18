package motocitizen.utils;

import motocitizen.startup.Startup;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Alert {
	protected final static Activity act = (Activity) Startup.context;

	public Alert(String text) {
		AlertDialog.Builder ad = new AlertDialog.Builder(act);
		ad.setTitle(text).setMessage(text);
		ad.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		ad.show();
	}
}

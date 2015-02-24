package motocitizen.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import motocitizen.app.mc.notification.MCNotification;
import motocitizen.utils.Show;


public class AuthorizedUser extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String text = intent.getStringExtra("text");
		Log.d("INTENT", text);
		new MCNotification(text);
		Show.showCurrent();
	}
}

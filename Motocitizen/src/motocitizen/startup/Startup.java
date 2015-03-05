package motocitizen.startup;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCLocation;
import motocitizen.app.mc.gcm.GcmBroadcastReceiver;
import motocitizen.app.osm.OSMMap;
import motocitizen.core.settings.SettingsMenu;
import motocitizen.main.R;
import motocitizen.utils.Const;
import motocitizen.utils.Keyboard;
import motocitizen.utils.Props;
import motocitizen.utils.Show;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Window;

public class Startup extends Activity {
	public static Props props;
	public static Context context;
	public static SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		context = this;
		new Const();

		prefs = getSharedPreferences("motocitizen.startup", MODE_PRIVATE);
		// prefs.edit().clear().commit();
		props = new Props();
		new MCAccidents();
		new OSMMap();
		new SettingsMenu();
		new SmallSettingsMenu();
		if (MCAccidents.auth.isFirstRun()) {
			Show.show(R.id.main_frame, R.id.first_auth_screen);
		} else {
			Show.show(R.id.main_frame, R.id.main_frame_applications);
		}
		new GcmBroadcastReceiver();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MCLocation.sleep();

	}

	@Override
	protected void onResume() {
		super.onResume();
		MCLocation.wakeup();
		catchIntent();
		context = this;
		MCAccidents.refresh();
		int id = prefs.getInt("mc.show.details", 0);
		if( id != 0){
			MCAccidents.toDetails(id);
			prefs.edit().putInt("mc.show.details", 0).commit();
		};
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	public boolean onKeyUp(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			SmallSettingsMenu.popupBL.show();
			Keyboard.hide();
			return true;
		case KeyEvent.KEYCODE_BACK:
			Show.showLast();
			Keyboard.hide();
			return true;
		}
		return super.onKeyUp(keycode, e);
	}

	private void catchIntent() {
		Intent intent = getIntent();
		if (intent != null) {
			String text = intent.getStringExtra("text");
			if (text != null) {
			}
		}
	}
}

package motocitizen.startup;

import motocitizen.core.Applications;
import motocitizen.core.InitializeAll;
import motocitizen.core.Tasks;
import motocitizen.core.settings.SettingsMenu;
import motocitizen.main.R;
import motocitizen.utils.Const;
import motocitizen.utils.Props;
import motocitizen.utils.Show;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Window;

public class Startup extends Activity {
	public static Tasks tasks;
	public static Applications applications;
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
		Show.last = this.findViewById(R.id.main_frame_applications).getId();
		Show.lastParent = this.findViewById(R.id.main_frame).getId();

		prefs = getSharedPreferences("motocitizen.startup", MODE_PRIVATE);
		// prefs.edit().clear().commit();
		prefs.edit().putString("backButton", "");
		props = new Props();
		tasks = new Tasks();
		applications = new Applications();
		InitializeAll.init();
		new SettingsMenu();
		new SmallSettingsMenu();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		tasks.allSleep();
	}

	@Override
	protected void onResume() {
		super.onResume();
		tasks.allWakeUp();
	}

	@Override
	public boolean onKeyUp(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			SmallSettingsMenu.popupBL.show();
			return true;
		case KeyEvent.KEYCODE_BACK:
			String backButtonClass = prefs.getString("backButton", "");
			if (!backButtonClass.equals("")) {
				try {
					Class<?> c = Class.forName(backButtonClass);
					c.getDeclaredMethod("backButton").invoke(null);
					return true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return super.onKeyUp(keycode, e);
	}
}

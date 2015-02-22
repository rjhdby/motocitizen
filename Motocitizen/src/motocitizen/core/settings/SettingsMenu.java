package motocitizen.core.settings;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Keyboard;
import motocitizen.utils.Show;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SettingsMenu {
	private static final Activity act = (Activity) Startup.context;
	private static final LinearLayout includeArea = (LinearLayout) act.findViewById(R.id.settings_include_area);
	private static String previsionBack;
	private static final SharedPreferences prefs = Startup.prefs;

	public SettingsMenu() {
		addListeners();
		setValues();
	}

	public static void open() {
		toSettings();
		setValues();
	}

	public static void backButton() {
		cancel();
	}

	public static void cancel() {
		for (String key : prefs.getAll().keySet()) {
			View v = includeArea.findViewWithTag(key);
			if (v instanceof EditText) {
				Keyboard.hide(v);
			}
		}
		Startup.prefs.edit().putString("backButton", previsionBack).commit();
		Show.show(R.id.main_frame_applications);
	}

	private static void toSettings() {
		Show.show(R.id.main_frame_settings);
	}

	private static void addListeners() {
		Button confirm = (Button) act.findViewById(R.id.settings_confirm_button);
		confirm.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				submit();
			}
		});
		Button cancel = (Button) act.findViewById(R.id.settings_cancel_button);
		cancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				cancel();
			}
		});
	}

	private static void setValues() {
		for (String key : prefs.getAll().keySet()) {
			Log.d("PREFS", key);
			View v = includeArea.findViewWithTag(key);
			if (v != null) {
				if (v instanceof CheckBox) {
					((CheckBox) v).setChecked(prefs.getBoolean(key, true));
				} else if (v instanceof EditText) {
					EditText e = (EditText) v;
					if (e.getInputType() == InputType.TYPE_CLASS_NUMBER) {
						e.setText(String.valueOf(prefs.getInt(key, 0)));
					} else {
						e.setText(prefs.getString(key, ""));
					}
				}
			}
		}
	}

	private static void submit() {
		for (String key : prefs.getAll().keySet()) {
			View v = includeArea.findViewWithTag(key);
			if (v != null) {
				if (v instanceof CheckBox) {
					prefs.edit().putBoolean(key, ((CheckBox) v).isChecked()).commit();
				} else if (v instanceof EditText) {
					EditText e = (EditText) v;
					if (e.getInputType() == InputType.TYPE_CLASS_NUMBER) {
						prefs.edit().putInt(key, Integer.parseInt(e.getText().toString())).commit();
					} else {
						prefs.edit().putString(key, e.getText().toString()).commit();
					}
				}
			}
		}
		cancel();
	}
}

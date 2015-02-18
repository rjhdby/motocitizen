package motocitizen.app.mc;

import java.util.Properties;

import motocitizen.app.mc.Listiners.MCListeners;
import motocitizen.app.mc.objects.MCButtons;
import motocitizen.app.mc.user.MCAuth;
import motocitizen.app.mc.user.MCRole;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Inflate;
import motocitizen.utils.Props;
import motocitizen.utils.Show;
import android.app.Activity;
import android.view.View;

public class MCAccidentsInit {
	private static Activity act;
	public static String previsionBack;

	public MCAccidentsInit() {
		act = (Activity) Startup.context;
		deploySettings();
		deployScreens();
		MCAuth.setAccess();
		new MCAccidents();
		addListeners();
		MCAuth.checkFirstAuth();
		setupRole();
	}

	private void deploySettings() {
		Properties props = Props.readAssets("mcaccidents.properties");
		for (Object key : props.keySet()) {
			String value = (String) props.get(key);
			if (!Startup.prefs.contains((String) key)) {
				if (value.equals("true") || value.equals("false")) {
					Startup.prefs.edit().putBoolean((String) key, Boolean.parseBoolean(value)).commit();
				} else {
					try {
						Integer pref = Integer.parseInt(value);
						Startup.prefs.edit().putInt((String) key, pref).commit();
					} catch (NumberFormatException e) {
						Startup.prefs.edit().putString((String) key, value).commit();
					}
				}
			}
		}
	}

	private void addListeners() {
		MCButtons.authButton.setOnClickListener(MCListeners.authButtonListener);
		MCButtons.authConfirmButton.setOnClickListener(MCListeners.authConfirmListener);
		MCButtons.authCancelButton.setOnClickListener(MCListeners.authCancelListener);
		MCButtons.dialButton.setOnClickListener(MCListeners.dialButtonListener);
		MCButtons.createAccButton.setOnClickListener(MCListeners.createAccButtonListener);
		MCButtons.newMessageButton.setOnClickListener(MCListeners.newMessageButtonListener);
		MCButtons.firstLoginButton.setOnClickListener(MCListeners.firstloginButtonListener);
		MCButtons.anonimButton.setOnClickListener(MCListeners.anonimButtonListener);
		MCButtons.authAnonimCheckBox.setOnCheckedChangeListener(MCListeners.authAnonimCheckBoxListener);
	}

	private void deployScreens() {
		Inflate.add(R.id.settings_include_area, R.layout.mc_acc_settings);
		Inflate.add(R.layout.mc_auth);
		Inflate.add(R.layout.mc_auth_first_check);
		Inflate.add(R.layout.mc_app_create_point);
		Inflate.add(R.layout.mc_select_sound);
	}

	private void setupRole() {
		if (!MCRole.isStandart()) {
			((View) act.findViewById(R.id.mc_new_message_area)).setVisibility(View.INVISIBLE);
		}
	}

	public static void exit() {
		Startup.prefs.edit().putString("backButton", previsionBack).commit();
		Show.showLast();
	}

	public static void backButton() {
		exit();
	}
}

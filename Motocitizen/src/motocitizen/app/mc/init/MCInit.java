package motocitizen.app.mc.init;

import java.util.Properties;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import motocitizen.app.mc.listeners.MCListeners;
import motocitizen.app.mc.objects.MCButtons;
import motocitizen.app.mc.user.MCAuth;
import motocitizen.app.mc.user.MCRole;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Inflate;
import motocitizen.utils.Props;
import motocitizen.utils.Text;

public class MCInit {
	public static void readProperties() {
		Properties props = Props.readAssets("mcaccidents.properties");
		for (Object key : props.keySet()) {
			String value = (String) props.get(key);
			Log.d((String) key,(String) props.get(key));
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

	public static void inflateViews() {
		Inflate.add(R.id.settings_include_area, R.layout.mc_acc_settings);
		Inflate.add(R.layout.mc_auth);
		Inflate.add(R.layout.mc_auth_first_check);
		Inflate.add(R.layout.mc_app_create_point);
		Inflate.add(R.layout.mc_select_sound);
	}

	public static void addListeners() {
		MCButtons.selectSoundCancelButton.setOnClickListener(MCListeners.selectSoundCancelButton);
		MCButtons.authConfirmButton.setOnClickListener(MCListeners.authConfirmListener);
		MCButtons.authButton.setOnClickListener(MCListeners.authButtonListener);
		MCButtons.authCancelButton.setOnClickListener(MCListeners.authCancelListener);
		MCButtons.dialButton.setOnClickListener(MCListeners.dialButtonListener);
		MCButtons.createAccButton.setOnClickListener(MCListeners.createAccButtonListener);
		MCButtons.newMessageButton.setOnClickListener(MCListeners.newMessageButtonListener);
		MCButtons.firstLoginButton.setOnClickListener(MCListeners.firstloginButtonListener);
		MCButtons.anonimButton.setOnClickListener(MCListeners.anonimButtonListener);
		MCButtons.authAnonimCheckBox.setOnCheckedChangeListener(MCListeners.authAnonimCheckBoxListener);
		MCButtons.selectSoundButton.setOnClickListener(MCListeners.selectSoundButtonListener);
		MCButtons.selectSoundConfirmButton.setOnClickListener(MCListeners.selectSoundConfirmListener);
		
	}
	
	public static void setupAccess(MCAuth auth) {
		Activity act = (Activity) Startup.context;
		View newMessageArea = (View) act.findViewById(R.id.mc_new_message_area);
		View loginField = Const.act.findViewById(R.id.mc_auth_login);
		View passwordField = Const.act.findViewById(R.id.mc_auth_password);
		CheckBox anonimCheckBox = ((CheckBox) Const.act.findViewById(R.id.mc_auth_anonim));
		ImageButton createButton = (ImageButton) Const.act.findViewById(R.id.mc_add_point_button);
		
		if (MCRole.isStandart()) {
			newMessageArea.setVisibility(View.VISIBLE);
			createButton.setVisibility(View.VISIBLE);
		} else {
			newMessageArea.setVisibility(View.INVISIBLE);
			createButton.setVisibility(View.INVISIBLE);
		}
		
		if (auth.anonim) {
			anonimCheckBox.setChecked(true);
			loginField.setVisibility(View.INVISIBLE);
			passwordField.setVisibility(View.INVISIBLE);
			auth.reset();
		} else {
			anonimCheckBox.setChecked(false);
			loginField.setVisibility(View.VISIBLE);
			passwordField.setVisibility(View.VISIBLE);
		}
	}
	public static void setupValues(MCAuth auth){
		Text.set(R.id.value_mcaccidents_auth_name, auth.name);
	}
}

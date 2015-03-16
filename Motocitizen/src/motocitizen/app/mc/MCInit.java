package motocitizen.app.mc;

import java.util.Properties;

import motocitizen.app.mc.user.MCAuth;
import motocitizen.app.mc.user.MCRole;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Props;
import motocitizen.utils.Text;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

public class MCInit {
	public static void readProperties() {
		Properties props = Props.readAssets("mcaccidents.properties");
		for (Object key : props.keySet()) {
			String value = (String) props.get(key);
//			Log.d((String) key,(String) props.get(key));
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
		MCObjects.accDetailsView.setTranslationX(Const.width);
		MCObjects.mapContainer.setTranslationX(Const.width);
	}

	public static void addListeners() {
		MCObjects.authConfirmButton.setOnClickListener(MCListeners.authConfirmListener);
		MCObjects.authButton.setOnClickListener(MCListeners.authButtonListener);
		MCObjects.authCancelButton.setOnClickListener(MCListeners.authCancelListener);
		MCObjects.dialButton.setOnClickListener(MCListeners.dialButtonListener);
		MCObjects.createAccButton.setOnClickListener(MCListeners.createAccButtonListener);
		MCObjects.firstLoginButton.setOnClickListener(MCListeners.firstloginButtonListener);
		MCObjects.anonimButton.setOnClickListener(MCListeners.anonimButtonListener);
		MCObjects.authAnonimCheckBox.setOnCheckedChangeListener(MCListeners.authAnonimCheckBoxListener);
		MCObjects.selectSoundButton.setOnClickListener(MCListeners.selectSoundButtonListener);
		MCObjects.mainTabsGroup.setOnCheckedChangeListener(MCListeners.mainTabsListener);
		MCObjects.newMessageButton.setOnClickListener(MCListeners.newMessageButtonListener);
		MCObjects.mcDetTabsGroup.setOnCheckedChangeListener(MCListeners.accDetTabsListener);
		MCObjects.onwayButton.setOnClickListener(MCListeners.onwayButtonListener);
		((EditText)	MCObjects.mcNewMessageText).addTextChangedListener(MCListeners.mcNewMessageTextListener);
	}
	
	public static void setupAccess(Context context, MCAuth auth) {
		View newMessageArea = (View) ((Activity) context).findViewById(R.id.mc_new_message_area);
		View loginField = ((Activity) context).findViewById(R.id.mc_auth_login);
		View passwordField = ((Activity) context).findViewById(R.id.mc_auth_password);
		CheckBox anonimCheckBox = ((CheckBox) ((Activity) context).findViewById(R.id.mc_auth_anonim));
		ImageButton createButton = (ImageButton) ((Activity) context).findViewById(R.id.mc_add_point_button);
		
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

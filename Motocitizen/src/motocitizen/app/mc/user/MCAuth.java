package motocitizen.app.mc.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Show;
import motocitizen.utils.Text;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

public class MCAuth {
	private static String login;
	private static String passwordHash;
	public static Map<String, String> user;

	public static boolean auth() {
		user = new HashMap<String, String>();
		login = makeLogin();
		passwordHash = makePassHash();
		if (!authCall()) {
			user.put("name", "");
			return false;
		}
		return true;
	}

	public static String getPassHash() {
		return passwordHash;
	}

	public static String getLogin() {
		return Startup.prefs.getString("mc.login", "");
	}

	private static String makePassHash() {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String pass = Startup.prefs.getString("mc.password", "");
			if (pass.equals("")) {
				Startup.prefs.edit().putString("mc.password", "").commit();
			}
			md.update(pass.getBytes());
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String makeLogin() {
		String login = Startup.prefs.getString("mc.login", "");
		if (login.equals("")) {
			Startup.prefs.edit().putString("mc.login", "").commit();
		}
		return login;
	}

	private static boolean authCall() {
		String ident = ((TelephonyManager) Startup.context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		Map<String, String> post = new HashMap<String, String>();
		post.put("ident", ident);
		post.put("login", login);
		post.put("passwordHash", passwordHash);
		JSONObject json = new JSONCall("mcaccidents", "auth").request(post);
		parseJSON(json);
		// setRoleA
		if (user.get("name") == null) {
			Startup.prefs.edit().putString("mc.name", "").commit();
			return false;
		} else {
			Startup.prefs.edit().putString("mc.name", user.get("name")).commit();
			return true;
		}
	}

	private static void parseJSON(JSONObject json) {
		Iterator<String> keys = json.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			try {
				user.put(key, json.getString(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isAnonimus() {
		return Startup.prefs.getBoolean("mc.anonim", false);
	}

	public static void setAccess() {
		auth();
		View loginField = Const.act.findViewById(R.id.mc_auth_login);
		View passwordField = Const.act.findViewById(R.id.mc_auth_password);
		View newMessage = Const.act.findViewById(R.id.mc_new_message_area);
		CheckBox anonim = ((CheckBox) Const.act.findViewById(R.id.mc_auth_anonim));
		ImageButton create = (ImageButton) Const.act.findViewById(R.id.mc_add_point_button);
		if (isAnonimus()) {
			anonim.setChecked(true);
			loginField.setVisibility(View.INVISIBLE);
			passwordField.setVisibility(View.INVISIBLE);
			user.put("role", "");
		} else {
			anonim.setChecked(false);
			loginField.setVisibility(View.VISIBLE);
			passwordField.setVisibility(View.VISIBLE);
			Text.set(R.id.value_mcaccidents_auth_name, Startup.prefs.getString("mc.name", ""));
		}

		if (MCRole.isStandart()) {
			newMessage.setVisibility(View.VISIBLE);
			create.setVisibility(View.VISIBLE);
		} else {
			newMessage.setVisibility(View.INVISIBLE);
			create.setVisibility(View.INVISIBLE);
		}
	}

	public static void checkFirstAuth() {
		if (!isAnonimus() && MCAuth.user.get("name").equals("")) {
			Startup.prefs.edit().putString("backButton", "motocitizen.app.mc.MCAuth").commit();
			Show.last = R.id.first_auth_screen;
		}
	}

	public static void backButton() {
	}
}

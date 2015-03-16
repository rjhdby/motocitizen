package motocitizen.app.mc.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Text;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CheckBox;

public class MCAuth {
	public String role;
	public String name;
	public String attr;
	public int id;

	public boolean authorized;
	public boolean anonim;

	public MCAuth() {
		reset();
		anonim = Startup.prefs.getBoolean("mc.anonim", false);
		if (!anonim) {
			auth();
		}
	}

	public void reset() {
		name = "";
		role = "";
		attr = "";
		id = 0;
		authorized = false;
	}

	public String getLogin() {
		return Startup.prefs.getString("mc.login", "");
	}

	public String makePassHash() {
		String hash = "";
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
			hash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}

	public boolean isFirstRun(){
		if(!anonim&&getLogin().equals("")){
			return true;
		}
		return false;
	}
	
	public void setAnonim(Context context, Boolean value){
		anonim = value;
		Startup.prefs.edit().putBoolean("mc.anonim", value).commit();
		reset();
		setAccess(context);
	}
	
	public void auth() {
		String ident = ((TelephonyManager) Startup.context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		Map<String, String> post = new HashMap<String, String>();
		post.put("ident", ident);
		post.put("login", getLogin());
		post.put("passwordHash", makePassHash());
		JSONObject json = new JSONCall("mcaccidents", "auth").request(post);
		parseJSON(json);
		Startup.prefs.edit().putString("mc.name", name).commit();
	}

	private void parseJSON(JSONObject json) {
		try {
			name = json.getString("name");
			role = json.getString("role");
			attr = json.getString("attr");
			id = json.getInt("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void setAccess(Context context) {
		//auth();
		View loginField = ((Activity) context).findViewById(R.id.mc_auth_login);
		View passwordField = ((Activity) context).findViewById(R.id.mc_auth_password);
		CheckBox anonimCheckBox = ((CheckBox) ((Activity) context).findViewById(R.id.mc_auth_anonim));
		if (anonim) {
			anonimCheckBox.setChecked(true);
			loginField.setVisibility(View.INVISIBLE);
			passwordField.setVisibility(View.INVISIBLE);
			role = "";
		} else {
			anonimCheckBox.setChecked(false);
			loginField.setVisibility(View.VISIBLE);
			passwordField.setVisibility(View.VISIBLE);
			Text.set(R.id.value_mcaccidents_auth_name, Startup.prefs.getString("mc.name", ""));
		}
	}
}

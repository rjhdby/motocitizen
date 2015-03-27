package motocitizen.app.mc.user;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Text;

public class MCAuth {
    public String role;
    public String name;
    public int id;
    public boolean anonim;

    public MCAuth() {
        reset();
        anonim = Startup.prefsDef.getBoolean("mc.anonim", false);
        if (!anonim) {
            auth();
        }
    }

    public void reset() {
        name = "";
        role = "";
        id = 0;
    }

    public String getLogin() {
        return Startup.prefsDef.getString("mc.login", "");
    }

    public String makePassHash() {
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String pass = Startup.prefsDef.getString("mc.password", "");
            if (pass.equals("")) {
                Startup.prefsDef.edit().putString("mc.password", "").commit();
            }
            md.update(pass.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public boolean isFirstRun() {
        return !anonim && getLogin().equals("");
    }

    public void setAnonim(Context context, Boolean value) {
        anonim = value;
        Startup.prefsDef.edit().putBoolean("mc.anonim", value).commit();
        reset();
        setAccess(context);
    }

    public void auth() {
        String ident = ((TelephonyManager) Startup.context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        Map<String, String> post = new HashMap<>();
        post.put("ident", ident);
        post.put("login", getLogin());
        post.put("passwordHash", makePassHash());
        JSONObject json = new JSONCall("mcaccidents", "auth").request(post);
        parseJSON(json);
        Startup.prefsDef.edit().putString("mc.name", name).commit();
    }

    private void parseJSON(JSONObject json) {
        try {
            name = json.getString("name");
            role = json.getString("role");
            id = json.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setAccess(Context context) {
        //auth();
//        View loginField = ((Activity) context).findViewById(R.id.mc_auth_login);
//        View passwordField = ((Activity) context).findViewById(R.id.mc_auth_password);
//        CheckBox anonimCheckBox = ((CheckBox) ((Activity) context).findViewById(R.id.mc_auth_anonim));
        if (anonim) {
//            anonimCheckBox.setChecked(true);
//            loginField.setVisibility(View.INVISIBLE);
//            passwordField.setVisibility(View.INVISIBLE);
            role = "";
        } else {
//            anonimCheckBox.setChecked(false);
//            loginField.setVisibility(View.VISIBLE);
//            passwordField.setVisibility(View.VISIBLE);
//            Text.set(R.id.value_mcaccidents_auth_name, Startup.prefsDef.getString("mc.name", ""));
        }
    }
}

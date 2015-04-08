package motocitizen.app.mc.user;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;

public class MCAuth {
    public String role;
    public String name;
    public int id;
    public boolean anonim;

    public MCAuth() {
        reset();
        anonim = Startup.prefs.getBoolean("mc.anonim", false);
        if (!anonim) {
            auth(Startup.prefs.getString("mc.login", ""), Startup.prefs.getString("mc.password", ""));
        }
    }

    public void reset() {
        name = "";
        role = "";
        id = 0;
    }

    public String getLogin() {
        return Startup.prefs.getString("mc.login", "");
    }

    public String makePassHash(String pass) {
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
//          String pass = Startup.prefsDef.getString("mc.password", "");

//Это зачем? ----
//            if (pass.equals("")) {
//                Startup.prefsDef.edit().putString("mc.password", "").commit();
//            }
//Это зачем? ----
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

    // Ну нету в java параметров по-умолчанию.
    public String makePassHash() {
        return makePassHash(Startup.prefs.getString("mc.password", ""));
    }

    public boolean isFirstRun() {
        return !anonim && getLogin().equals("");
    }

    public void setAnonim(Context context, Boolean value) {
        anonim = value;
        Startup.prefs.edit().putBoolean("mc.anonim", value).commit();
        reset();
        setAccess(context);
    }

    public Boolean auth(String login, String password) {
        if(Startup.isOnline()) {
            String ident = ((TelephonyManager) Startup.context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            Map<String, String> post = new HashMap<>();
            post.put("ident", ident);
            post.put("login", login);
            post.put("passwordHash", makePassHash(password));
            JSONObject json = new JSONCall("mcaccidents", "auth").request(post);
            parseJSON(json);
            if (name.length() > 0) {
                Startup.prefs.edit().putString("mc.name", name).commit();
                Startup.prefs.edit().putString("mc.login", login).commit();
                Startup.prefs.edit().putString("mc.password", password).commit();
                return true;
            } else {
                return false;
            }
        } else {
            //TODO Перенести в ресурсы
            Toast.makeText(Startup.context, "Авторизация не возможна, пожалуйста, проверьте доступность Internet.", Toast.LENGTH_LONG).show();
            return false;
        }
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

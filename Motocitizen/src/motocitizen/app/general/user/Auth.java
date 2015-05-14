package motocitizen.app.general.user;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import motocitizen.Activity.AuthActivity;
import motocitizen.network.JSONCall;
import motocitizen.startup.MyPreferences;
import motocitizen.startup.Startup;

public class Auth {
    private String role;
    private String name;
    private int id;
    private MyPreferences prefs;
    private boolean isAuthorized = false;

    public Auth(Context context) {
        prefs = new MyPreferences(context);
        reset();

        if (!prefs.isAnonim() ) {
          if(!prefs.getLogin().isEmpty()) {
              auth(context, prefs.getLogin(), prefs.getPassword());
          } else {
              Intent i = new Intent(Startup.context, AuthActivity.class);
              Startup.context.startActivity(i);
          }
        }
    }

    void reset() {
        name = "";
        role = "";
        id = 0;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public int getID() {
        return id;
    }

    public String getLogin() {
        return prefs.getLogin();
    }

    String makePassHash(String pass) {
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
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
        return makePassHash(prefs.getPassword());
    }

    public boolean isAnonim() {
        return prefs.isAnonim();
    }

    public Boolean auth(Context context, String login, String password) {
        if(Startup.isOnline()) {
            String ident = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            Map<String, String> post = new HashMap<>();
            post.put("ident", ident);
            post.put("login", login);
            post.put("passwordHash", makePassHash(password));
            JSONObject json = new JSONCall(context, "mcaccidents", "auth").request(post);
            parseJSON(json);
            if (name.length() > 0) {
                prefs.setLogin(login);
                prefs.setPassword(password);
                prefs.setAnonim(false);
                isAuthorized = true;
            } else {
                isAuthorized = false;
            }
        } else {
            //TODO Перенести в ресурсы
            Toast.makeText(context, "Авторизация не возможна, пожалуйста, проверьте доступность Internet.", Toast.LENGTH_LONG).show();
            isAuthorized = false;
        }
        return isAuthorized;
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

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void logoff() {
        reset();
        this.isAuthorized = false;
    }
}

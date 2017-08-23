package motocitizen.user;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import motocitizen.network.CoreRequest;
import motocitizen.network.requests.AuthRequest;
import motocitizen.utils.Preferences;

/*
fun auth(login: String, password: String, callback: CoreRequest.RequestResultCallback) {
        preferences!!.password = password
        AuthRequest(login, getPassHash(password), object : CoreRequest.RequestResultCallback {
            override fun call(response: JSONObject) {
                parseAuthResult(response, login)
                callback.call(response)
            }
        })
    }
 */
//todo object
public class User {
    private Role    role         = Role.RO;
    private String  name         = "";
    private int     id           = 0;
    private boolean isAuthorized = false;

    private Preferences preferences;

    private User() {}

    public static User getInstance() {
        if (Holder.instance == null) {
            Holder.instance = new User();
            Holder.instance.preferences = Preferences.INSTANCE;
        }
        return Holder.instance;
    }

    public static User dirtyRead() {
        return Holder.instance == null ? new User() : Holder.instance;
    }

    private static class Holder {
        private static User instance;
    }

    public void auth(String login, String password, CoreRequest.RequestResultCallback callback) {
        preferences.setPassword(password);
        new AuthRequest(login, getPassHash(password), response -> {
            parseAuthResult(response, login);
            callback.call(response);
        });
    }

    // {"r":{"id":"8","r":3},"e":{}}
    private void parseAuthResult(JSONObject response, String login) {
        isAuthorized = false;
        try {
            JSONObject result = response.getJSONObject("r");
            id = Integer.parseInt(result.getString("id"));
            name = login;
            role = Role.Companion.parse(result.getInt("r"));
            preferences.setLogin(name);
            preferences.setAnonim(false);
            isAuthorized = true;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("AUTH ERROR", response.toString());
        }
    }

    public int getId() {return id;}

    public String getPassHash() {
        return getPassHash(preferences.getPassword());
    }

    private String getPassHash(String pass) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pass.getBytes());
            byte[] digest = md.digest();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void logoff() {
        name = "";
        role = Role.RO;
        id = 0;
        isAuthorized = false;
    }

    public String getName() {
        return name;
    }

    public boolean isAuthorized() {return isAuthorized;}

    public boolean isModerator()  {return role.isModerator();}

    public boolean isStandard()   {return role.isStandard();}

    public String getRoleName()   {return role.getText();}
}

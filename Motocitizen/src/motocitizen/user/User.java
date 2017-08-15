package motocitizen.user;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import motocitizen.network.ApiRequest;
import motocitizen.network.requests.AuthRequest;
import motocitizen.utils.Preferences;

public class User {
    private Role    role         = Role.RO;
    private String  name         = "";
    private int     id           = 0;
    private boolean isAuthorized = false;

    private Preferences preferences;

    private User() {}

    public static User getInstance(Context context) {
        if (Holder.instance == null) {
            Holder.instance = new User();
            Holder.instance.preferences = Preferences.Companion.getInstance(context);
        }
        return Holder.instance;
    }

    public static User dirtyRead() {
        return Holder.instance == null ? new User() : Holder.instance;
    }

    private static class Holder {
        private static User instance;
    }

    public void auth(String login, String password, ApiRequest.RequestResultCallback callback) {
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
//    private void parseAuthResult(JSONObject response) {
//        isAuthorized = false;
//        if (!response.has("id")) {
//            Log.d("AUTH ERROR", response.toString());
//            return;
//        }
//        try {
//            name = response.getString("name");
//            if (name.length() == 0) return;
//            role = Role.Companion.parse(response.getString("role"));
//            id = Integer.parseInt(response.getString("id"));
//            preferences.setLogin(name);
//            preferences.setAnonim(false);
//            isAuthorized = true;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public int getId() {return id;}

    public String getPassHash() {
        return getPassHash(preferences.getPassword());
    }

    public String getPassHash(String pass) {
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

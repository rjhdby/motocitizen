package motocitizen.user;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import motocitizen.network.requests.AuthRequest;
import motocitizen.utils.Preferences;

public class Auth {
    private Role    role;
    private String  name;
    private int     id;
    private boolean isAuthorized;

    {
        role = Role.RO;
        name = "";
        id = 0;
        isAuthorized = false;
    }

    private Auth() {

    }

    public static void init(){
        initialAuth();
    }


    private static void initialAuth() {
        if (Preferences.isAnonim()) return;
        if (Preferences.getLogin().equals("")) return;
        try {
            getInstance().auth(Preferences.getLogin(), Preferences.getPassword());
        } catch (Error e) {
            Log.d("AUTH ERROR", e.getLocalizedMessage());
        }
    }

    private static class Holder {
        private final static Auth instance = new Auth();
    }

    public static Auth getInstance() {
        return Holder.instance;
    }

    public static String makePassHash(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pass.getBytes());
            byte[]        digest = md.digest();
            StringBuilder sb     = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Boolean auth(String login, String password) throws Error {
        AuthRequest auth = new AuthRequest();
        auth.setLogin(login);
        auth.setPassword(password);
        JSONObject result = auth.execute();
        isAuthorized = false;
        if (auth.error(result)) {
            String error = auth.getError(result);
            throw new Error(error);
        }
        try {
            name = result.getString("name");
            role = Role.parse(result.getString("role"));
            id = Integer.parseInt(result.getString("id"));
            Preferences.setUserId(id);
            Preferences.setUserName(name);
            Preferences.setUserRole(role.getCode());
            if (name.length() > 0) {
                Preferences.setLogin(login);
                Preferences.setPassword(password);
                Preferences.setAnonim(false);
                isAuthorized = true;
            }
        } catch (JSONException e) {
            return false;
        }
        return isAuthorized;
    }

    public Role getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    public String makePassHash() {
        return makePassHash(Preferences.getPassword());
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void logoff() {
        name = "";
        role = Role.RO;
        id = 0;
        this.isAuthorized = false;
    }
}

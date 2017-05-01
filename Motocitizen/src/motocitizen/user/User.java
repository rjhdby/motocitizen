package motocitizen.user;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import motocitizen.network.requests.AuthRequest;
import motocitizen.utils.Preferences;

public class User {
    private Role    role         = Role.RO;
    private String  name         = "";
    private int     id           = 0;
    private boolean isAuthorized = false;

    private User()                   {}

    public static User getInstance() {return Holder.instance;}

    public static void init() {
        if (Preferences.getInstance().isAnonim()) return;
        if (Preferences.getInstance().getLogin().equals("")) return;
        getInstance().auth(Preferences.getInstance().getLogin(), Preferences.getInstance().getPassword());
    }

    private static class Holder {
        private final static User instance = new User();
    }

    public boolean auth(String login, String password) {
        AuthRequest auth   = new AuthRequest(login, password);
        JSONObject  result = auth.execute();
        isAuthorized = false;
        if (auth.error(result)) {
            Log.d("AUTH ERROR", auth.getError(result));
            return false;
        }
        try {
            name = result.getString("name");
            if (name.length() == 0) return false;

            role = Role.parse(result.getString("role"));
            id = Integer.parseInt(result.getString("id"));
            Preferences.getInstance().setUserId(id);
            Preferences.getInstance().setUserName(name);
            Preferences.getInstance().setUserRole(role.getCode());
            Preferences.getInstance().setLogin(login);
            Preferences.getInstance().setPassword(password);
            Preferences.getInstance().setAnonim(false);
            isAuthorized = true;
        } catch (JSONException ignore) {}
        return isAuthorized;
    }

    public int getId() {return id;}

    public String getPassHash() {
        return getPassHash(Preferences.getInstance().getPassword());
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

    public String getRoleName()   {return role.getName();}
}

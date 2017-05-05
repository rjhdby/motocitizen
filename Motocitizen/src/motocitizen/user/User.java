package motocitizen.user;

import android.content.Context;
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

    private Preferences preferences;

    private User() {}

    public static User getInstance(Context context) {
        if (Holder.instance == null) {
            Holder.instance = new User();
            Holder.instance.preferences = Preferences.Companion.getInstance(context);
            if (!Preferences.Companion.getInstance(context).getAnonim() && !Preferences.Companion.getInstance(context).getLogin().equals(""))
                Holder.instance.auth(Preferences.Companion.getInstance(context).getLogin(), Preferences.Companion.getInstance(context).getPassword());
        }
        return Holder.instance;
    }

    public static User dirtyRead() {
        return Holder.instance == null ? new User() : Holder.instance;
    }

    private static class Holder {
        private static User instance;
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

            role = Role.Companion.parse(result.getString("role"));
            id = Integer.parseInt(result.getString("id"));
            preferences.setLogin(login);
            preferences.setPassword(password);
            preferences.setAnonim(false);
            isAuthorized = true;
        } catch (JSONException ignore) {}
        return isAuthorized;
    }

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

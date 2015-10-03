package motocitizen.app.general.user;

import android.content.Intent;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import motocitizen.Activity.AuthActivity;
import motocitizen.MyApp;
import motocitizen.main.R;
import motocitizen.network.requests.AuthRequest;
import motocitizen.utils.Preferences;

public class Auth {
    private Role    role;
    private String  name;
    private String  login;
    private String  password;
    private int     id;
    private boolean isAuthorized;

    {
        role = Role.RO;
        name = "";
        id = 0;
        isAuthorized = false;
    }

    public Auth() {
        if (Preferences.isAnonim()) return;
        login = Preferences.getLogin();
        password = Preferences.getPassword();
        if (!auth(login, password)) showLogin();
    }

    public static String makePassHash(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pass.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Boolean auth(String login, String password) {
        this.password = password;
        this.login = login;
        AuthRequest auth = new AuthRequest();
        auth.setLogin(login);
        auth.setPassword(password);
        JSONObject result = auth.execute();
        isAuthorized = false;
        if (auth.error(result)) {
            String error = auth.getError(result);
            message(error);
            return false;
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
            message(MyApp.getAppContext().getString(R.string.unknown_error));
        }
        return isAuthorized;
    }

    private void showLogin() {
        Intent i = new Intent(MyApp.getAppContext(), AuthActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApp.getAppContext().startActivity(i);
    }

    private void message(String text) {
        Toast.makeText(MyApp.getAppContext(), text, Toast.LENGTH_LONG).show();
    }

    public Role getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return Preferences.getLogin();
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

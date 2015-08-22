package motocitizen.app.general.user;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import motocitizen.Activity.AuthActivity;
import motocitizen.main.R;
import motocitizen.network.requests.AuthRequest;
import motocitizen.startup.Preferences;

public class Auth {
    private final Context context;
    private String role;
    private String name;
    private int    id;
    private boolean isAuthorized = false;
    private       String  login;
    private       String  password;

    public Auth(Context context) {
        this.context = context;
        reset();

        if (!Preferences.isAnonim()) {
            if (!Preferences.getLogin().isEmpty()) {
                login = Preferences.getLogin();
                password = Preferences.getPassword();
                if (!auth(context, login, password)) {
                    showLogin(context);
                }
            } else {
                showLogin(context);
            }
        }
    }

    private void reset() {
        name = "";
        role = "";
        id = 0;
    }

    public Boolean auth(Context context, String login, String password) {
        this.password = password;
        this.login = login;
        AuthRequest auth = new AuthRequest(context);
        auth.setLogin(login);
        auth.setPassword(password);
        JSONObject result = auth.execute();
        if (auth.error(result)) {
            String error = auth.getError(result);
            message(error);
            isAuthorized = false;
        } else {
            try {
                name = result.getString("name");
                role = result.getString("role");
                id = Integer.parseInt(result.getString("id"));
                Preferences.setUserId(id);
                Preferences.setUserName(name);
                Preferences.setUserRole(role);
                if (name.length() > 0) {
                    Preferences.setLogin(login);
                    Preferences.setPassword(password);
                    Preferences.setAnonim(false);
                    isAuthorized = true;
                } else {
                    isAuthorized = false;
                }
            } catch (JSONException e) {
                message(context.getString(R.string.unknown_error));
                isAuthorized = false;
            }
        }
        return isAuthorized;
    }

    private void showLogin(Context context) {
        Intent i = new Intent(context, AuthActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void message(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public int getid() {
        return id;
    }

    public String getLogin() {
        return Preferences.getLogin();
    }

    // Ну нету в java параметров по-умолчанию.
    public String makePassHash() {
        return makePassHash(Preferences.getPassword());
    }

    public static String makePassHash(String pass) {
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

    public boolean isAnonim() {
        return Preferences.isAnonim();
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void logoff() {
        reset();
        this.isAuthorized = false;
    }
}

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
import motocitizen.startup.MyPreferences;

public class Auth {
    private       String        role;
    private       String        name;
    private       int           id;
    private final MyPreferences prefs;
    private boolean isAuthorized = false;
    private final Context context;
    private       String  login;
    private       String  password;

    public Auth(Context context) {
        this.context = context;
        prefs = new MyPreferences(context);
        reset();


        if (!prefs.isAnonim()) {
            if (!prefs.getLogin().isEmpty()) {
                login = prefs.getLogin();
                password = prefs.getPassword();
                if (!auth(context, login, password)) {
                    showLogin(context);
                }
            } else {
                showLogin(context);
            }
        }
    }

    private void showLogin(Context context) {
        Intent i = new Intent(context, AuthActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void reset() {
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

    // Ну нету в java параметров по-умолчанию.
    public String makePassHash() {
        return makePassHash(prefs.getPassword());
    }

    public boolean isAnonim() {
        return prefs.isAnonim();
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
            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            isAuthorized = false;
        } else {
            try {
                name = result.getString("name");
                role = result.getString("role");
                id = Integer.parseInt(result.getString("id"));
                if (name.length() > 0) {
                    prefs.setLogin(login);
                    prefs.setPassword(password);
                    prefs.setAnonim(false);
                    isAuthorized = true;
                } else {
                    isAuthorized = false;
                }
            } catch (JSONException e) {
                Toast.makeText(context, context.getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                isAuthorized = false;
            }
        }
        return isAuthorized;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void logoff() {
        reset();
        this.isAuthorized = false;
    }
}

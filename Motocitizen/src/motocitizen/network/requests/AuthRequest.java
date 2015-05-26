package motocitizen.network.requests;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.app.general.user.Auth;

public class AuthRequest extends HTTPClient {
    public AuthRequest(Context context) {
        this.context = context;
        String ident = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        post = new HashMap<>();
        post.put("ident", ident);
        post.put("calledMethod", "auth");
    }
    public void setLogin(String login){
        post.put("login", login);
    }
    public void setPassword(String password){
        post.put("passwordHash", Auth.makePassHash(password));
    }
    public JSONObject execute(){
        return super.request(post);
    }
}

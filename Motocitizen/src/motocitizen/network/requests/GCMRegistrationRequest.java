package motocitizen.network.requests;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

public class GCMRegistrationRequest extends HTTPClient {
    public GCMRegistrationRequest(Context context, String regId) {
        this.context = context;
        post = new HashMap<>();
        String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        post.put("owner_id", String.valueOf(AccidentsGeneral.auth.getID()));
        post.put("gcm_key", regId);
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("imei", imei);
        post.put("passhash", AccidentsGeneral.auth.makePassHash());
        post.put("calledMethod", "registerGCM");
        execute(post);
    }
}

package motocitizen.network.requests;

import android.content.Context;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

public class OnWayRequest extends HTTPClient {
    public OnWayRequest(Context context, int id) {
        this.context = context;
        post = new HashMap<>();
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("passhash", AccidentsGeneral.auth.makePassHash());
        post.put("id", String.valueOf(id));
        post.put("callMethod", "onway");
        execute(post);
    }
}

package motocitizen.network.requests;

import android.content.Context;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

/**
 * Created by elagin on 01.06.15.
 */
public class CancelOnWayRequest extends HTTPClient {
    public CancelOnWayRequest(AsyncTaskCompleteListener listener, Context context, int id) {
        this.listener = listener;
        this.context = context;
        post = new HashMap<>();
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("passhash", AccidentsGeneral.auth.makePassHash());
        post.put("id", String.valueOf(id));
        post.put("calledMethod", "cancelOnWay");
        execute(post);
    }
}

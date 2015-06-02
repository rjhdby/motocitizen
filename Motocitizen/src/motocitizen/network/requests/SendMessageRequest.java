package motocitizen.network.requests;

import android.content.Context;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

public class SendMessageRequest  extends HTTPClient {
    public SendMessageRequest(AsyncTaskCompleteListener listener, Context context, int id, String text) {
        this.listener = listener;
        this.context = context;
        post = new HashMap<>();
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("passhash", AccidentsGeneral.auth.makePassHash());
        post.put("id", String.valueOf(id));
        post.put("text", text);
        post.put("calledMethod", "message");
        execute(post);
    }
}

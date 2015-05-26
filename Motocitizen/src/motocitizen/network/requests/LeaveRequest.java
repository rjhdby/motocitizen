package motocitizen.network.requests;

import android.content.Context;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

public class LeaveRequest extends HTTPClient {
    public LeaveRequest (Context context, int id){
        this.context = context;
        post = new HashMap<>();
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("id", String.valueOf(id));
        post.put("callMethod", "leave");
        execute(post);
    }
}

package motocitizen.network.requests;

import android.content.Context;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

public class InplaceRequest extends HTTPClient {
    public InplaceRequest (Context context, int id){
        this.context = context;
        post = new HashMap<>();
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("id", String.valueOf(id));
        post.put("calledMethod", "inplace");
        execute(post);
    }
}

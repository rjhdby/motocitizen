package motocitizen.network.requests;

import android.content.Context;

import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

public class AccidentChangeState extends HTTPClient {
    public static final String ACTIVE = "acc_status_act";
    public static final String ENDED = "acc_status_end";
    public static final String HIDE = "acc_status_hide";
    int id;
    String state;
    public AccidentChangeState (Context context, int id, String state){
        this.context = context;
        this.id = id;
        this.state = state;
        post = new HashMap<>();
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("passhash", AccidentsGeneral.auth.makePassHash());
        post.put("state", state);
        post.put("id", String.valueOf(id));
        post.put("calledMethod", "changeState");
        execute(post);
    }
    @Override
    public void onPostExecute(JSONObject result){
        AccidentsGeneral.points.getPoint(id).setStatus(state);
        AccidentsGeneral.redraw(context);
    }
}

package motocitizen.network.requests;

import android.content.Context;
import android.location.Location;

import java.util.Date;
import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;
import motocitizen.main.R;
import motocitizen.utils.Const;

public class CreateAccidentRequest extends HTTPClient {
    public CreateAccidentRequest(AsyncTaskCompleteListener listener, Context context) {
        this.context = context;
        this.listener = listener;
        post.put("status", "acc_status_act");
        post = new HashMap<>();
        post.put("calledMethod", "createAcc");
        post.put("hint", context.getString(R.string.request_create_acc));
        post.put("owner_id", String.valueOf(AccidentsGeneral.auth.getID()));
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("passhash", AccidentsGeneral.auth.makePassHash());
    }

    public void execute() {
        super.execute(post);
    }

    public void setLocation(Location location) {
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lon", String.valueOf(location.getLongitude()));
    }

    public void setType(String type) {
        post.put("type", type);
    }

    public void setAddress(String address) {
        post.put("address", address);
    }

    public void setDescription(String description) {
        post.put("descr", description);
    }

    public void setCreated(Date created) {
        post.put("created", Const.dateFormat.format(created));
    }

    public void setMed(String med) {
        post.put("med", med);
    }

    public void setStatus(String status) {
        post.put("status", status);
    }
}

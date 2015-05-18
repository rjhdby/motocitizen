package motocitizen.app.general;

import android.content.Context;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import motocitizen.utils.Const;

public class AccidentVolunteer {
    public int id;
    public String name;
    private Status status;
    public Date time;

    public enum Status {
        INPLACE, LEAVE, ONWAY
    }

    public void setStatus(Status newStatus) {
        status = newStatus;
    }

    public void setStatus(String newStatus) {
        if(newStatus.equals("inplace"))
            status = Status.INPLACE;
        else if(newStatus.equals("leave"))
            status = Status.LEAVE;
        else if(newStatus.equals("onway"))
            status = Status.ONWAY;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isInplace() {
        return status == Status.INPLACE;
    }

    public boolean isLeave() {
        return status == Status.LEAVE;
    }

    public AccidentVolunteer(JSONObject json) throws JSONException {
        id = json.getInt("id");
        name = json.getString("name");
        setStatus(json.getString("status"));
        time = new Date(Long.parseLong(json.getString("uxtime"), 10) * 1000);
    }

    public AccidentVolunteer(int id, String name, Status status){
        this.id = id;
        this.name = name;
        this.status = status;
        this.time = new Date();
    }

    public TableRow createRow(Context context) {
        String type = ", выехал в ";
        if(isInplace()) {
            type = ", приехал в ";
        } else if(isLeave()){
            type = ", уехал в ";
        }
        Log.d("TYPE", type);
        Log.d("NAME", name);
        TableRow tr = new TableRow(context);
        TextView nameView = new TextView(tr.getContext());
        nameView.setText(name + type + Const.timeFormat.format(time.getTime()));
        tr.addView(nameView);
        return tr;
    }
}

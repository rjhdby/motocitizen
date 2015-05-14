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
    public String status;
    public Date time;

    public AccidentVolunteer(JSONObject json) throws JSONException {
        id = json.getInt("id");
        name = json.getString("name");
        status = json.getString("status");
        time = new Date(Long.parseLong(json.getString("uxtime"), 10) * 1000);
    }

    public AccidentVolunteer(int id, String name, String status){
        this.id = id;
        this.name = name;
        this.status = status;
        this.time = new Date();
    }

    public TableRow createRow(Context context) {
        String type = ", выехал в ";
        if(status.equals("inplace")){
            type = ", приехал в ";
        } else if(status.equals("leave")){
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

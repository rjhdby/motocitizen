package motocitizen.app.mc;

import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import motocitizen.utils.Const;

public class MCVolunteer {
    public int id;
    public String name;
    public String status;
    public Date time;

    public MCVolunteer(JSONObject json) throws JSONException {
        id = json.getInt("id");
        name = json.getString("name");
        status = json.getString("status");
        time = new Date(Long.parseLong(json.getString("uxtime"), 10)*1000);
    }

    public TableRow createRow(Context context) {
        TableRow tr = new TableRow(context);
        TextView nameView = new TextView(tr.getContext());
        nameView.setText(name + ", выехал в " + Const.timeFormat.format(time.getTime()));
        tr.addView(nameView);
        return tr;
    }
}

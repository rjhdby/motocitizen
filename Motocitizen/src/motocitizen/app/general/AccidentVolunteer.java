package motocitizen.app.general;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import motocitizen.main.R;
import motocitizen.utils.Const;

public class AccidentVolunteer {
    public  int    id;
    public  String name;
    private Status status;
    public  Date   time;

    public enum Status {
        INPLACE, LEAVE, ONWAY, CANCEL
    }

    public void setStatus(Status newStatus) {
        status = newStatus;
    }

    public void setStatus(String newStatus) {
        if (newStatus.equals("inplace"))
            status = Status.INPLACE;
        else if (newStatus.equals("leave"))
            status = Status.LEAVE;
        else if (newStatus.equals("onway"))
            status = Status.ONWAY;
        else if (newStatus.equals("cancel"))
            status = Status.CANCEL;
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

    public boolean isCancel() {
        return status == Status.CANCEL;
    }

    public AccidentVolunteer(JSONObject json) throws JSONException {
        id = json.getInt("id");
        name = json.getString("name");
        setStatus(json.getString("status"));
        time = new Date(Long.parseLong(json.getString("uxtime"), 10) * 1000);
    }

    public AccidentVolunteer(int id, String name, Status status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.time = new Date();
    }
    private void inflateHeader(Context context, ViewGroup tableLayout) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow       tr = (TableRow) li.inflate(R.layout.volunteer_row, tableLayout, false);
        ((TextView) tr.findViewById(R.id.volunteer)).setText("Кто");
        ((TextView) tr.findViewById(R.id.action)).setText("Что");
        ((TextView) tr.findViewById(R.id.time)).setText("Когда");
        tableLayout.addView(tr);
    }

    public void inflateRow(Context context, ViewGroup tableLayout) {
        if (isCancel() || isLeave()) return;
        if (tableLayout.getChildCount() == 0) {
            inflateHeader(context, tableLayout);
        }
        String type;
        if (isInplace()) {
            type = "приехал";
        } else {
            type = "выехал";
        }
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow       tr = (TableRow) li.inflate(R.layout.volunteer_row, tableLayout, false);
        ((TextView) tr.findViewById(R.id.volunteer)).setText(name);
        ((TextView) tr.findViewById(R.id.action)).setText(type);
        ((TextView) tr.findViewById(R.id.time)).setText(Const.timeFormat.format(time.getTime()));
        tableLayout.addView(tr);
    }
}

package motocitizen.app.general;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import motocitizen.utils.Const;
import motocitizen.utils.MyUtils;

public class AccidentVolunteer {
    public int id;
    public String name;
    private Status status;
    public Date time;

    public enum Status {
        INPLACE, LEAVE, ONWAY, CANCEL
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
        else if(newStatus.equals("cancel"))
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
        } else if(isCancel()) {
            type = ", отменил выезд в ";
        }
        Log.d("TYPE", type);
        Log.d("NAME", name);
        TableRow tr = new TableRow(context);
        TableRow.LayoutParams lp = new TableRow.LayoutParams();
        TextView nameView = new TextView(tr.getContext());
        nameView.setText(name + type + Const.timeFormat.format(time.getTime()));
        nameView.setLayoutParams(lp);
        tr.addView(nameView);
        return tr;
    }
  /*
public TableRow createRow(Context context) {
    TableRow tr = new TableRow(context);
    TableRow.LayoutParams lp = new TableRow.LayoutParams();
    TextView tvOwner = new TextView(tr.getContext());
    TextView tvText = new TextView(tr.getContext());
    TextView tvDate = new TextView(tr.getContext());
    lp.setMargins(0, 0, 5, 0);
    tvOwner.setLayoutParams(lp);
    tvOwner.setText(name);

    tvText.setText("");
    tvDate.setText(MyUtils.getStringTime(time, true));
    tr.setTag(String.valueOf(id));
    tr.addView(tvOwner);
    tr.addView(tvText);
    tr.addView(tvDate);
    // tr.setOnLongClickListener(rowLongClick);
    return tr;
}
*/
}

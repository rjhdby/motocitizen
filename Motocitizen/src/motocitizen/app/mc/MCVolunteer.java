package motocitizen.app.mc;

import java.text.ParseException;
import java.util.Date;

import motocitizen.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;

public class MCVolunteer {
	public int id;
	public String name;
	public String status;
	public Date time;

	public MCVolunteer(JSONObject json) throws JSONException {
		id = json.getInt("id");
		name = json.getString("name");
		status = json.getString("status");
		try {
			time = Const.dateFormat.parse(json.getString("timest"));
		} catch (ParseException e) {
			time = new Date();
			e.printStackTrace();
		}
	}

	public TableRow createRow(Context context) {
		TableRow tr = new TableRow(context);
		TextView nameView = new TextView(tr.getContext());
		// TextView statusView = new TextView(tr.getContext());
		// TextView timeView = new TextView(tr.getContext());
		nameView.setText(name + ", выехал в " + Const.timeFormat.format(time.getTime()));
		// statusView.setText(status + " ");
		// timeView.setText(Const.timeFormat.format(time.getTime()));
		tr.addView(nameView);
		// tr.addView(statusView);
		// tr.addView(timeView);
		return tr;
	}
}

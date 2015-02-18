package motocitizen.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import motocitizen.startup.Startup;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TableRow;

public class Const {
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
	public static final Activity act = (Activity) Startup.context;
	public static final LayoutInflater li = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	public static final TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
}

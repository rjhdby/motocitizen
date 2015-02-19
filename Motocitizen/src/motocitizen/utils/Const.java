package motocitizen.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import motocitizen.startup.Startup;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;

public class Const {
	public static SimpleDateFormat timeFormat;
	public static SimpleDateFormat dateFormat;
	public static Activity act;
	public static LayoutInflater li;
	public static TableRow.LayoutParams trlp;
	public static LayoutParams lp;
	public static int defaultColor, defaultBGColor;
	public Const(){
		timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		act = (Activity) Startup.context;
		lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		trlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		li = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TypedArray ta = Startup.context.obtainStyledAttributes(new int[] { android.R.attr.colorBackground, android.R.attr.textColorPrimary });
		defaultBGColor = ta.getIndex(0);
		defaultColor = ta.getIndex(1);
		ta.recycle();
	}
}

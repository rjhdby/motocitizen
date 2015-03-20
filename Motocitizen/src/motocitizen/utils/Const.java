package motocitizen.utils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import motocitizen.startup.Startup;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;

public class Const {
	public static SimpleDateFormat timeFormat, fullTimeFormat, dateFormat;
	public static LayoutInflater li;
	public static TableRow.LayoutParams trlp;
	public static LayoutParams lp;
	public static int defaultColor, defaultBGColor;
	public static Map<String, String> med_text, status_text, type_text;
	public static final float dp = Startup.context.getResources().getDisplayMetrics().density;
	public static int width, height;

	public Const() {
		Display display = ((Activity) Startup.context).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
		fullTimeFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		Activity act = (Activity) Startup.context;
		lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		trlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		li = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TypedArray ta = Startup.context.obtainStyledAttributes(new int[] { android.R.attr.colorBackground, android.R.attr.textColorPrimary });
		defaultBGColor = ta.getIndex(0);
		defaultColor = ta.getIndex(1);
		ta.recycle();
		med_text = new HashMap<String, String>();
		status_text = new HashMap<String, String>();
		type_text = new HashMap<String, String>();
		med_text.put("mc_m_d", "Летальный");
		med_text.put("mc_m_h", "Тяжелые травмы");
		med_text.put("mc_m_l", "Легкие травмы");
		med_text.put("mc_m_wo", "Без травм");
		med_text.put("mc_m_na", "");

		status_text.put("acc_status_act", "Активно");
		status_text.put("acc_status_end", "Завершено");
		status_text.put("acc_status_hide", "Скрыто");
		status_text.put("acc_status_war", "Конфликт");

		type_text.put("acc_b", "Поломка");
		type_text.put("acc_o", "Прочее");
		type_text.put("acc_m", "Один участник");
		type_text.put("acc_m_a", "ДТП мот/авто");
		type_text.put("acc_m_m", "ДТП мот/мот");
		type_text.put("acc_m_p", "Наезд на пешехода");
		type_text.put("acc_s", "Угон");
	}
}

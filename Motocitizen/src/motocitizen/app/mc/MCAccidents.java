package motocitizen.app.mc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import motocitizen.app.mc.gcm.MCGCMRegistration;
import motocitizen.app.mc.popups.MCAccListPopup;
import motocitizen.app.mc.user.MCAuth;
import motocitizen.app.osm.OSMMap;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.NewID;
import motocitizen.utils.Show;
import motocitizen.utils.Text;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MCAccidents {
	private static final TableLayout tl = (TableLayout) Const.act.findViewById(R.id.accListContent);

	private static Integer[] sorted;
	private static Map<String, Boolean> visibility;
	public static int onway, inplace;
	public static MCPoint currentPoint;
	public static MCPoints points;
	public static MCAuth auth;
	private static Context context;

	public MCAccidents(Context context) {
		this.context = context;
		onway = 0;
		inplace = 0;
		MCInit.readProperties();
		MCInit.addListeners();
		auth = new MCAuth();
		new MCLocation();
		MCInit.setupAccess(auth);
		MCInit.setupValues(auth);
		points = new MCPoints();
		points.load();
		new MCGCMRegistration();
		makeSortedList();
		currentPoint = points.getPoint(sorted[0]);
	}

	public static TableRow drawError() {
		View vAccRow = Const.li.inflate(R.layout.mc_acc_list_error_row, tl, false);
		TableRow tr = (TableRow) vAccRow.findViewById(R.id.mc_acc_list_error_row);
		tr.setId(NewID.id());
		((TextView) tr.findViewById(R.id.mc_acc_list_error_text)).setText(points.error);
		return tr;
	}

	private static void makeSortedList() {
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(points.keySet());
		sorted = new Integer[list.size()];
		list.toArray(sorted);
		Arrays.sort(sorted, Collections.reverseOrder());
	}

	private static TableRow yesterdayRow() {
		TableRow tr = new TableRow(Const.act);
		TextView tv = new TextView(tr.getContext());
		tv.setText("Вчера");
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundColor(Color.GRAY);
		tr.setBackgroundColor(Color.GRAY);
		tr.setGravity(Gravity.CENTER);
		tr.addView(tv, Const.trlp);
		tv.setLayoutParams(Const.trlp);
		return tr;
	}

	public static void setupVisibility() {
		visibility = new HashMap<String, Boolean>();
		visibility.put("acc_b", Startup.prefs.getBoolean("mc.show.break", true));
		visibility.put("acc_m", Startup.prefs.getBoolean("mc.show.acc", true));
		visibility.put("acc_m_m", Startup.prefs.getBoolean("mc.show.acc", true));
		visibility.put("acc_m_a", Startup.prefs.getBoolean("mc.show.acc", true));
		visibility.put("acc_m_p", Startup.prefs.getBoolean("mc.show.acc", true));
		visibility.put("acc_o", Startup.prefs.getBoolean("mc.show.other", true));
		visibility.put("acc_s", Startup.prefs.getBoolean("mc.show.steal", true));
	}

	public static void drawList() {
		setupVisibility();
		tl.removeAllViews();
		boolean noYesterday = true;
		if (points.error.equals("ok") || points.error.equals("no_new")) {
			makeSortedList();
			for (int i = 0; i < sorted.length; i++) {
				MCPoint acc = points.getPoint(sorted[i]);
				// Log.d("POINT", acc.toString());
				if (visibility.get(acc.type)) {
					if (!acc.isToday() && noYesterday) {
						tl.addView(yesterdayRow(), Const.trlp);
						noYesterday = false;
					}
					TableRow tr = acc.createTableRow(context);
					tl.addView(tr, Const.trlp);
				} else {
					acc.row_id = 0;
				}
			}
			if (sorted.length == 0) {
				tl.addView(drawError());
			} else {
				makeDetails(currentPoint.id);
				points.setSelected(context, currentPoint.id);
			}
		} else {
			tl.addView(drawError());
		}
		Show.showLast();
	}

	private static OnLongClickListener detLongClick = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			PopupWindow pw;
			pw = MCAccListPopup.getPopupWindow(currentPoint.id);
			pw.showAsDropDown(v, 20, -20);
			return true;
		}
	};

	public static void makeDetails(int id) {
		MCPoint p = points.getPoint(id);
		currentPoint = p;
		Text.set(R.id.mc_acc_details_general_type, p.getTypeText() + ". " + p.getMedText());
		Text.set(R.id.mc_acc_details_general_status, p.getStatusText());
		Text.set(R.id.mc_acc_details_general_time, Const.timeFormat.format(p.created.getTime()));
		Text.set(R.id.mc_acc_details_general_owner, p.owner);
		Text.set(R.id.mc_acc_details_general_address, "(" + p.distanceText() + ") " + p.address);
		Text.set(R.id.mc_acc_details_general_description, p.descr);

		if (currentPoint.id == onway || currentPoint.id == inplace) {
			MCObjects.onwayButton.setVisibility(View.INVISIBLE);
		} else {
			MCObjects.onwayButton.setVisibility(View.VISIBLE);
		}
		((View) Const.act.findViewById(R.id.mc_acc_details_general)).setOnLongClickListener(detLongClick);
		ViewGroup tv = (ViewGroup) Const.act.findViewById(R.id.mc_det_messages_table);
		tv.removeAllViews();
		for (int i : p.messages.keySet()) {
			tv.addView(p.messages.get(i).createRow(context));
		}
		((ScrollView) Const.act.findViewById(R.id.mc_det_messages_scroll)).fullScroll(View.FOCUS_UP);
		ViewGroup vg = (ViewGroup) MCObjects.onwayContent;
		vg.removeAllViews();
		for (int i : p.volunteers.keySet()) {
			vg.addView(p.volunteers.get(i).createRow(context));
		}
		OSMMap.zoom(16);
		OSMMap.jumpToPoint(p.location);
	}

	public static void refresh() {
		getAccidents();
		drawList();
		OSMMap.placeAcc();
	}

	private static void getAccidents() {
		points.load();
		tl.removeAllViews();
	}

	public static void toDetails() {
		toDetails(currentPoint.id);
	}

	public static void toDetails(int id) {
		MCObjects.tabDetailsButton.setChecked(true);
		makeDetails(id);
		points.setSelected(context, id);
	}
}

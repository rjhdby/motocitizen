package motocitizen.app.mc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import motocitizen.app.mc.MCVolunteers.Volunteer;
import motocitizen.app.mc.gcm.MCGCMRegistration;
import motocitizen.app.mc.popups.MCAccListPopup;
import motocitizen.app.mc.user.MCAuth;
import motocitizen.app.osm.OSMMap;
import motocitizen.core.Point;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.NewID;
import motocitizen.utils.Show;
import motocitizen.utils.Text;
import android.graphics.Color;
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
	public static Point currentPoint;
	public static MCPoints points;
	public static MCAuth auth;

	public MCAccidents() {
		onway = 0;
		inplace = 0;
		MCInit.readProperties();
		MCInit.addListeners();
		auth = new MCAuth();
		new MCLocation();
		MCInit.setupAccess(auth);
		MCInit.setupValues(auth);
		points = new MCPoints();
		new MCGCMRegistration();
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
		list.addAll(points.points.keySet());
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
		boolean noYesterday = true;
		if (points.error.equals("ok") || points.error.equals("no_new")) {
			int first = 0;
			makeSortedList();
			for (int i = 0; i < sorted.length; i++) {
				Point acc = points.get(sorted[i]);
				// Log.d("POINT", acc.toString());
				if (visibility.get(acc.get("mc_accident_orig_type"))) {
					TableRow tr = createRow(acc);
					if (!points.isToday(sorted[i]) && noYesterday) {
						tl.addView(yesterdayRow(), Const.trlp);
						noYesterday = false;
						if (tl.getChildCount() == 1) {
							first = 1;
						}
					}
					tl.addView(tr, Const.trlp);
				}
			}
			if (sorted.length == 0) {
				tl.addView(drawError());
			} else {
				tl.getChildAt(first).performClick();
			}
		} else {
			tl.addView(drawError());
		}
		Show.showLast();

		// ((RadioButton) tabsgroup.getChildAt(0)).setChecked(true);
	}

	private static void touchById(int id) {
		int rowId = Integer.parseInt(points.get(id).get("row_id"));
		tl.findViewById(rowId).performClick();
	}

	private static TableRow createRow(Point p) {
		View vAccRow = Const.li.inflate(R.layout.mc_acc_row, tl, false);
		TableRow tr = (TableRow) vAccRow.findViewById(R.id.accidentRow);

		int id = NewID.id();
		tr.setId(id);
		p.set("row_id", String.valueOf(id));
		tr.setOnClickListener(getAccRowClick(tr));

		Text.set(tr, R.id.mc_row_time, points.getTime(p.id));
		Text.set(tr, R.id.mc_row_address, p.get("address"));
		Text.set(tr, R.id.mc_row_general, p.get("mc_accident_type") + ". " + p.get("mc_accident_med"));
		Text.set(tr, R.id.mc_row_description, p.get("descr"));
		Text.set(tr, R.id.mc_row_distance, distanceText(p));
		tr.setOnLongClickListener(rowLongClick);

		return tr;
	}

	private static OnLongClickListener rowLongClick = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			Point p = points.findByRowId(v.getId());
			PopupWindow pw;
			pw = MCAccListPopup.getPopupWindow(p);
			pw.showAsDropDown(v, 20, -20);
			return true;
		}
	};

	private static OnLongClickListener detLongClick = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			PopupWindow pw;
			pw = MCAccListPopup.getPopupWindow(currentPoint);
			pw.showAsDropDown(v, 20, -20);
			return true;
		}
	};

	static View.OnClickListener getAccRowClick(final TableRow tr) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				ViewGroup vg = (ViewGroup) tl;
				Point p = points.findByRowId(tr.getId());
				for (int i = 0; i < vg.getChildCount(); i++) {
					View currView = vg.getChildAt(i);
					Point currPoint = points.findByRowId(currView.getId());
					if (currPoint != null) {
						currView.setBackgroundResource(getState(currPoint));
					}
				}
				tr.setBackgroundResource(getState(p, true));
				makeDetails(p);
				toDetails();
			}
		};
	}

	private static int getState(Point p) {
		if (p.get("status").equals("acc_status_end")) {
			return R.drawable.accident_row_gradient_ended;
		} else if (p.get("status").equals("acc_status_hide")) {
			return R.drawable.accident_row_gradient_hide;
		} else {
			return R.drawable.accident_row_gradient;
		}
	}

	private static int getState(Point p, Boolean selected) {
		if (p.get("status").equals("acc_status_end")) {
			return R.drawable.accident_row_gradient_selected_ended;
		} else if (p.get("status").equals("acc_status_hide")) {
			return R.drawable.accident_row_gradient_selected_hide;
		} else {
			return R.drawable.accident_row_gradient_selected;
		}
	}

	private static void makeDetails(Point p) {
		Text.set(R.id.mc_acc_details_general_type, p.get("mc_accident_type") + ". " + p.get("mc_accident_med"));
		Text.set(R.id.mc_acc_details_general_status, p.get("status_text"));
		Text.set(R.id.mc_acc_details_general_time, points.getTime(p.id));
		Text.set(R.id.mc_acc_details_general_owner, p.get("owner"));
		Text.set(R.id.mc_acc_details_general_address, "(" + distanceText(p) + ") " + p.get("address"));
		Text.set(R.id.mc_acc_details_general_description, p.get("descr"));
		currentPoint = p;
		if (currentPoint.id == onway || currentPoint.id == inplace) {
			MCObjects.onwayButton.setVisibility(View.INVISIBLE);
		} else {
			MCObjects.onwayButton.setVisibility(View.VISIBLE);
		}
		((View) Const.act.findViewById(R.id.mc_acc_details_general)).setOnLongClickListener(detLongClick);
		ViewGroup tv = (ViewGroup) Const.act.findViewById(R.id.mc_det_messages_table);
		points.messages.get(p.id).drawList(tv);
		((ScrollView) Const.act.findViewById(R.id.mc_det_messages_scroll)).fullScroll(View.FOCUS_UP);
		ViewGroup vg = (ViewGroup) MCObjects.onwayContent;
		vg.removeAllViews();
		for(Volunteer v: MCAccidents.points.volunteers.get(currentPoint.id).items){
			TableRow tr = new TableRow(vg.getContext());
			TextView name = new TextView(tr.getContext());
			TextView status = new TextView(tr.getContext());
			TextView time = new TextView(tr.getContext());
			name.setText(v.name + " ");
			status.setText(v.status + " ");
			time.setText(v.time);
			tr.addView(name);
			tr.addView(status);
			tr.addView(time);
			vg.addView(tr);
		}
	}

	private static String distanceText(Point p) {
		double d = points.distanceFromUser(p.id);
		if (d > 1000) {
			return String.valueOf(Math.round(d / 10) / 100) + "км";
		} else {
			return String.valueOf((int) d) + "м";
		}
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
		MCObjects.tabDetailsButton.setChecked(true);
	}

	public static void toDetails(int id) {
		toDetails();
		touchById(id);
	}
}

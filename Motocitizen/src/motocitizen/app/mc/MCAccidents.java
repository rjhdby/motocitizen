package motocitizen.app.mc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import motocitizen.app.mc.gcm.MCGCMRegistration;
import motocitizen.app.mc.init.MCInit;
import motocitizen.app.mc.popups.MCAccListPopup;
import motocitizen.app.mc.user.MCAuth;
import motocitizen.core.Point;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.NewID;
import motocitizen.utils.Text;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MCAccidents {
	private static final TableLayout tl = (TableLayout) Const.act.findViewById(R.id.accListContent);
	private static final RadioGroup detailstabsgroup = (RadioGroup) Const.act.findViewById(R.id.mc_det_tabs_group);
	private static final RadioGroup tabsgroup = (RadioGroup) Const.act.findViewById(R.id.main_tabs_group);
	private static final FrameLayout detailstabscontent = (FrameLayout) Const.act.findViewById(R.id.mc_det_tab_content);

	private static Integer[] sorted;
	private static Map<String, Boolean> visibility;

	public static Point currentPoint;
	public static MCPoints points;
	public static MCAuth auth;
	private MCGCMRegistration gcm;
	
	public MCAccidents() {
		MCInit.readProperties();
		MCInit.inflateViews();
		MCInit.addListeners();
		auth = new MCAuth();
		MCInit.setupAccess(auth);
		MCInit.setupValues(auth);
		points = new MCPoints();
		new MCGCMRegistration();
		addListeners();
		getAccidents();
		drawList();
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
				Log.d("POINT", acc.toString());
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

		((RadioButton) tabsgroup.getChildAt(0)).setChecked(true);
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

		Const.act.findViewById(R.id.mc_det_people_onway).setVisibility(View.VISIBLE);
		currentPoint = p;
		((View) Const.act.findViewById(R.id.mc_acc_details_general)).setOnLongClickListener(detLongClick);
		ViewGroup tv = (ViewGroup) Const.act.findViewById(R.id.mc_det_messages_table);
		points.messages.get(p.id).drawList(tv);
		((ScrollView) Const.act.findViewById(R.id.mc_det_messages_scroll)).fullScroll(View.FOCUS_UP);
	}

	private static String distanceText(Point p) {
		double d = points.distanceFromUser(p.id);
		if (d > 1000) {
			return String.valueOf(Math.round(d / 10) / 100) + "км";
		} else {
			return String.valueOf((int) d) + "м";
		}
	}

	private void addListeners() {
		detailstabsgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				String name = (String) Const.act.findViewById(checkedId).getTag();
				ViewGroup vg = (ViewGroup) detailstabscontent;
				for (int i = 0; i < vg.getChildCount(); i++) {
					String currentTag = (String) vg.getChildAt(i).getTag();
					if (currentTag.equals(name)) {
						vg.getChildAt(i).setVisibility(View.VISIBLE);
					} else {
						vg.getChildAt(i).setVisibility(View.INVISIBLE);
					}
				}
			}
		});
	}

	public static void refresh() {
		getAccidents();
		drawList();
	}

	private static void getAccidents() {
		points.load();
		tl.removeAllViews();
	}

	public static void toDetails() {
		((RadioButton) tabsgroup.findViewWithTag("mc_acc_details")).setChecked(true);
	}

	public static void toDetails(int id) {
		((RadioButton) tabsgroup.findViewWithTag("mc_acc_details")).setChecked(true);
		touchById(id);
	}
}

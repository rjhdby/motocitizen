package motocitizen.app.mc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.app.mc.gcm.MCGCMRegistration;
import motocitizen.app.mc.popups.MCAccListPopup;
import motocitizen.app.mc.user.MCAuth;
import motocitizen.main.R;
import motocitizen.network.JsonRequest;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;

public class MCAccidents {

    private static int onway;
    private static int inplace;
    public static MCPoint currentPoint;
    public static MCPoints points;
    public static MCAuth auth;
    private static Integer[] sorted;

    public static int getOnwayID() {
        return onway;
    }

    public static void setOnwayID(int id) {
        onway = id;
    }

    public static int getInplaceID() {
        return inplace;
    }

    private static final OnLongClickListener detLongClick = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            PopupWindow pw;
            pw = MCAccListPopup.getPopupWindow(currentPoint.id);
            pw.showAsDropDown(v, 20, -20);
            return true;
        }
    };

    public MCAccidents(Context context, SharedPreferences prefs) {
        onway = 0;
        inplace = 0;
        MCInit.readProperties();
        MCInit.addListeners();
        auth = new MCAuth();
        new MCLocation(context);
        MCInit.setupAccess(context, auth);
        MCInit.setupValues(auth);
        points = new MCPoints(prefs);
        new MCGCMRegistration();
        currentPoint = new MCPoint();
    }

    private static MCPoint getCurrent() {
        makeSortedList();
        if (currentPoint != null) {
            return currentPoint;
        } else if (points.keySet().size() != 0) {
            return points.getPoint(sorted[0]);
        } else {
            return null;
        }
    }

    private static void makeSortedList() {
        List<Integer> list = new ArrayList<>();
        list.addAll(points.keySet());
        sorted = new Integer[list.size()];
        list.toArray(sorted);
        Arrays.sort(sorted, Collections.reverseOrder());
    }

    private static TextView yesterdayRow(Context context) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = (int) (8 * Const.dp);
        lp.setMargins(margin, 0, margin, 0);
        TextView tv = new TextView(context);
        tv.setText("Вчера");
        tv.setTextColor(Color.WHITE);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(0xFFC62828);
        tv.setLayoutParams(lp);
        return tv;
    }

    private static FrameLayout noAccidentsNotification(Context context) {
        FrameLayout fl = new FrameLayout(context);
        TextView tv = new TextView(context);
        tv.setText("Нет событий");
        tv.setTextColor(Color.RED);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(Color.GRAY);
        fl.setBackgroundColor(Color.GRAY);
        fl.addView(tv, Const.lp);
        return fl;
    }

    private static void drawList(Context context) {
        ViewGroup view = (ViewGroup) ((Activity) context).findViewById(R.id.accListContent);
        view.removeAllViews();
        MCAccTypes.refresh();
        boolean noYesterday = true;
        if (points.error.equals("ok") || points.error.equals("no_new")) {
            makeSortedList();
            for (Integer aSorted : sorted) {
                MCPoint acc = points.getPoint(aSorted);
                if (MCAccTypes.get(acc.type).enabled) {
                    if (!acc.isToday() && noYesterday) {
                        view.addView(yesterdayRow(context));
                        noYesterday = false;
                    }
                    FrameLayout tr = acc.createAccRow(context);
                    view.addView(tr);
                } else {
                    acc.row_id = 0;
                }
            }
            if (sorted.length == 0) {
                view.addView(noAccidentsNotification(context));
                //zz
                // ((Activity) context).findViewById(R.id.tab_acc_details_button).setEnabled(false);
            } else if (currentPoint.id == 0) {
                //zz
                // ((Activity) context).findViewById(R.id.tab_acc_details_button).setEnabled(false);
                points.setSelected(context, currentPoint.id);
            } else {
                //zz
                // makeDetails(context, currentPoint.id);
                points.setSelected(context, currentPoint.id);
                //zz
                // ((Activity) context).findViewById(R.id.tab_acc_details_button).setEnabled(true);
            }
        } else {
            // TODO Сюда вкрячить сообщение об ошибке
        }
    }

    public static TableRow getDelimiterRow(Context context, String text) {
        TableRow tr = new TableRow(context);
        TextView tw = new TextView(tr.getContext());
        tr.setLayoutParams(Const.trlp);
        tw.setTextColor(Color.BLACK);
        tw.setBackgroundColor(Color.LTGRAY);
        tw.setGravity(Gravity.CENTER);
        tw.setText(text);
        tw.setLayoutParams(Const.trlp);
        tr.addView(tw);
        return tr;
    }
// zz
//
//    private static void makeDetails(Context context, int id) {
//        MCPoint p = points.getPoint(id);
//        currentPoint = p;
//        Text.set(R.id.mc_acc_details_general_type, p.getTypeText() + ". " + p.getMedText());
//        Text.set(R.id.mc_acc_details_general_status, p.getStatusText());
//        Text.set(R.id.mc_acc_details_general_time, Const.timeFormat.format(p.created.getTime()));
//        Text.set(R.id.mc_acc_details_general_owner, p.owner);
//        Text.set(R.id.mc_acc_details_general_address, "(" + p.getDistanceText() + ") " + p.address);
//        Text.set(R.id.mc_acc_details_general_description, p.descr);
//
//        if (currentPoint.id == onway || currentPoint.id == inplace) {
//            MCObjects.onwayButton.setVisibility(View.INVISIBLE);
//        } else {
//            MCObjects.onwayButton.setVisibility(View.VISIBLE);
//        }
//        /*
//		 * Выводим список сообщений
//		 */
//        ((Activity) context).findViewById(R.id.mc_acc_details_general).setOnLongClickListener(detLongClick);
//        ViewGroup messageView = (ViewGroup) MCObjects.mcDetMessagesTable;
//        messageView.removeAllViews();
//        for (int i : p.getSortedMessagesKeys()) {
//            messageView.addView(p.messages.get(i).createRow(context));
//        }
//        ((ScrollView) ((Activity) context).findViewById(R.id.mc_det_messages_scroll)).fullScroll(View.FOCUS_UP);
//		/*
//		 * Выводим список волонтеров
//		 */
//        ViewGroup vg_onway = (ViewGroup) MCObjects.onwayContent;
//        ViewGroup vg_inplace = (ViewGroup) MCObjects.inplaceContent;
//        vg_onway.setVisibility(View.INVISIBLE);
//        vg_inplace.setVisibility(View.INVISIBLE);
//        vg_onway.removeAllViews();
//        vg_inplace.removeAllViews();
//        for (int i : p.getSortedVolunteersKeys()) {
//            MCVolunteer current = p.volunteers.get(i);
//            if (current.status.equals("onway")) {
//                if (vg_onway.getVisibility() == View.INVISIBLE) {
//                    vg_onway.setVisibility(View.VISIBLE);
//                    vg_onway.addView(getDelimiterRow(context, "В пути"));
//                }
//                vg_onway.addView(current.createRow(context));
//            }
//        }
//		/*
//		 * Выводим историю
//		 */
//        ViewGroup logView = (ViewGroup) MCObjects.mcDetLogContent;
//        logView.removeAllViews();
//        logView.addView(MCPointHistory.createHeader(context));
//        for (int i : p.getSortedHistoryKeys()) {
//            logView.addView(p.history.get(i).createRow(context));
//        }
//        Startup.map.zoom(16);
//        Startup.map.jumpToPoint(p.location);
//    }

    public static void refresh(Context context) {
        points.load();
        Startup.map.placeAcc(context);
        redraw(context);
        Startup.map.placeAcc(context);
    }

    public static void refreshPoints(Context context, JSONObject data) {
        if(data != null) {
            try {
                JSONArray arr  = data.getJSONArray("list");
                points.update(arr);
                Startup.map.placeAcc(context);
                redraw(context);
                Startup.map.placeAcc(context);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(Startup.context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void redraw(Context context) {
        currentPoint = getCurrent();
        if (currentPoint == null) {
            return;
        }
        drawList(context);
    }
/*
    public static void toDetails(Context context) {
        currentPoint = getCurrent();
        if (currentPoint == null) {
            return;
        }
        toDetails(context, currentPoint.id);
    }
*/
    public static void toDetails(Context context, int id) {

        MCPoint p = points.getPoint(id);
        currentPoint = p;
/*
        if (!points.containsKey(id)) {
            return;
        }
*/
        //MCObjects.tabDetailsButton.setChecked(true);
        //makeDetails(context, id);
        points.setSelected(context, id);
        //redraw(context);
        Intent intent = new Intent(Startup.context, AccidentDetailsActivity.class);
        Startup.context.startActivity(intent);
    }

    public static JsonRequest getLoadPointsRequest() {
        return points.getLoadRequet();
    }
}

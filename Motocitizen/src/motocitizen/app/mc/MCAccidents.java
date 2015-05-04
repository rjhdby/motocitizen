package motocitizen.app.mc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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
import motocitizen.startup.MCPreferences;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;

public class MCAccidents {

    public static int onway;
    public static int inplace;
    private static MCPoint currentPoint;
    public static MCPoints points;
    public static MCAuth auth;
    private static Integer[] sorted;
    private static MCPreferences prefs;

    public static int getInplaceID() {
        return inplace;
    }

    public static void setInPlace(int id){
        inplace = id;
        for(int key:points.keySet()){
            if(points.getPoint(key).isInPlace()){
                points.getPoint(key).setLeave();
            }
        }
        points.getPoint(id).setInPlace();
    }

    public static int getOnway(){
        return onway;
    }

    public static void setLeave(int id){
        if(points.getPoint(id).isInPlace()){
            points.getPoint(id).setLeave();
        }
    }

    public static void setOnWay(int id) {
        for(int key:points.keySet()){
            if(points.getPoint(key).isOnWay()){
                points.getPoint(key).resetStatus();
            }
        }
        onway = id;
        points.getPoint(id).setOnWay();
    }

    private static final OnLongClickListener detLongClick = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            PopupWindow pw;
            pw = MCAccListPopup.getPopupWindow(currentPoint.getId(), false);
            pw.showAsDropDown(v, 20, -20);
            return true;
        }
    };

    public static int getCurrentPointID() {
        return currentPoint.getId();
    }

    public static void setCurrentPoint(MCPoint point) {
        currentPoint = point;
    }

    public MCAccidents(Context context) {
        prefs = new MCPreferences(context);
        onway = 0;
        inplace = 0;
        auth = new MCAuth(context);
        new MCLocation(context);
        points = new MCPoints(context);
        new MCGCMRegistration(context);
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
        int margin = (int) (8 * Const.getDP(context));
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
        //MCAccTypes.refresh();
        boolean noYesterday = true;
        if (points.error.equals("ok") || points.error.equals("no_new")) {
            makeSortedList();
            for (Integer aSorted : sorted) {
                MCPoint acc = points.getPoint(aSorted);
                if (acc.isVisible()) {
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
            } else if (currentPoint.getId() == 0) {
                points.setSelected(context, currentPoint.getId());
            } else {
                points.setSelected(context, currentPoint.getId());
            }
        } else {
            // TODO Сюда вкрячить сообщение об ошибке
        }
    }

    @SuppressWarnings("SameParameterValue")
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

    public static void refresh(Context context) {
        points.load();
        Startup.map.placeAcc(context);
        redraw(context);
        Startup.map.placeAcc(context);
    }

    public static void refreshPoints(Context context, JSONObject data) {
        if (data != null) {
            try {
                JSONArray arr = data.getJSONArray("list");
                points.update(arr);
                Startup.map.placeAcc(context);
                redraw(context);
                Startup.map.placeAcc(context);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
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

    public static void toDetails(Context context) {
        toDetails(context, currentPoint.getId());
    }

    public static void toDetails(Context context, int id) {
        currentPoint = points.getPoint(id);
        if (currentPoint != null) {
/*
        if (!points.containsKey(id)) {
            return;
        }
*/
            points.setSelected(context, id);
            //redraw(context);
            Intent intent = new Intent(context, AccidentDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("accidentID", currentPoint.getId());
            intent.putExtras(bundle);
            context.startActivity(intent);

        } else {
            Toast.makeText(context, Startup.context.getString(R.string.cant_open_incident), Toast.LENGTH_LONG).show();
        }
    }

    public static JsonRequest getLoadPointsRequest() {
        return points.getLoadRequest();
    }
}

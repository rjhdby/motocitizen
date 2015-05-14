package motocitizen.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Const {
    public static SimpleDateFormat timeFormat, fullTimeFormat, dateFormat;
    public static TableRow.LayoutParams trlp;
    public static LayoutParams lp;
    public static Map<String, String> med_text, status_text, type_text;
    public final static int EQUATOR = 20038;

    @SuppressWarnings("deprecation")
    public Const() {
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        fullTimeFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        trlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        med_text = new HashMap<>();
        status_text = new HashMap<>();
        type_text = new HashMap<>();
        med_text.put("mc_m_d", "Летальный");
        med_text.put("mc_m_h", "Тяжелые травмы");
        med_text.put("mc_m_l", "Легкие травмы");
        med_text.put("mc_m_wo", "Без травм");
        med_text.put("mc_m_na", "");

        status_text.put("acc_status_act", "Активно");
        status_text.put("acc_status_end", "Отбой");
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

    public static float getDP(Context context){
        return context.getResources().getDisplayMetrics().density;
    }
    public static int getHeight(Context context){
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }

    public static int getWidth(Context context){
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    public static int getDefaultBGColor(Context context){
        TypedArray ta = context.obtainStyledAttributes(new int[]{android.R.attr.colorBackground, android.R.attr.textColorPrimary});
        int color = ta.getIndex(0);
        ta.recycle();
        return color;
    }

    public static int getDefaultColor(Context context){
        TypedArray ta = context.obtainStyledAttributes(new int[]{android.R.attr.colorBackground, android.R.attr.textColorPrimary});
        int color = ta.getIndex(1);
        ta.recycle();
        return color;
    }

    public static LayoutInflater getLayoutInflater(Context context){
        Activity act = (Activity) context;
        return (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}

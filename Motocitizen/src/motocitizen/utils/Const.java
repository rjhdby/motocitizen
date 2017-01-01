package motocitizen.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Const {
    public static final SimpleDateFormat FULL_TIME_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT      = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static final SimpleDateFormat TIME_FORMAT      = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public final static int              EQUATOR          = 20038;
    public final static String           PHONE            = "78007751734";

    public static int getWidth(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    //TODO изничтожить
    public static int getDefaultBGColor(Context context) {
        TypedArray ta    = context.obtainStyledAttributes(new int[]{android.R.attr.colorBackground, android.R.attr.textColorPrimary});
        int        color = ta.getIndex(0);
        ta.recycle();
        return color;
    }
}

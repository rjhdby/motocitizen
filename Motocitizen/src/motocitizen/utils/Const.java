package motocitizen.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class Const {
    public final static int    EQUATOR = 20038;
    public final static String PHONE   = "78007751734";

    public static int getWidth(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }
}

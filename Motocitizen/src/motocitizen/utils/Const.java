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
    public static final SimpleDateFormat FULL_TIME_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT      = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static final SimpleDateFormat TIME_FORMAT      = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public final static int              EQUATOR          = 20038;

    public static float getDP(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getHeight(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }

    public static int getWidth(Context context) {
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
}

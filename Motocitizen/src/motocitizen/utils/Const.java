package motocitizen.utils;

import android.content.res.TypedArray;
import android.view.Display;

import java.text.SimpleDateFormat;
import java.util.Locale;

import motocitizen.MyApp;

public class Const {
    public static final SimpleDateFormat FULL_TIME_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT      = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static final SimpleDateFormat TIME_FORMAT      = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public final static int              EQUATOR          = 20038;

    public static float getDP() {
        return MyApp.getCurrentActivity().getResources().getDisplayMetrics().density;
    }

    public static int getHeight() {
        Display display = MyApp.getCurrentActivity().getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }

    public static int getWidth() {
        Display display = MyApp.getCurrentActivity().getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    public static int getDefaultBGColor() {
        TypedArray ta    = MyApp.getCurrentActivity().obtainStyledAttributes(new int[]{android.R.attr.colorBackground, android.R.attr.textColorPrimary});
        int        color = ta.getIndex(0);
        ta.recycle();
        return color;
    }

    public static int getDefaultColor() {
        TypedArray ta    = MyApp.getCurrentActivity().obtainStyledAttributes(new int[]{android.R.attr.colorBackground, android.R.attr.textColorPrimary});
        int        color = ta.getIndex(1);
        ta.recycle();
        return color;
    }
}

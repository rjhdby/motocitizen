package motocitizen.utils;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import motocitizen.startup.Startup;

public class Text {
    private static final Activity act = (Activity) Startup.context;

    public static void set(final int id, final String text) {
        Runnable update = new Runnable() {
            @Override
            public void run() {
                TextView v = (TextView) act.findViewById(id);
                v.setText(text);
            }
        };
        act.runOnUiThread(update);
    }

    public static void set(final View view, final int id, final String text) {
        Runnable update = new Runnable() {
            @Override
            public void run() {
                TextView v = (TextView) view.findViewById(id);
                v.setText(text);
            }
        };
        act.runOnUiThread(update);
    }

    public static String get(int id) {
        TextView v = (TextView) act.findViewById(id);
        return v.getText().toString();
    }
}

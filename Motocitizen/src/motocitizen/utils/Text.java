package motocitizen.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import motocitizen.startup.Startup;

public class Text {

    public static void set(final Context context, final int id, final String text) {
        Runnable update = new Runnable() {
            @Override
            public void run() {
                TextView v = (TextView) ((Activity) context).findViewById(id);
                v.setText(text);
            }
        };
        ((Activity) Startup.context).runOnUiThread(update);
    }

    public static void set(final View view, final int id, final String text) {
        Runnable update = new Runnable() {
            @Override
            public void run() {
                TextView v = (TextView) view.findViewById(id);
                v.setText(text);
            }
        };
        ((Activity) Startup.context).runOnUiThread(update);
    }

    public static String get(int id) {
        TextView v = (TextView) ((Activity) Startup.context).findViewById(id);
        return v.getText().toString();
    }
}

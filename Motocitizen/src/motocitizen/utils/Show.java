package motocitizen.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import motocitizen.startup.Startup;

public class Show {
    public static Integer currentGeneral;

    public static void show(int parentId, int childId) {
        ViewGroup parent = (ViewGroup) ((Activity) Startup.context).findViewById(parentId);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view.getId() == childId) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }
}

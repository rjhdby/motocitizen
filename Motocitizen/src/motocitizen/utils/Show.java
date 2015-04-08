package motocitizen.utils;

import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import motocitizen.startup.Startup;

public class Show {
    private static Pair<Integer, Integer> current;
    public static Integer currentGeneral;

    public static void show(int parentId, int childId) {
        current = new Pair<>(parentId, childId);
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

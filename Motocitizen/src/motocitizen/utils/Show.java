package motocitizen.utils;

import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import motocitizen.app.mc.MCObjects;
import motocitizen.main.R;
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

    public static void show(int childId) {
        show(R.id.main_frame, childId);
    }

    public static void showLast() {
        if (current == null) {
            show(R.id.main_frame, R.id.main_frame_applications);
        } else {
            if (current.first == R.id.main_frame) {
                /*if (current.second == R.id.mc_select_sound_screen || current.second == R.id.mc_auth) {
                    show(R.id.main_frame, R.id.main_frame_settings);
                } else if (current.second == R.id.mc_create_main || current.second == R.id.main_frame_settings) {

                    show(R.id.main_frame, R.id.main_frame_applications);
                } else*/ if (current.second == R.id.main_frame_applications) {

                    MCObjects.tabAccidentsButton.setChecked(true);
                }
            }
        }
    }
}

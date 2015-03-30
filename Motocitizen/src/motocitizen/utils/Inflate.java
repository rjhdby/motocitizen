package motocitizen.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import motocitizen.startup.Startup;

public class Inflate {
    private static final Activity act = (Activity) Startup.context;
    private static final LayoutInflater li = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    /** Устанавливаем карту в контейнер.*/
    public static void set(int parentId, int childId) {
        ViewGroup parent = (ViewGroup) act.findViewById(parentId);
        View child = li.inflate(childId, parent, false);
        if(parent.getChildCount() > 0)
            parent.removeAllViews();
        parent.addView(child);
    }

}

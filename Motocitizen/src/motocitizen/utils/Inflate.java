package motocitizen.utils;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Inflate {
	private static final Activity act = (Activity) Startup.context;
	private static final int main = act.findViewById(R.id.main_frame).getId();
	private static final LayoutInflater li = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	public static void add(int parentId, int childId) {
		ViewGroup parent = (ViewGroup) act.findViewById(parentId);
		View child = li.inflate(childId, parent, false);
		parent.addView(child);
	}
	
	public static void add(int childId) {
		add(main, childId);
	}
	
	public static void add(View parentView, int childId) {
		ViewGroup parent = (ViewGroup) parentView;
		View child = li.inflate(childId, parent, false);
		parent.addView(child);
	}
}

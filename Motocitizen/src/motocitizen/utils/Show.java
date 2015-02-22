package motocitizen.utils;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

public class Show {
	public static Pair<Integer, Integer> current;

	public static void show(int parentId, int childId) {
		current = new Pair<Integer, Integer>(parentId, childId);
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
			switch (current.first) {
			case R.id.main_frame:
				switch (current.second) {
				case R.id.mc_select_sound_screen:
				case R.id.mc_auth:
					show(R.id.main_frame, R.id.main_frame_settings );
					break;
				case R.id.mc_create_main:
				case R.id.main_frame_settings:
					show(R.id.main_frame, R.id.main_frame_applications );
					break;
				case R.id.main_frame_applications:
					RadioGroup tabsgroup = (RadioGroup) ((Activity) Startup.context).findViewById(R.id.main_tabs_group);
					tabsgroup.check(2);
					break;
				default:
					break;
				}
				break;
			}
		}
	}
}

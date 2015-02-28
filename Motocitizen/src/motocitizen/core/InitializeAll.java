package motocitizen.core;

import java.util.Map;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

public class InitializeAll {
	public static void init() {
		Map<String, Application> apps = Startup.applications.apps;
		Map<String, Task> tasks = Startup.tasks.tasks;
		String[] order = Startup.props.get("start.order").split(",");
		for (int i = 0; i < order.length; i++) {
			String[] parts = order[i].split("[.]");
			// Log.d("ORDER", order[i]);
			String type = parts[0];
			String name = parts[1];
			if (type.equals("app")) {
				if (apps.containsKey(name)) {
					Log.d("APP INIT", name);
					apps.get(name).run();
				}
			} else if (type.equals("task")) {
				if (tasks.containsKey(name)) {
					Log.d("TASK INIT", name);
					tasks.get(name).run("high");
				}
			}
		}
		// addListener();
	}

	public static void addListener() {
		RadioGroup tabsgroup = (RadioGroup) ((Activity) Startup.context).findViewById(R.id.main_tabs_group);
		tabsgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				FrameLayout tabscontent = (FrameLayout) ((Activity) Startup.context).findViewById(R.id.tabcontent);
//				Log.d("TAB PRESSED", String.valueOf(checkedId));
//				Log.d("TAB", ((Activity) Startup.context).findViewById(checkedId).toString());
				String name = (String) ((Activity) Startup.context).findViewById(checkedId).getTag() + "_content";
				ViewGroup vg = (ViewGroup) tabscontent;
				for (int i = 0; i < vg.getChildCount(); i++) {
					String currentTag = (String) vg.getChildAt(i).getTag();
					if (currentTag.equals(name)) {
						vg.getChildAt(i).setVisibility(View.VISIBLE);
					} else {
						vg.getChildAt(i).setVisibility(View.INVISIBLE);
					}
				}
			}
		});
	}

	/* TO DO Убрать эту порнографию */
	public static void checkTab() {
		RadioGroup tabsgroup = (RadioGroup) ((Activity) Startup.context).findViewById(R.id.main_tabs_group);
		tabsgroup.check(4);
		tabsgroup.check(2);
	}
}

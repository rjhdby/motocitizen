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
		Map<String,Application> apps = Startup.applications.apps;
		Map<String,Task> tasks = Startup.tasks.tasks;
		String[] order = Startup.props.get("start.order").split(",");
		for (int i = 0; i < order.length; i++) {
			String[] parts = order[i].split("[.]");
//			Log.d("ORDER", order[i]);
			String type = parts[0];
			String name = parts[1];
			if (type.equals("app")){
				if(apps.containsKey(name)){
					Log.d("APP INIT", name);
					apps.get(name).run();
				}
			}
			else if(type.equals("task")){
				if(tasks.containsKey(name)){
					Log.d("TASK INIT", name);
					tasks.get(name).run("high");
				}
			}
		}
		//addListener();
	}
	
	public static void addListener() {
		final Activity act = (Activity) Startup.context;
		final FrameLayout tabscontent = (FrameLayout) act.findViewById(R.id.tabcontent);
		RadioGroup tabsgroup = (RadioGroup) act.findViewById(R.id.main_tabs_group);
		tabsgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.d("TAB PRESSED", String.valueOf(checkedId));
				String name = (String) act.findViewById(checkedId).getTag() + "_content";
				ViewGroup vg = (ViewGroup) tabscontent;
				for (int i = 0; i < vg.getChildCount(); i++) {
					String currentTag = (String) vg.getChildAt(i).getTag();
					if (currentTag.equals(name)) {
						vg.getChildAt(i).setVisibility(View.VISIBLE);
						if(i == 0){
							Startup.prefs.edit().putString("backButton", "").commit();
						}else{
							Startup.prefs.edit().putString("backButton", "motocitizen.core.BackButton").commit();
						}
					} else {
						vg.getChildAt(i).setVisibility(View.INVISIBLE);
					}
				}
			}
		});
		/*TO DO Убрать эту порнографию */
		tabsgroup.check(4);
		tabsgroup.check(2);
	}
}

package motocitizen.core;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

public class BackButton {
	final static Activity act = (Activity) Startup.context;
	final static FrameLayout tabscontent = (FrameLayout) act.findViewById(R.id.tabcontent);
	final static RadioGroup tabsgroup = (RadioGroup) act.findViewById(R.id.main_tabs_group);

	public static void backButton() {
		Startup.prefs.edit().putString("backButton", "").commit();
		tabsgroup.check(tabsgroup.getChildAt(0).getId());
		ViewGroup vg = (ViewGroup) tabscontent;
		for (int i = 1; i < vg.getChildCount(); i++) {
			vg.getChildAt(i).setVisibility(View.INVISIBLE);
		}
		vg.getChildAt(0).setVisibility(View.VISIBLE);
	}
}

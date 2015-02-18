package motocitizen.core;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.NewID;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Tab {
	private static final LayoutInflater li = (LayoutInflater) Startup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	private static final Activity act = (Activity) Startup.context;

	public String name;
	public String text;
	public int tabId;
	public int contentId;
	private RadioGroup tabsgroup;
	private FrameLayout tabscontent;

	public Tab(String name, String text) {
		this.name = name;
		this.text = text;
	}

	public void deploy(RadioGroup tabsgroup, FrameLayout tabscontent) {
		this.tabsgroup = tabsgroup;
		this.tabscontent = tabscontent;
		tabscontent.addView(createContent());
		tabsgroup.addView(createTab());
		tabsgroup.addView(createDelimiter());
	}

	private RadioButton createTab() {
		View vTab = li.inflate(R.layout.tab, tabsgroup, false);
		RadioButton tab = (RadioButton) vTab.findViewById(R.id.tab);
		this.tabId = NewID.id();
		tab.setText(this.text);
		tab.setTag(this.name);
		tab.setId(this.tabId);
		tab.setChecked(false);
		return tab;
	}

	private LinearLayout createContent() {
		int id = act.getResources().getIdentifier(this.name, "layout", act.getPackageName());
		LinearLayout content = (LinearLayout) li.inflate(id, tabscontent, false);
		this.contentId = NewID.id();
		content.setTag(this.name + "_content");
		content.setId(this.contentId);
		return content;
	}

	private LinearLayout createDelimiter() {
		LinearLayout delimiter = new LinearLayout(act);
		delimiter.setBackgroundColor(0);
		delimiter.setLayoutParams(new LayoutParams((int) (act.getResources().getDisplayMetrics().density + 0.5f), LayoutParams.MATCH_PARENT));
		return delimiter;
	}
}
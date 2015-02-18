package motocitizen.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

public class Application {
	private final static String APP = Startup.props.get("default.app");
	public String name;
	public String description;
	public String className;
	public Map<String, String> tabs;
	public Map<String, String> params;
	public List<String> tasks;

	public Application() {
		this(APP);
	}

	public Application(String name) {
		this.name = name;
		String pref = "app." + name + ".";
		params = new HashMap<String, String>();
		tabs = new HashMap<String, String>();
		tasks = new ArrayList<String>();
		description = Startup.props.get(pref + "description");
		className = Startup.props.get(pref + "class");
		for (String key : Startup.props.keys(pref)){
			String[] parts = key.split("[.]");
			if(parts[2].equals("tab")){
				tabs.put(parts[3], Startup.props.get(key));
			}
			else if(parts[2].equals("task")){
				tasks.add(Startup.props.get(key));
			}
			else{
				params.put(parts[2], Startup.props.get(key));
			}
		}
	}
	
	public void run(){
		deployTabs();
		try {
			Class<?> c = Class.forName(className);
			Constructor<?> cons = c.getConstructor();
			cons.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			Log.d("APPLICATION STARTED", description + " " + className);
		}
	}
	
	private void deployTabs() {
		Activity act = (Activity) Startup.context;
		FrameLayout tabscontent = (FrameLayout) act.findViewById(R.id.tabcontent);
		RadioGroup tabsgroup = (RadioGroup) act.findViewById(R.id.main_tabs_group);
		for (String key : tabs.keySet()) {
			(new Tab(key, tabs.get(key))).deploy(tabsgroup, tabscontent);
		}
	}
}

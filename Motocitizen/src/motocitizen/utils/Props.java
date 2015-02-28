package motocitizen.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import motocitizen.startup.Startup;
import android.app.Activity;
import android.content.res.AssetManager;

public class Props {
	private static final String GLOBAL = "global.properties";
	private static final Activity act = (Activity) Startup.context;
	private static final AssetManager as = act.getAssets();
	private static Properties summary = new Properties();
	private static Properties global = new Properties();

	public Props() {
		makeGlobal();
		makeSummary();
	}

	private void makeGlobal() {
		InputStream is;
		try {
			is = as.open(GLOBAL);
			global = read(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void makeSummary() {
		summary.clear();
		summary.putAll(global);
	}

	public static Properties read(InputStream is) {
		Reader reader;
		Properties current = new Properties();
		try {
			reader = new InputStreamReader(is, "UTF-8");
			current.load(reader);
			is.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return current;
	}

	public static Properties readAssets(String fileName) {
		InputStream is;
		Properties current = new Properties();
		try {
			is = as.open(fileName);
			current = read(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return current;
	}

	public String get(String key) {
		return summary.getProperty(key);
	}

	public List<String> keys() {
		return this.keys("");
	}

	public List<String> keys(String part) {
		List<String> keys = new ArrayList<String>();
		Enumeration<Object> e = summary.keys();
		while (e.hasMoreElements()) {
			String key = e.nextElement().toString();
			if (key.matches("^" + part + ".*$")) {
				keys.add(key);
			}
		}
		return keys;
	}

	public boolean contains(String obj) {
		return summary.contains(obj);
	}

	public boolean containsKey(String key) {
		return summary.containsKey(key);
	}
}

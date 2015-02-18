package motocitizen.core;

import java.util.HashMap;
import java.util.Map;

import motocitizen.startup.Startup;

public class Applications {
	public Map<String,Application> apps;

	public Applications() {
		apps = new HashMap<String,Application>();
		for (String prop : Startup.props.keys("app.")) {
			String[] parts = prop.split("[.]");
			String appName = parts[1];
			if (parts[0].equals("app") && parts[2].equals("class")) {
				if (Startup.props.get("app." + appName + ".enabled").equals("true")) {
					apps.put(appName, new Application(appName));
				}
			}
		}
	}
}

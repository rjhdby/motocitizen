package motocitizen.core;

import java.util.HashMap;
import java.util.Map;

import motocitizen.startup.Startup;

public class Tasks {

	public Map<String, Task> tasks;

	public Tasks() {
		tasks = new HashMap<String, Task>();
		for (String prop : Startup.props.keys("task.")) {
			String[] parts = prop.split("[.]");
			String type = parts[0];
			String name = parts[1];
			String param = parts[2];
			if (type.equals("task") && param.equals("class")) {
				tasks.put(name, new Task(name));
			}
		}
	}
	
	public void allSleep() {
		for (String task : tasks.keySet()) {
			tasks.get(task).sleep();
		}
	}

	public void allStop() {
		for (String task : tasks.keySet()) {
			tasks.get(task).stop();
		}
	}

	public void allWakeUp() {
		for (String task : tasks.keySet()) {
			tasks.get(task).wakeUp();
		}
	}
}

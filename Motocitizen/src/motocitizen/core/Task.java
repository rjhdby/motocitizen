package motocitizen.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

import motocitizen.startup.Startup;

public class Task {
	private Timer timer;
	public String className;
	public String mode;
	public int interval;
	public String status;
	public TimerTask timerTask;
	public String name;

	public Task(String name) {
		this.name = name;
		status = "stopped";
		className = Startup.props.get("task." + name + ".class");
		interval = Integer.parseInt(Startup.props.get("task." + name + ".interval"));
		mode = Startup.props.get("task." + name + ".mode");
	}

	public void run(String mode) {
		this.mode = mode;
		if (this.status.equals("stopped")) {
			try {
				Class<?> timerTaskObj;
				if (timer != null) {
					timer.cancel();
				}
				timer = new Timer();
				timerTaskObj = Class.forName(className);
//				Log.d("TASK CONSTRUCT", className);
				Constructor<?> timerTaskConstructor = timerTaskObj.getConstructor(String.class);
				timerTask = (TimerTask) timerTaskConstructor.newInstance(mode);
				timer.schedule(timerTask, 1, interval);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} finally {
				this.status = "started";
//				Log.d("TASK STARTED", className);
			}
		} else {
//			Log.d("TASK ALREADY STARTED", className);
		}
	}

	public void stop() {
		invokePrimitive("stop");
		timerTask.cancel();
		this.status = "stopped";
//		Log.d("TASK STOPPED", className);
	}

	public void sleep() {
		if (status.equals("started")) {
			invokePrimitive("sleep");
			status = "sleep";
//			Log.d("TASK SLEEP", className);
		}
	}

	public void wakeUp() {
		if (status.equals("sleep")) {
			invokePrimitive("wakeUp");
			status = "started";
//			Log.d("TASK WAKEUP", className);
		}
	}

	public void changeMode(String mode) {
		try {
			timerTask.getClass().getDeclaredMethod("changeMode", String.class).invoke(timerTask, mode);
			this.mode = mode;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void invokePrimitive(String name) {
		try {
			timerTask.getClass().getDeclaredMethod(name).invoke(timerTask);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public Object getObj(String name) {
		Object result = new Object();
		try {
			result = timerTask.getClass().getDeclaredMethod(name).invoke(timerTask);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return result;
	}
}

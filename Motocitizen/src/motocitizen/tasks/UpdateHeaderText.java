package motocitizen.tasks;

import java.lang.reflect.InvocationTargetException;
import java.util.TimerTask;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import android.app.Activity;
import android.widget.TextView;

public class UpdateHeaderText extends TimerTask {

	private Activity act;
	private String status;

	public UpdateHeaderText(String mode) {
		super();
		act = (Activity) Startup.context;
		status = "first";
//		run();
//		Log.d("SERVICE STARTED", "UpdateStatus");
	}

	@Override
	public void run() {
		if (status.equals("started") || status.equals("first")) {
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					String metod = "getContent";
					if (status.equals("first")) {
						status = "started";
						metod = "getContentForce";
					}
					Class<?> providerObj;
					String newStatus = "";
					final TextView text = (TextView) act.findViewById(R.id.statusBarText);
					String currentStatus = (String) (text).getText();
					try {
						providerObj = Class.forName(Startup.props.get("task.updateheadertext.provider"));
						newStatus = (String) providerObj.getMethod(metod, String.class).invoke(null, currentStatus);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
					if (!newStatus.equals("") && !newStatus.equals(currentStatus)) {
						text.setText(newStatus);
					}
				}
			});
		}
	}

	public void stop() {
		status = "stopped";
//		Log.d("SERVICE STOPED", "UpdateStatus");
	}

	public void sleep() {
		status = "sleep";
//		Log.d("SERVICE SLEEP", "UpdateStatus");
	}

	public void wakeUp() {
		status = "started";
		run();
//		Log.d("SERVICE WAKEUP", "UpdateStatus");
	}
	
	public void changeMode(String mode) {
//		Log.d("SERVICE MODE CHANGE", "UpdateStatus");
		wakeUp();
	}
}

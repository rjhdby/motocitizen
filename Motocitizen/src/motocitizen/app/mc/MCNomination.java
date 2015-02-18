package motocitizen.app.mc;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class MCNomination {
	public static Location location;
	public MCNomination() {
	}

	public static String getContent(String current) {
		String result = current;
		Date date = new Date();
		location = getLocation();
		if ((location.getTime() - date.getTime()) < 1500000) {
			result = authInfo() + getAddress(location);
		}
		return result;
	}

	public static String getContentForce(String current) {
		location = getLocation();
		return authInfo() + getAddress(location);
	}

	private static String authInfo(){
		String name = Startup.prefs.getString("mc.login", "");
		if(name.equals("")){
			return "";
		}
		else{
			return name + ":";
		}
	}
	
	private static Location getLocation() {
		Location location = null;
		try {
			Class<?> actionObj;
			actionObj = Class.forName(Startup.tasks.tasks.get("locationservice").className);
			location = (Location) actionObj.getMethod("getLocation").invoke(null);
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
		return location;
	}

	public static String getAddress(Location location) {
		String result;
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		Map<String, String> post = new HashMap<String, String>();
		post.put("lat", String.valueOf(lat));
		post.put("lon", String.valueOf(lon));
		JSONObject json = new JSONCall("mcaccidents", "geocode").request(post);
		try {
			result = json.getString("address");
		} catch (JSONException e) {
			result = "Ошибка геокодирования";
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getAddress() {
		return getAddress(location);
	}

	public static Location getCoordinates(String address) {
		String URL = Startup.props.get("app.osm.provider.search");
		return new Location(URL);
	}
}

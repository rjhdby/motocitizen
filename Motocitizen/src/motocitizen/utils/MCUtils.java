package motocitizen.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.location.LocationManager;

public class MCUtils {
	public static boolean isInteger(String s) {
		return isInteger(s, 10);
	}

	public static boolean isInteger(String s, int radix) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), radix) < 0)
				return false;
		}
		return true;
	}

	public static LatLng LocationToLatLng(Location location) {
		return new LatLng(location.getLatitude(), location.getLongitude());
	}

	public static Location LatLngToLocation(LatLng latlng) {
		Location location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(latlng.latitude);
		location.setLongitude(latlng.longitude);
		location.setAccuracy(0);
		return location;
	}

	public static List<String> getPhonesFromText(String in) {
		List<String> out = new ArrayList<String>();
		String phonesString = in.replaceAll("[^0-9]", "");
		Matcher matcher = Pattern.compile("[7|8][0-9]{10}").matcher(phonesString);
		while (matcher.find()) {
			out.add(matcher.group());
		}
		return out;
	}

	public static String getIntervalFromNowInText(Date date) {
		return getIntervalFromNowInText(date, false);
	}

	public static String getIntervalFromNowInText(Date date, boolean full) {
		StringBuilder out = new StringBuilder();
		Date now = new Date();
		int minutes = (int) (now.getTime() - date.getTime()) / (60000);
		if (minutes <= 0) {
			return "Только что";
		}
		int hours = (int) minutes / 60;
		minutes -= hours * 60;
		int min = minutes % 10;
		if (full) {
			if (hours == 1 || hours == 21) {
				out.append(String.valueOf(hours) + " час ");
			} else if (hours == 2 || hours == 3 || hours == 4 || hours == 22 || hours == 23 || hours == 24) {
				out.append(String.valueOf(hours) + " часа ");
			} else if (hours > 4 && hours < 21) {
				out.append(String.valueOf(hours) + " часов ");
			}
			if (minutes > 10 && minutes < 20) {
				out.append(String.valueOf(minutes) + " минут");
			} else if (min == 1) {
				out.append(String.valueOf(minutes) + " минуту");
			} else if (min > 1 && min < 5) {
				out.append(String.valueOf(minutes) + " минуты");
			} else if (min > 4 || min == 0) {
				out.append(String.valueOf(minutes) + " минут");
			}
			out.append(" назад");
		} else {
			out.append((String.valueOf(hours) + "ч "));
			out.append((String.valueOf(minutes) + "м"));
		}
		return out.toString();
	}

	public static String getStringTime(Date date) {
		return getStringTime(date, false);
	}

	public static String getStringTime(Date date, boolean full) {
		String out = "";
		if (full) {
			out = Const.fullTimeFormat.format(date);
		} else {
			out = Const.timeFormat.format(date);
		}
		return out;
	}
}

package motocitizen.utils;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtils {
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
        List<String> out = new ArrayList<>();
        in = in + ".";
        Matcher matcher = Pattern.compile("[7|8][ \\(-]?[\\d]{3}[ \\)-]?[\\d]{3}[ -]?[\\d]{2}[ -]?[\\d]{2}[\\D]").matcher(in);
        while (matcher.find()) {
            out.add("+7" + matcher.group().replaceAll("[^0-9]", "").substring(1));
        }
        return out;
    }

    public static String getIntervalFromNowInText(Date date) {
        return getIntervalFromNowInText(date, false);
    }

    public static String getIntervalFromNowInText(Date date, boolean full) {
        StringBuilder out     = new StringBuilder();
        Date          now     = new Date();
        int           minutes = (int) (now.getTime() - date.getTime()) / (60000);
        if (minutes <= 0) return "Только что";

        int hours = minutes / 60;
        minutes -= hours * 60;
        int min = minutes % 10;
        if (full) {
            switch (hours) {
                case 1:
                case 21:
                    out.append(String.valueOf(hours)).append(" час ");
                    break;
                case 2:
                case 3:
                case 4:
                case 22:
                case 23:
                case 24:
                    out.append(String.valueOf(hours)).append(" часа ");
                    break;
                default:
                    out.append(String.valueOf(hours)).append(" часов ");
            }
            if (minutes > 10 && minutes < 20) {
                out.append(String.valueOf(minutes)).append(" минут");
            } else if (min == 1) {
                out.append(String.valueOf(minutes)).append(" минуту");
            } else if (min > 1 && min < 5) {
                out.append(String.valueOf(minutes)).append(" минуты");
            } else if (min > 4 || min == 0) {
                out.append(String.valueOf(minutes)).append(" минут");
            }
            out.append(" назад");
        } else {
            out.append(String.valueOf(hours)).append("ч ");
            out.append(String.valueOf(minutes)).append("м");
        }
        return out.toString();
    }

    public static String getStringTime(Date date, boolean full) {
        return full ? Const.FULL_TIME_FORMAT.format(date) : Const.TIME_FORMAT.format(date);
    }

    public static String getStringTime(Date date) {
        return getStringTime(date, false);
    }

    public static int newId() {
        final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
        if (Build.VERSION.SDK_INT < 17) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1;
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }
}

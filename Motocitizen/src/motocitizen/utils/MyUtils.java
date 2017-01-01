package motocitizen.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import motocitizen.main.R;

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

    @NonNull
    public static String getIntervalFromNowInText(Context context, Date date) {
        Date now     = new Date();
        int  minutes = (int) ((now.getTime() - date.getTime()) / 60000);
        if (minutes == 0) return "Только что";
        return context.getResources().getString(R.string.time_interval_short, minutes / 60, minutes % 60);
    }

    public static String getStringTime(Date date, boolean full) {
        return full ? Const.FULL_TIME_FORMAT.format(date) : Const.TIME_FORMAT.format(date);
    }

    public static int newId() {
        final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
        if (Build.VERSION.SDK_INT < 17) {
            for (; ; ) {
                final int result   = sNextGeneratedId.get();
                int       newValue = result + 1;
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

package motocitizen.geocoder;

import android.content.Context;
import android.location.Geocoder;

/**
 * Created by rjhdby on 14.01.17.
 */

public class MyGeocoder {

    private static class Holder {
        private static Geocoder instance;
    }

    public static Geocoder getInstance() {
        return Holder.instance;
    }

    public static void init(Context context) {
        Holder.instance = new Geocoder(context);
    }

    private MyGeocoder() {
    }
}

package motocitizen.geocoder;

import android.content.Context;
import android.location.Geocoder;

public class MyGeoCoder {

    private static class Holder {
        private static Geocoder instance;
    }

    public static Geocoder getInstance() {
        return Holder.instance;
    }

    public static void init(Context context) {
        Holder.instance = new Geocoder(context);
    }

    private MyGeoCoder() {
    }

//    public static LatLng latLngByAddress(String) {
//
//    }
}

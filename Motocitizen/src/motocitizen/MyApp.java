package motocitizen;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import motocitizen.app.general.user.Auth;
import motocitizen.startup.MyPreferences;
import motocitizen.utils.Props;

public class MyApp extends Application {

    private final MyApp instance;
    public  MyPreferences prefs = null;
    private Props         props = null;
    private Auth          auth  = null;
    private Geocoder      geocoder = null;

    public MyApp() {
        instance = this;
    }

    public MyPreferences getPreferences() {
        if (prefs == null)
            prefs = new MyPreferences(getApplicationContext());
        return prefs;
    }

    public Props getProps() {
        if(props == null)
            props = new Props(instance);
        return props;
    }

    public Auth getMCAuth() {
        if(auth == null )
            auth = new Auth(instance);
        return auth;
    }

    public String getAddres(Location location) {
        if (geocoder == null) {
            geocoder = new Geocoder(getApplicationContext());
        }
        StringBuilder res = new StringBuilder();
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address addr = list.get(0);
            String locality = addr.getLocality();
            if (locality == null)
                locality = addr.getAdminArea();
            if (locality == null && addr.getMaxAddressLineIndex() > 0)
                locality = addr.getAddressLine(0);

            String thoroughfare = addr.getThoroughfare();
            if (thoroughfare == null)
                thoroughfare = addr.getSubAdminArea();

            String featureName = addr.getFeatureName();

            if (locality != null)
                res.append(locality);
            if (thoroughfare != null) {
                if (res.length() > 0)
                    res.append(" ");
                res.append(thoroughfare);
            }
            if (featureName != null)
                if (res.length() > 0)
                    res.append(" ");
            res.append(featureName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }
}

package motocitizen;

import android.app.Application;
import motocitizen.startup.MCPreferences;

/**
 * Created by pavel on 12.05.15.
 */
public class MyApp extends Application {

    private MyApp instance;
    public MCPreferences prefs = null;
    public MyApp() {
        instance = this;
    }

    public MCPreferences getPreferences() {
        if(prefs == null)
            prefs = new MCPreferences(getApplicationContext());
        return prefs;
    }
}

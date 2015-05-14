package motocitizen;

import android.app.Application;

import motocitizen.app.general.user.Auth;
import motocitizen.startup.MyPreferences;
import motocitizen.utils.Props;

public class MyApp extends Application {

    private MyApp instance;
    public MyPreferences prefs = null;
    private Props props = null;
    private Auth auth = null;

    public MyApp() {
        instance = this;
    }

    public MyPreferences getPreferences() {
        if(prefs == null)
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
}

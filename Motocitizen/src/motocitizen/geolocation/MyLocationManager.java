package motocitizen.geolocation;

public class MyLocationManager {

    public static boolean permissionRequested = false;
    public static boolean permissionStatus = false;

    private MyLocationManager() {
    }

    private static class Holder {
        private static SecuredLocationManagerInterface instance;
    }

    public static void init(boolean real) {
        permissionStatus = real;
        if (real) Holder.instance = new NormalLocationManager();
        else Holder.instance = new FakeLocationManager();
    }

    public static SecuredLocationManagerInterface getInstance() {
        if (Holder.instance == null) {
            init(permissionStatus);
            return Holder.instance;
        } else {
            return Holder.instance;
        }
    }
}

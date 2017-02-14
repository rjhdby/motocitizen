package motocitizen.geolocation;

public class MyLocationManager {

    public static boolean permissionRequested = false;

    private MyLocationManager() {
    }

    private static class Holder {
        private static SecuredLocationManagerInterface instance;
    }

    public static void init(boolean real) {
        if (real) Holder.instance = new NormalLocationManager();
        else Holder.instance = new FakeLocationManager();
    }

    public static SecuredLocationManagerInterface getInstance() {
        return Holder.instance;
    }
}

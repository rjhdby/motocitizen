package motocitizen.router;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import motocitizen.activity.AboutActivity;
import motocitizen.activity.AccidentDetailsActivity;
import motocitizen.activity.AuthActivity;
import motocitizen.activity.BusinessCardActivity;
import motocitizen.activity.CreateAccActivity;
import motocitizen.activity.MainScreenActivity;
import motocitizen.activity.SettingsActivity;
import motocitizen.activity.StartupActivity;


public class Router {
    public enum Target {
        ABOUT(AboutActivity.class),
        DETAILS(AccidentDetailsActivity.class),
        AUTH(AuthActivity.class),
        BUSINESS_CARD(BusinessCardActivity.class),
        CREATE(CreateAccActivity.class),
        MAIN(MainScreenActivity.class),
        SETTINGS(SettingsActivity.class),
        STARTUP(StartupActivity.class);

        private final Class activity;

        Target(Class activity) {
            this.activity = activity;
        }

        public Class getActivity() {
            return activity;
        }
    }

    public static void goTo(Activity activity, Target target) {
        goTo(activity, target, new Bundle());
    }

    public static void goTo(Activity activity, Target target, Bundle bundle) {
        Intent intent = new Intent(activity, target.getActivity());
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void dial(Activity activity, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+" + phone));
        activity.startActivity(intent);
    }

    public static void sms(Activity activity, String phone) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:" + phone));
        activity.startActivity(intent);
    }

    public static void share(Activity activity, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

    //TODO EXTERMINATUS!!!!
    public static void exit(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        activity.startActivity(intent);
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    public static void toExternalMap(Activity activity, LatLng latLng) {
        String uri    = "geo:" + latLng.latitude + "," + latLng.longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        activity.startActivity(intent);
    }
}

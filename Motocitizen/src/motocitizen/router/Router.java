package motocitizen.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Stack;

import motocitizen.activity.AboutActivity;
import motocitizen.activity.AccidentDetailsActivity;
import motocitizen.activity.AuthActivity;
import motocitizen.activity.BusinessCardActivity;
import motocitizen.activity.CreateAccActivity;
import motocitizen.activity.MainScreenActivity;
import motocitizen.activity.SettingsActivity;
import motocitizen.activity.StartupActivity;

import static motocitizen.router.Router.Target.BUSINESS_CARD;
import static motocitizen.router.Router.Target.DETAILS;
import static motocitizen.router.Router.Target.MAIN;

/**
 * Created by rjhdby on 31.01.2017.
 */

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

        private Class activity;

        Target(Class activity) {
            this.activity = activity;
        }

        public Class getActivity() {
            return activity;
        }

        public static Target getInstance(Class activity) {
            for (Target target : values()) {
                if (target.activity.equals(activity)) return target;
            }
            return MAIN;
        }
    }

    private static Stack<Target> history = new Stack<>();

    public static void goTo(Activity activity, Target target) {
        goTo(activity, target, new Bundle());
    }

    public static void goTo(Activity activity, Target target, Bundle bundle) {
        updateHistory(activity, target);
        Intent intent = new Intent(activity, target.getActivity());
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void back(Activity activity) {
        if (history.isEmpty()) {
            activity.moveTaskToBack(true);
            return;
        }
        goTo(activity, history.pop());
    }

    private static void updateHistory(Activity activity, Target target) {
        Target source = Target.getInstance(activity.getClass());
        if (target == BUSINESS_CARD || source == MAIN) {
            history.push(target);
        }
        if (target != MAIN && source == DETAILS) {
            history.push(target);
        }
    }
}

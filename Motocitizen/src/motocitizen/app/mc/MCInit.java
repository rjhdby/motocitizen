package motocitizen.app.mc;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import motocitizen.app.mc.user.MCAuth;
import motocitizen.app.mc.user.MCRole;
import motocitizen.main.R;
import motocitizen.startup.Startup;

public class MCInit {
    public static void setupAccess(Context context, MCAuth auth) {
        ImageButton createButton = (ImageButton) ((Activity) context).findViewById(R.id.mc_add_point_button);

        if (MCRole.isStandart()) {
            createButton.setVisibility(View.VISIBLE);
        } else {
            createButton.setVisibility(View.INVISIBLE);
        }

        if (auth.anonim) {
            //anonimCheckBox.setChecked(true);
            //loginField.setVisibility(View.INVISIBLE);
            //passwordField.setVisibility(View.INVISIBLE);
            auth.reset();
        } else {
            //anonimCheckBox.setChecked(false);
            //loginField.setVisibility(View.VISIBLE);
            //passwordField.setVisibility(View.VISIBLE);
        }
    }

    public static void setupValues(MCAuth auth) {
        Startup.prefs.edit().putString("mc.name", auth.name).commit();
    }
}

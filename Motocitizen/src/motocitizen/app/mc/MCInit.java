package motocitizen.app.mc;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Properties;

import motocitizen.app.mc.user.MCAuth;
import motocitizen.app.mc.user.MCRole;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.InputFilterMinMax;
import motocitizen.utils.Props;
import motocitizen.utils.Text;

public class MCInit {
//    public static void readProperties() {
//        MCObjects.mapContainer.setTranslationX(Const.width);
//    }

    public static void addListeners() {

        //MCObjects.firstLoginButton.setOnClickListener(MCListeners.firstloginButtonListener);
        //MCObjects.authAnonimCheckBox.setOnCheckedChangeListener(MCListeners.authAnonimCheckBoxListener);

        //MCObjects.mainTabsGroup.setOnCheckedChangeListener(MCListeners.mainTabsListener);
//        MCObjects.valueAppMcaccidentsDistance.setFilters(new InputFilter[]{new InputFilterMinMax(0, 20050)});
//        MCObjects.valueAppMcaccidentsDistanceAlarm.setFilters(new InputFilter[]{new InputFilterMinMax(0, 20050)});
    }

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

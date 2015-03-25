package motocitizen.core.settings;

import android.app.Activity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import motocitizen.app.mc.MCAccidents;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Keyboard;
import motocitizen.utils.Show;

public class SettingsMenu {
    private static final Activity act = (Activity) Startup.context;
    private static final View includeArea = act.findViewById(R.id.settings_include_area);

    public SettingsMenu() {
        addListeners();
        setValues();
    }

    public static void open() {
        toSettings();
        setValues();
    }

    public static void refresh() {
        setValues();
    }

    private static void cancel() {
        for (String key : Startup.prefs.getAll().keySet()) {
            View v = includeArea.findViewWithTag(key);
            if (v instanceof EditText) {
                Keyboard.hide(v);
            }
        }
        Show.show(R.id.main_frame_applications);
    }

    private static void toSettings() {
        Show.show(R.id.main_frame_settings);
    }

    private static void addListeners() {
        Button confirm = (Button) act.findViewById(R.id.settings_confirm_button);
        confirm.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                submit();
            }
        });
        Button cancel = (Button) act.findViewById(R.id.settings_cancel_button);
        cancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private static void setValues() {
        for (String key : Startup.prefs.getAll().keySet()) {
            View v = includeArea.findViewWithTag(key);
            if (v != null) {
                if (v instanceof CheckBox) {
                    ((CheckBox) v).setChecked(Startup.prefs.getBoolean(key, true));
                    Log.d("PREFS", key + "=" + String.valueOf(Startup.prefs.getBoolean(key, true)));
                } else if (v instanceof EditText) {
                    EditText e = (EditText) v;
                    if (e.getInputType() == InputType.TYPE_CLASS_NUMBER) {
                        e.setText(String.valueOf(Startup.prefs.getInt(key, 0)));
                        Log.d("PREFS", key + "=" + String.valueOf(Startup.prefs.getInt(key, 0)));
                    } else {
                        e.setText(Startup.prefs.getString(key, ""));
                        Log.d("PREFS", key + "=" + Startup.prefs.getString(key, ""));
                    }
                } else if (v instanceof TextView) {
                    ((TextView) v).setText(Startup.prefs.getString(key, ""));
                    Log.d("PREFS", key + "=" + Startup.prefs.getString(key, ""));
                }
            }
        }
    }

    private static void submit() {
        for (String key : Startup.prefs.getAll().keySet()) {
            View v = includeArea.findViewWithTag(key);
            if (v != null) {
                if (v instanceof CheckBox) {
                    Startup.prefs.edit().putBoolean(key, ((CheckBox) v).isChecked()).commit();
                } else if (v instanceof EditText) {
                    EditText e = (EditText) v;
                    if (e.getInputType() == InputType.TYPE_CLASS_NUMBER) {
                        Startup.prefs.edit().putInt(key, Integer.parseInt(e.getText().toString())).commit();
                    } else {
                        Startup.prefs.edit().putString(key, e.getText().toString()).commit();
                    }
                }
            }
        }
        MCAccidents.redraw(Startup.context);
        cancel();
    }
}

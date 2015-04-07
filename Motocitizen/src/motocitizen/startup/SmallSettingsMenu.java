package motocitizen.startup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import motocitizen.Activity.AboutActivity;
import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.Activity.SettingsFragment;
import motocitizen.app.mc.MCAccidents;
// zz
// import motocitizen.core.settings.SettingsMenu;
import motocitizen.main.R;
import motocitizen.network.IncidentRequest;
import motocitizen.network.JsonRequest;

@SuppressLint("RtlHardcoded")
public class SmallSettingsMenu {
    private static PopupMenu popupTR;
    public static PopupMenu popupBL;

    public SmallSettingsMenu() {
        final Activity act = (Activity) Startup.context;
        ImageButton b = (ImageButton) act.findViewById(R.id.statusBarButton);
        View v = act.findViewById(R.id.footer_menu_anchor);
        popupBL = new PopupMenu(act, v, Gravity.LEFT);
        popupTR = new PopupMenu(act, b);
        popupBL.getMenuInflater().inflate(R.menu.small_settings_menu, popupBL.getMenu());
        popupTR.getMenuInflater().inflate(R.menu.small_settings_menu, popupTR.getMenu());
        OnMenuItemClickListener popupListener = new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.small_menu_refresh) {
                    MCAccidents.refresh(Startup.context);
                    if (Startup.isOnline()) {
                        JsonRequest request = MCAccidents.getLoadPointsRequest();
                        if (request != null) {
                            (new IncidentRequest(Startup.context)).execute(request);
                        }
                    } else {
                        Toast.makeText(Startup.context, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
                    }
                } else if (id == R.id.small_menu_settings) {
/*
                    Intent i = new Intent(act, ConfigActivity.class);
                    Startup.context.startActivity(i);
*/

                    FragmentManager fm = ((Activity) Startup.context).getFragmentManager();
                    Fragment pf, mf;
                    pf = fm.findFragmentByTag("settings");
                    mf = fm.findFragmentByTag("main_screen");
                    if(pf == null){
                        pf = new SettingsFragment();
                        fm.beginTransaction().add(R.id.main_frame,pf,"settings").commit();
                    }
                    fm.beginTransaction().hide(mf).show(pf).commit();
                } else if (id == R.id.small_menu_about) {
                    Intent i = new Intent(act, AboutActivity.class);
                    Startup.context.startActivity(i);
                } else if (id == R.id.show_acc_details) {
                    Intent intent = new Intent(act, AccidentDetailsActivity.class);
                    Startup.context.startActivity(intent);
                } else if (id == R.id.small_menu_exit) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    Startup.context.startActivity(intent);
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                }
                return true;
            }
        };
        popupTR.setOnMenuItemClickListener(popupListener);
        popupBL.setOnMenuItemClickListener(popupListener);

        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupTR.show();
            }
        });
    }
}

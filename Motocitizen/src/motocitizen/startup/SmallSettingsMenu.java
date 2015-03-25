package motocitizen.startup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import motocitizen.app.mc.MCAccidents;
import motocitizen.core.settings.SettingsMenu;
import motocitizen.main.R;

@SuppressLint("RtlHardcoded")
public class SmallSettingsMenu {
    private static PopupMenu popupTR;
    public static PopupMenu popupBL;

    public SmallSettingsMenu() {
        Activity act = (Activity) Startup.context;
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
                } else if (id == R.id.small_menu_settings) {

                    SettingsMenu.open();
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

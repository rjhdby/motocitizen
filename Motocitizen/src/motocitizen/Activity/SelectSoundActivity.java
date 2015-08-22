package motocitizen.Activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import motocitizen.MyApp;
import motocitizen.main.R;
import motocitizen.startup.Preferences;
import motocitizen.utils.Const;
import motocitizen.utils.NewID;

public class SelectSoundActivity extends ActionBarActivity {
    private static Map<Integer, Uri> notifications;
    private static int               currentId;
    private static ViewGroup         vg;
    private static Uri               currentUri;
    private static String            currentTitle;
    private static RingtoneManager   rm;
    private        Preferences       prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_sound);
        prefs = ((MyApp) context.getApplicationContext()).getPreferences();
        vg = (ViewGroup) findViewById(R.id.sound_select_table);
        rm = new RingtoneManager(this);
        rm.setType(RingtoneManager.TYPE_NOTIFICATION);
        currentUri = Preferences.getAlarmSoundUri();
        currentTitle = Preferences.getAlarmSoundTitle();

        Button selectSoundConfirmButton = (Button) findViewById(R.id.select_sound_save_button);
        selectSoundConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTitle.equals("default system")) {
                    Preferences.setDefaultSoundAlarm();
                } else Preferences.setSoundAlarm(currentTitle, currentUri);
                finish();
            }
        });

        Button selectSoundCancelButton = (Button) findViewById(R.id.select_sound_cancel_button);
        selectSoundCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawList(this);
    }

    private void drawList(Context context) {
        currentId = 0;
        notifications = new HashMap<>();
        Cursor cursor = rm.getCursor();
        if (cursor.getCount() == 0 && !cursor.moveToFirst()) {
            return;
        }
        while (!cursor.isAfterLast() && cursor.moveToNext()) {
            int currentPosition = cursor.getPosition();
            inflateRow(context, vg, currentPosition);
        }
    }

    private void inflateRow(final Context context, ViewGroup viewGroup, int currentPosition) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow       tr = (TableRow) li.inflate(R.layout.sound_row, viewGroup, false);
        tr.setId(NewID.id());
        ((TextView) tr.findViewById(R.id.sound)).setText(rm.getRingtone(currentPosition).getTitle(context));
        notifications.put(tr.getId(), rm.getRingtoneUri(currentPosition));
        tr.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentId != 0) {
                    vg.findViewById(currentId).setBackgroundColor(Const.getDefaultBGColor(context));
                }
                currentId = v.getId();
                vg.findViewById(currentId).setBackgroundColor(Color.GRAY);
                Ringtone current = RingtoneManager.getRingtone(v.getContext(), notifications.get(v.getId()));
                current.play();
                currentUri = notifications.get(v.getId());
                currentTitle = current.getTitle(v.getContext());
            }
        });
        viewGroup.addView(tr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}

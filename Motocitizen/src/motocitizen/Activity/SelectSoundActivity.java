package motocitizen.Activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCObjects;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.NewID;

public class SelectSoundActivity extends ActionBarActivity {

    private static Map<Integer, Uri> notifications;
    private static int currentId;
    private static ViewGroup vg;
    private static String currentUri;
    private static String currentTitle;
    private static RingtoneManager rm;

    private static Button selectSoundConfirmButton;
    private static Button selectSoundCancelButton;

    private final Button.OnClickListener play = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (currentId != 0) {
                vg.findViewById(currentId).setBackgroundColor(Const.defaultBGColor);
            }
            currentId = v.getId();
            vg.findViewById(currentId).setBackgroundColor(Color.GRAY);
            Ringtone current = RingtoneManager.getRingtone(v.getContext(), notifications.get(v.getId()));
            current.play();
            currentUri = notifications.get(v.getId()).toString();
            currentTitle = current.getTitle(v.getContext());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mc_select_sound);

        vg = (ViewGroup)findViewById(R.id.sound_select_table);
        rm = new RingtoneManager(this);
        rm.setType(RingtoneManager.TYPE_NOTIFICATION);
        String defaultUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION).toString();
        currentUri = Startup.prefs.getString("mc.notification.sound", defaultUri);
        currentTitle = Startup.prefs.getString("mc.notification.sound.title", "default system");

        selectSoundConfirmButton = (Button) findViewById(R.id.select_sound_save_button);
        selectSoundConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Startup.prefs.edit().putString("mc.notification.sound", currentUri).commit();
                Startup.prefs.edit().putString("mc.notification.sound.title", currentTitle).commit();
                finish();
            }
        });

        selectSoundCancelButton = (Button) findViewById(R.id.select_sound_cancel_button);
        selectSoundCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });;
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

            TableRow tr = createRow(context, rm.getRingtone(currentPosition).getTitle(context));
            int id = tr.getId();
            notifications.put(id, rm.getRingtoneUri(currentPosition));
            vg.addView(tr);
            vg.addView(createDelimiter(context));
        }
    }

    private TableRow createRow(Context context, String title) {
        TableRow tr = new TableRow(context);
        TextView tv = new TextView(tr.getContext());
        tr.setId(NewID.id());
        tr.setLayoutParams(Const.trlp);
        tv.setLayoutParams(Const.trlp);
        tv.setLines(2);
        tv.setText(title);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tr.addView(tv);
        tr.setOnClickListener(play);
        return tr;
    }

    private TableRow createDelimiter(Context context) {
        TableRow tr = new TableRow(context);
        TextView tv = new TextView(tr.getContext());
        tr.setLayoutParams(Const.trlp);
        tv.setLayoutParams(Const.trlp);
        tv.setHeight(2);
        tv.setBackgroundColor(Color.GRAY);
        tr.addView(tv);
        return tr;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_select_sound, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}

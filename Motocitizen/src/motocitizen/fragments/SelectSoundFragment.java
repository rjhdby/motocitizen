package motocitizen.fragments;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.utils.Preferences;
import motocitizen.utils.Const;

public class SelectSoundFragment extends Fragment {
    private static Map<Integer, Sound> notifications;
    private static ViewGroup           ringtoneList;
    private static int                 currentId;
    private static Uri                 currentUri;
    private static String              currentTitle;

    static {
        currentId = 0;
        currentUri = Preferences.getAlarmSoundUri();
        currentTitle = Preferences.getAlarmSoundTitle();
    }

    private class Sound {
        private final Uri      uri;
        private final Ringtone ringtone;

        public Sound(Uri uri, Ringtone ringtone) {
            this.uri = uri;
            this.ringtone = ringtone;
        }

        public Uri getUri() {
            return uri;
        }

        public String getTitle() {
            return ringtone.getTitle(getActivity());
        }

        public void play() {
            ringtone.play();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.select_sound_fragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        ringtoneList = (ViewGroup) getActivity().findViewById(R.id.sound_select_table);
        getActivity().findViewById(R.id.select_sound_fragment).setVisibility(View.VISIBLE);
        if (notifications == null) getSystemSounds();

        Button selectSoundConfirmButton = (Button) getActivity().findViewById(R.id.select_sound_save_button);
        selectSoundConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTitle.equals("default system")) {
                    Preferences.setDefaultSoundAlarm();
                } else Preferences.setSoundAlarm(currentTitle, currentUri);
                finish();
            }
        });

        Button selectSoundCancelButton = (Button) getActivity().findViewById(R.id.select_sound_cancel_button);
        selectSoundCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        drawList();
    }

    private void getSystemSounds() {
        RingtoneManager rm = new RingtoneManager(getActivity());
        rm.setType(RingtoneManager.TYPE_NOTIFICATION);
        notifications = new HashMap<>();
        Cursor cursor = rm.getCursor();
        if (cursor.getCount() == 0 && !cursor.moveToFirst()) return;
        while (!cursor.isAfterLast() && cursor.moveToNext()) {
            int currentPosition = cursor.getPosition();
            notifications.put(currentPosition, new Sound(rm.getRingtoneUri(currentPosition), rm.getRingtone(currentPosition)));
        }
    }

    private void drawList() {
        for (int key : notifications.keySet()) {
            inflateRow(ringtoneList, key);
        }
    }

    private void inflateRow(ViewGroup viewGroup, int currentPosition) {
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow       tr = (TableRow) li.inflate(R.layout.sound_row, viewGroup, false);
        tr.setTag(currentPosition);

        ((TextView) tr.findViewById(R.id.sound)).setText(notifications.get(currentPosition).getTitle());

        tr.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                int tag = (Integer) v.getTag();
                if (currentId != 0) {
                    ringtoneList.findViewWithTag(currentId).setBackgroundColor(Const.getDefaultBGColor());
                }
                currentId = tag;
                v.setBackgroundColor(Color.GRAY);
                notifications.get(tag).play();
                currentUri = notifications.get(tag).getUri();
                currentTitle = notifications.get(tag).getTitle();
            }
        });
        viewGroup.addView(tr);
    }

    private void finish() {
        getActivity().findViewById(R.id.select_sound_fragment).setVisibility(View.GONE);
        getFragmentManager().beginTransaction().remove(this).replace(android.R.id.content, new SettingsFragment()).commit();
    }
}

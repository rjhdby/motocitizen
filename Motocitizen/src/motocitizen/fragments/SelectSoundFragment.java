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
import motocitizen.utils.Const;
import motocitizen.utils.Preferences;

public class SelectSoundFragment extends Fragment {
    private Map<Integer, Sound> notifications;
    private ViewGroup           ringtoneList;
    private int                 currentId;
    private Uri                 currentUri;
    private String              currentTitle;

    {
        currentId = 0;
        currentUri = Preferences.getAlarmSoundUri();
        currentTitle = Preferences.getAlarmSoundTitle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.select_sound_fragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.select_sound_fragment).setVisibility(View.VISIBLE);
        if (notifications == null) getSystemSounds();

        ringtoneList = (ViewGroup) getActivity().findViewById(R.id.sound_select_table);
        Button selectSoundConfirmButton = (Button) getActivity().findViewById(R.id.select_sound_save_button);
        Button selectSoundCancelButton  = (Button) getActivity().findViewById(R.id.select_sound_cancel_button);

        selectSoundConfirmButton.setOnClickListener(new ConfirmButtonListener());
        selectSoundCancelButton.setOnClickListener(new CancelButtonListener());

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
        tr.setOnClickListener(new SoundRowClickListener());

        ((TextView) tr.findViewById(R.id.sound)).setText(notifications.get(currentPosition).getTitle());
        viewGroup.addView(tr);
    }

    private void finish() {
        getActivity().findViewById(R.id.select_sound_fragment).setVisibility(View.GONE);
        getFragmentManager().beginTransaction().remove(this).replace(android.R.id.content, new SettingsFragment()).commit();
    }

    private class SoundRowClickListener implements View.OnClickListener {
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
    }

    private class CancelButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class ConfirmButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (currentTitle.equals("default system")) {
                Preferences.setDefaultSoundAlarm();
            } else Preferences.setSoundAlarm(currentTitle, currentUri);
            finish();
        }
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
}

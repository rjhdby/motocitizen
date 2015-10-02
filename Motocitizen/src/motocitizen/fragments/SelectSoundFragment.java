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
import motocitizen.startup.Preferences;
import motocitizen.utils.Const;

public class SelectSoundFragment extends Fragment {
    private static Map<Integer, Uri> notifications;
    private static int               currentId;
    private static ViewGroup         vg;
    private static Uri               currentUri;
    private static String            currentTitle;
    private static RingtoneManager   rm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.select_sound_fragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        vg = (ViewGroup) getActivity().findViewById(R.id.sound_select_table);
        getActivity().findViewById(R.id.select_sound_fragment).setVisibility(View.VISIBLE);
        rm = new RingtoneManager(getActivity());
        rm.setType(RingtoneManager.TYPE_NOTIFICATION);
        currentUri = Preferences.getAlarmSoundUri();
        currentTitle = Preferences.getAlarmSoundTitle();

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

    private void drawList() {
        currentId = 0;
        notifications = new HashMap<>();
        Cursor cursor = rm.getCursor();
        if (cursor.getCount() == 0 && !cursor.moveToFirst()) return;
        while (!cursor.isAfterLast() && cursor.moveToNext()) {
            int currentPosition = cursor.getPosition();
            inflateRow(vg, currentPosition);
        }
    }

    private void inflateRow(ViewGroup viewGroup, int currentPosition) {
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow       tr = (TableRow) li.inflate(R.layout.sound_row, viewGroup, false);
        tr.setTag(currentPosition);
        ((TextView) tr.findViewById(R.id.sound)).setText(rm.getRingtone(currentPosition).getTitle(getActivity()));
        notifications.put(currentPosition, rm.getRingtoneUri(currentPosition));
        tr.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                int tag = (Integer) v.getTag();
                if (currentId != 0) {
                    vg.findViewWithTag(currentId).setBackgroundColor(Const.getDefaultBGColor());
                }
                currentId = tag;
                v.setBackgroundColor(Color.GRAY);
                Ringtone current = RingtoneManager.getRingtone(v.getContext(), notifications.get(tag));
                current.play();
                currentUri = notifications.get(tag);
                currentTitle = current.getTitle(v.getContext());
            }
        });
        viewGroup.addView(tr);
    }

    private void finish() {
        getActivity().findViewById(R.id.select_sound_fragment).setVisibility(View.GONE);
        getFragmentManager().beginTransaction().remove(this).replace(android.R.id.content, new SettingsFragment()).commit();
    }
}

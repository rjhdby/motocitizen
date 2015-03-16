package motocitizen.app.mc;

import java.util.HashMap;
import java.util.Map;

import motocitizen.core.settings.SettingsMenu;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.NewID;
import motocitizen.utils.Show;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class MCSelectSound {
	public static Map<Integer, Uri> notifications;
	public static int currentId;
	private static ViewGroup vg;
	private static String currentUri;
	private static String currentTitle;
	private static RingtoneManager rm;
	private static Boolean firstrun;
	private static Button save, cancel;

	public MCSelectSound(Context context) {
		vg = (ViewGroup) ((Activity) context).findViewById(R.id.sound_select_table);
		rm = new RingtoneManager(context);
		rm.setType(RingtoneManager.TYPE_NOTIFICATION);
		String defaultUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION).toString();
		currentUri = Startup.prefs.getString("mc.notification.sound", defaultUri);
		currentTitle = Startup.prefs.getString("mc.notification.sound.title", "default system");
		if (firstrun == null) {
			drawList(context);
			firstrun = true;
		}
		save = (Button) ((Activity) context).findViewById(R.id.select_sound_save_button);
		cancel = (Button) ((Activity) context).findViewById(R.id.select_sound_cancel_button);
		save.setOnClickListener(saveListener);
		cancel.setOnClickListener(cancelListener);
	}

	private void drawList(Context context) {
		currentId = 0;
		notifications = new HashMap<Integer, Uri>();
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

	private Button.OnClickListener play = new Button.OnClickListener() {

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

	private static Button.OnClickListener saveListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			Startup.prefs.edit().putString("mc.notification.sound", currentUri).commit();
			Startup.prefs.edit().putString("mc.notification.sound.title", currentTitle).commit();
			SettingsMenu.refresh();
			Show.showLast();
		}
	};

	private static Button.OnClickListener cancelListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			Show.showLast();
		}
	};
}

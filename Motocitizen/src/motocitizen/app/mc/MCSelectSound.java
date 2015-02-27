package motocitizen.app.mc;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import motocitizen.core.settings.SettingsMenu;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.AlarmsList;
import motocitizen.utils.Const;
import motocitizen.utils.NewID;
import motocitizen.utils.Show;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class MCSelectSound {
	public static Map<Integer, File> sounds;
	private static MediaPlayer mp;
	private static int defaultId;
	public static int currentId;
	private static ViewGroup vg;
	private static Handler handler;
	private static String currentFile;

	public MCSelectSound() {
		vg = (ViewGroup) ((Activity) Startup.context).findViewById(R.id.sound_select_table);
		sounds = new HashMap<Integer, File>();
		handler = new Handler();
		currentId = 0;
		currentFile = "default system";
		drawList();
		Button save = (Button) ((Activity) Startup.context).findViewById(R.id.select_sound_save_button);
		Button cancel = (Button) ((Activity) Startup.context).findViewById(R.id.select_sound_cancel_button);
		save.setOnClickListener(saveListener);
		cancel.setOnClickListener(cancelListener);
	}

	private void drawList() {
		vg.addView(makeDefault());
		vg.addView(createDelimiter());
		List<File> lf = AlarmsList.getList();
		for (File f : lf) {
			TableRow tr = createRow(f);
			int id = tr.getId();
			sounds.put(id, f);
			vg.addView(tr);
			vg.addView(createDelimiter());
		}
	}

	private TableRow createRow(File f) {
		TableRow tr = new TableRow(Startup.context);
		TextView tv = new TextView(tr.getContext());
		tr.setId(NewID.id());
		tr.setLayoutParams(Const.trlp);
		tv.setLayoutParams(Const.trlp);
		tv.setLines(2);
		tv.setText(f.getName());
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tr.addView(tv);
		tr.setOnClickListener(play);
		return tr;
	}

	private TableRow createDelimiter() {
		TableRow tr = new TableRow(Startup.context);
		TextView tv = new TextView(tr.getContext());
		tr.setLayoutParams(Const.trlp);
		tv.setLayoutParams(Const.trlp);
		tv.setHeight(2);
		tv.setBackgroundColor(Color.GRAY);
		tr.addView(tv);
		return tr;
	}

	private TableRow makeDefault() {
		File defaultSound = new File("default system");
		TableRow tr = createRow(defaultSound);
		int id = tr.getId();
		defaultId = id;
		sounds.put(id, defaultSound);
		return tr;
	}

	private Button.OnClickListener play = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mp != null) {
				mp.release();
			}
			if (currentId != 0) {
				vg.findViewById(currentId).setBackgroundColor(Const.defaultBGColor);
			}
			currentId = v.getId();
			vg.findViewById(currentId).setBackgroundColor(Color.GRAY);
			if (currentId != defaultId) {
				currentFile = sounds.get(v.getId()).getAbsolutePath();
				mp = MediaPlayer.create(Startup.context, Uri.fromFile(sounds.get(v.getId())));
				mp.setLooping(false);
				int time = mp.getDuration();
				handler.removeCallbacks(abortLoop);
				handler.postDelayed(abortLoop, time);
				try {
					mp.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.release();
					}
				});
			}
		}
	};
	
	private Button.OnClickListener saveListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.d("SOUND", currentFile);
			Startup.prefs.edit().putString("mc.notification.sound", currentFile).commit();
			SettingsMenu.refresh();
			Show.showLast();
		}
	};
	
	private Button.OnClickListener cancelListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			Show.showLast();
		}
	};
	
	private static Runnable abortLoop = new Runnable() {
		@Override
		public void run() {
			mp.release();
		}
	};
	//

}

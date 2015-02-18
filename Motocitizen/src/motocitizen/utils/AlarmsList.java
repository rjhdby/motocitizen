package motocitizen.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import motocitizen.startup.Startup;
import android.annotation.TargetApi;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;

public class AlarmsList {
	private static final String ROOT = "/system/media/audio";

	public static List<File> getList() {
		return getList(5, ROOT);
	}

	public static List<File> getList(int seconds) {
		return getList(seconds, ROOT);
	}

	public static List<File> getList(int seconds, String path) {
		List<File> fileList = new ArrayList<File>();
		File file = new File(path);
		File[] list = file.listFiles();
		for (int i = 0; i < list.length; i++) {
			if (list[i].isDirectory()) {
				fileList.addAll(getList(seconds, list[i].getAbsolutePath()));
			} else if (list[i].isFile() && isAudio(list[i]) && isShort(list[i], seconds)) {
				fileList.add(list[i]);
			}
		}
		return fileList;
	}

	private static boolean isAudio(File file) {
		String[] suffix = new String[] { ".mp3", ".3gp", ".aac", ".ogg", ".vaw" };
		for (String s : suffix) {
			if (file.getName().endsWith(s)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isShort(File file, int seconds) {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
			return isShortNew(file, seconds);
		} else {
			return isShortOld(file, seconds);
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	private static boolean isShortNew(File file, int seconds) {
		try {
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(file.getAbsolutePath());
			String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			retriever.release();
			long sec = Long.parseLong(time) / 1000;
			return sec < seconds;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean isShortOld(File file, int seconds) {
		try {
			MediaPlayer mp = MediaPlayer.create(Startup.context, Uri.fromFile(file));
			int time = mp.getDuration() / 1000;
			Boolean result = time < seconds;
			mp.release();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}

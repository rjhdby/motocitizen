package motocitizen.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaMetadataRetriever;

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
}

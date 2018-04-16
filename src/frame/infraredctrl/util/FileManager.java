package frame.infraredctrl.util;

import java.io.File;

import android.content.Context;

public class FileManager extends FileUtil {
	public FileManager(Context context) {
		super(context);
	}

	// 原图
	public static final String IMAGE_PIC_CACHE_DIR = getDataRootPath() + "Cache/image/";
	// 头像（缩略图）
	public static final String IMAGE_HEAD_CACHE_DIR = getDataRootPath() + "Cache/icon/";
	public static final String FILE_TEMP_DIR = getDataRootPath() + "Temp/";

	// public static final String CSZB_DB_DIR = getDataRootPath() +
	// "Database/";
	// public static final String CSZB_DRAFT_FILE_DIR =
	// getSdCardRootPath() + "CSZB/dfiles/";
	// public static final String CSZB_SAVE_FILE_DIR =
	// getSdCardRootPath() + "CSZB/save/";

	public static File getImageTempFile() {
		return getTempFile(FILE_TEMP_DIR, "image_" + System.currentTimeMillis() + ".jpg");
	}

	// public static File getVideoTempFile() {
	// return getTempFile(FILE_TEMP_DIR, "video_" +
	// System.currentTimeMillis() + ".3gp");
	// }
	//
	// public static File getVoiceTempFile() {
	// return getTempFile(FILE_TEMP_DIR, "voice_" +
	// System.currentTimeMillis() + ".amr");
	// }

	// public static File saveImageDraftFile() {
	// return getTempFile(CSZB_DRAFT_FILE_DIR, "image_" +
	// System.currentTimeMillis() + ".jpg");
	// }

	private static File getTempFile(String dirPath, String name) {
		if (isSdCardExist()) {
			File tempFile = new File(dirPath + name);
			File dir = tempFile.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (tempFile.exists()) {
				tempFile.delete();
			}
			return tempFile;
		}
		return null;
	}

	public static void buildSystemFolders() {
		if (isSdCardExist()) {
			String[] paths = new String[] { IMAGE_PIC_CACHE_DIR, IMAGE_HEAD_CACHE_DIR, FILE_TEMP_DIR };
			for (String path : paths) {
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}
		}
	}
}

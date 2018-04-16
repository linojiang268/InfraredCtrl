package frame.infraredctrl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

public class FileUtil {
	protected Context mContext;

	public FileUtil(Context context) {
		this.mContext = context;
	}

	/**
	 * 判断SD卡是否存在
	 * 
	 * @return
	 */
	public static boolean isSdCardExist() {
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取SD卡可用空间大小（字节）
	 * 
	 * @return
	 */
	public static int getSdCardAvailableSize() {
		File data = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(data.getPath());
		@SuppressWarnings("deprecation")
		int availableBlocks = sf.getAvailableBlocks(); // 可用存储块数量
		// int blockCount = sf.getBlockCount(); //总存储块数量
		@SuppressWarnings("deprecation")
		int size = sf.getBlockSize(); // 存储块大小
		// int totalSize = blockCount * size; //总空间
		return availableBlocks * size; // 可用空间
	}

	/**
	 * 获取SD卡根目录路径
	 * 
	 * @return
	 */
	public static String getSdCardRootPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	}

	/**
	 * 获取外部文件存储位置根目录路径
	 * 
	 * @return
	 */
	public static String getDataRootPath() {
		return getSdCardRootPath() + "Android/data/com.infraredctrl.files/";
	}

	/**
	 * 获取外部存储文件的状态
	 * 
	 * @param fileName
	 *            外部存储数据文件的文件名（包括除根目录外的所有文件路径）
	 * @return 0 文件已存在；1 文件不存在；2 SD卡未插入
	 */
	public static int dataRootFileStatus(String fileName) {
		if (isSdCardExist()) {
			File dataFile = new File(getDataRootPath() + fileName);
			if (dataFile.exists()) {
				return 0;
			} else {
				return 1;
			}
		}
		return 2;
	}

	/**
	 * 清除指定文件夹中的除文件夹外的所有文件
	 * 
	 * @param dirPath
	 */
	public static void clearFolderFiles(String dirPath) {
		File dir = new File(dirPath);
		File[] files = null;
		if (dir.exists() && dir.isDirectory()) {
			files = dir.listFiles();
			for (File file : files) {
				if (!file.isDirectory() && file.exists()) {
					file.delete();
				}
			}
		}
	}

	/**
	 * 清除指定文件夹中的除文件夹外的所有超出修改时间的文件
	 * 
	 * @param dirPath
	 *            文件夹路径
	 * @param delTimeLength
	 *            最大修改时间
	 */
	public static void cleanFolderFiles(String dirPath, long delTimeLength) {
		File dir = new File(dirPath);
		File[] files = null;
		if (dir.exists() && dir.isDirectory()) {
			long timeNow = System.currentTimeMillis();
			files = dir.listFiles();
			for (File file : files) {
				if (!file.isDirectory() && file.exists() && ((timeNow - file.lastModified()) > delTimeLength)) {
					file.delete();
				}
			}
		}
	}

	/**
	 * 清除指定的多个文件家中除文件夹外的所有文件
	 * 
	 * @param dirPaths
	 *            文件夹路径数组
	 */
	public static void clearFoldersFiles(String... dirPaths) {
		for (String dirPath : dirPaths) {
			clearFolderFiles(dirPath);
		}
	}

	/**
	 * 清除指定的多个文件夹中的除文件夹外的所有超出修改时间的文件
	 * 
	 * @param delTimeLength
	 *            最大修改时间
	 * @param dirPaths
	 *            文件夹路径数组
	 */
	public static void cleanFoldersFiles(long delTimeLength, String... dirPaths) {
		for (String dirPath : dirPaths) {
			cleanFolderFiles(dirPath, delTimeLength);
		}
	}

	public static void deleteFile(File file) {
		if (isSdCardExist() && !file.isDirectory() && file.exists()) {
			file.delete();
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param orgFilePath
	 *            原文件路径
	 * @param destFilePath
	 *            目标文件路径
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFile(String orgFilePath, String destFilePath) throws IOException {
		return copyFile(new File(orgFilePath), new File(destFilePath));
	}

	public static boolean copyFileKeepName(File orgFile, String dir) throws IOException {
		return copyFile(orgFile, new File(dir + orgFile.getName()));
	}

	/**
	 * 复制文件
	 * 
	 * @param orgFile
	 *            原文件
	 * @param destFile
	 *            目标文件
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFile(File orgFile, File destFile) throws IOException {
		if (!isSdCardExist() || orgFile.isDirectory() || !orgFile.exists() || destFile.isDirectory()) {
			return false;
		}
		if (destFile.exists()) {
			destFile.delete();
		} else {
			File destDir = destFile.getParentFile();
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
		}
		FileInputStream fis = new FileInputStream(orgFile);
		FileOutputStream fos = new FileOutputStream(destFile);
		int readLen = 0;
		byte[] buf = new byte[1024];
		while ((readLen = fis.read(buf)) != -1) {
			fos.write(buf, 0, readLen);
		}
		fos.flush();
		fos.close();
		fis.close();
		return true;
	}

	/**
	 * 剪切文件
	 * 
	 * @param orgFile
	 *            原文件
	 * @param destFile
	 *            目标文件
	 * @return
	 * @throws IOException
	 */
	public static boolean moveFile(File orgFile, File destFile) throws IOException {
		if (copyFile(orgFile, destFile)) {
			orgFile.delete();
			return true;
		}
		return false;
	}

	/**
	 * 剪切文件
	 * 
	 * @param orgFilePath
	 *            原文件路径
	 * @param destFilePath
	 *            目标文件路径
	 * @return
	 * @throws IOException
	 */
	public static boolean moveFile(String orgFilePath, String destFilePath) throws IOException {
		File orgFile = new File(orgFilePath);
		if (copyFile(orgFile, new File(destFilePath))) {
			orgFile.delete();
			return true;
		}
		return false;
	}

	/**
	 * 存储工程私有文件
	 * 
	 * @param c
	 * @param fileName
	 * @param content
	 * @return
	 */
	public static boolean saveData(Context c, String fileName, String content) {
		try {
			FileOutputStream outStream = c.openFileOutput(fileName, Context.MODE_PRIVATE);
			outStream.write(content.getBytes());
			outStream.flush();
			outStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 读取工程私有文件
	 * 
	 * @param c
	 * @param fileName
	 * @return
	 */
	public static String readData(Context c, String fileName) {
		try {
			FileInputStream inStream = c.openFileInput(fileName);
			if (inStream != null) {
				byte[] data = StreamUtil.readStream(inStream);
				if (data.length > 0) {
					return new String(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 复制工程中资源文件夹Asset中的文件
	 * 
	 * @param c
	 * @param assetFileName
	 *            资源文件名
	 * @param destFile
	 *            目标文件
	 * @return
	 * @throws IOException
	 */
	public static boolean copyAssetFile(Context c, String assetFileName, File destFile) throws IOException {
		if (!isSdCardExist() || assetFileName == null || assetFileName.equals("") || destFile.isDirectory()) {
			return false;
		}
		if (destFile.exists()) {
			destFile.delete();
		} else {
			File destDir = destFile.getParentFile();
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
		}
		AssetManager asset = c.getAssets();
		InputStream input = asset.open(assetFileName);
		FileOutputStream output = new FileOutputStream(destFile);
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = input.read(buffer)) != -1) {
			output.write(buffer, 0, count);
		}
		output.flush();
		output.close();
		input.close();
		// asset.close();
		return true;
	}

	/**
	 * 复制工程中资源文件夹Asset中的文件
	 * 
	 * @param c
	 * @param assetFileName
	 *            资源文件名
	 * @param destFilePath
	 *            目标文件路径
	 * @return
	 * @throws IOException
	 */
	public static boolean copyAssetFile(Context c, String assetFileName, String destFilePath) throws IOException {
		return copyAssetFile(c, assetFileName, new File(destFilePath));
	}

	public static File getFileFromIntentData(Activity a, Intent data) {
		return new File(getFilePathFromIntentData(a, data));
	}

	@SuppressWarnings("deprecation")
	public static String getFilePathFromIntentData(Activity a, Intent data) {
		Uri uri = data.getData();
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = a.managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		return actualimagecursor.getString(actual_image_column_index);
	}
}

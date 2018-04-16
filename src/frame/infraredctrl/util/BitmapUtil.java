package frame.infraredctrl.util;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapUtil {
	public static final int DEFOULT_SIZE = 1000 * 1000;

	/**
	 * 获取工程中图片资源的bitmap
	 * 
	 * @param c
	 * @param bmpId
	 * @return
	 */
	public static Bitmap getLocalResImageBitmap(Context c, int bmpId) {
		return BitmapFactory.decodeResource(c.getResources(), bmpId).copy(Bitmap.Config.ARGB_8888, true);
	}

	/**
	 * * 获取外部图片的bitmap
	 * 
	 * @param imgFilePath
	 *            图片文件路径
	 * @param maxNumOfPixels
	 *            图片的最大像素
	 * @return
	 */
	public static Bitmap getLocalFileImageBitmap(String imgFilePath, int maxNumOfPixels) {
		if (imgFilePath == null || imgFilePath.equals("")) {
			return null;
		}
		return getLocalFileImageBitmap(new File(imgFilePath), maxNumOfPixels);
	}

	/**
	 * 旋转bitmap
	 * 
	 * @param orgBmp
	 *            原bitmap
	 * @param degree
	 *            旋转角度
	 * @return
	 */
	public static Bitmap getRotateBitmap(Bitmap orgBmp, float degree) {
		if (orgBmp != null && !orgBmp.isRecycled()) {
			if (degree % 360f == 0) {
				return Bitmap.createBitmap(orgBmp, 0, 0, orgBmp.getWidth(), orgBmp.getHeight());
			}
			Matrix m = new Matrix();
			m.postRotate(degree);
			return Bitmap.createBitmap(orgBmp, 0, 0, orgBmp.getWidth(), orgBmp.getHeight(), m, true);
		}
		return null;
	}

	/**
	 * 将bitmap保存为图片文件
	 * 
	 * @param destFile
	 *            目标文件
	 * @param orgBmp
	 *            原bitmap
	 * @return
	 */
	public static boolean saveBitmap(File destFile, Bitmap orgBmp, int quality) {
		if (!FileUtil.isSdCardExist()) {
			return false;
		}
		if (orgBmp != null && !orgBmp.isRecycled() && !destFile.isDirectory()) {
			if (destFile.exists()) {
				destFile.delete();
			} else {
				File dir = new File(destFile.getParent());
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}
			try {
				FileOutputStream fOut = new FileOutputStream(destFile);
				boolean b = orgBmp.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
				fOut.flush();
				fOut.close();
				return b;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 获取外部图片的bitmap
	 * 
	 * @param imgFile
	 *            图片文件
	 * @param maxNumOfPixels
	 *            图片的最大像素
	 * @return
	 */
	public static synchronized Bitmap getLocalFileImageBitmap(File imgFile, int maxNumOfPixels) {
		if (imgFile == null || !imgFile.exists()) {
			return null;
		}
		Log.i("getLocalFileImageBitmap", imgFile.getAbsolutePath());
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgFile.getAbsolutePath(), opts);
		opts.inJustDecodeBounds = false;
		if (maxNumOfPixels <= 0) {
			opts.inSampleSize = computeSampleSize(opts, -1, DEFOULT_SIZE);
		} else {
			opts.inSampleSize = computeSampleSize(opts, -1, maxNumOfPixels);
		}
		return BitmapFactory.decodeFile(imgFile.getAbsolutePath(), opts);
	}

	private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

}

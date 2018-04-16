package frame.infraredctrl.util;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

public class ImageCache {
	private Context mContext;

	public ImageCache(Context context) {
		this.mContext = context;
	}

	public Context getContext() {
		return this.mContext;
	}

	// ---------------------------ϵͳͼƬ����---------------------------
	private static final int APP_CACHE_SIZE = 1024 * 1024;
	private static final LruCache<String, Bitmap> mAppCache = new LruCache<String, Bitmap>(APP_CACHE_SIZE) {
		@Override
		protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
			super.entryRemoved(evicted, key, oldValue, newValue);
		}

		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getRowBytes() * value.getHeight();
		}
	};

	// ---------------------------Ӳ����---------------------------
	private final int HARD_CACHE_SIZE = 8 * 1024 * 1024;// 8MӲ����ռ�
	private final LruCache<String, Bitmap> mHardCache = new LruCache<String, Bitmap>(HARD_CACHE_SIZE) {
		@Override
		protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
			// Ӳ���û�������һ�������ʹ�õ�oldvalue���뵽�����û�����
			mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
		}

		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getRowBytes() * value.getHeight();
		}
	};

	// ---------------------------������---------------------------
	private static final int SOFT_CACHE_CAPACITY = 40;
	@SuppressWarnings("serial")
	private static final LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_CAPACITY, 0.75f, true) {
		@Override
		public SoftReference<Bitmap> put(String key, SoftReference<Bitmap> value) {
			return super.put(key, value);
		}

		@Override
		protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
			if (size() > SOFT_CACHE_CAPACITY) {
				return true;
			} else {
				return false;
			}
		}
	};

	public boolean putCache(String key, Bitmap bmp) {
		if (bmp != null) {
			synchronized (mHardCache) {
				mHardCache.put(key, bmp);
			}
			return true;
		} else {
			return false;
		}
	}

	public Bitmap getCache(String key) {
		synchronized (mHardCache) {
			final Bitmap bmp1 = mHardCache.get(key);
			if (bmp1 != null) {
				return bmp1;
			}
		}
		synchronized (mSoftCache) {
			SoftReference<Bitmap> reference = mSoftCache.get(key);
			if (reference != null) {
				final Bitmap bmp = reference.get();
				if (bmp != null) {
					return bmp;
				} else {
					mSoftCache.remove(key);
				}
			}
		}
		return null;
	}

	public Bitmap getAppImageCache(int image_id) {
		if (image_id != -1) {
			String key = String.valueOf(image_id);
			final Bitmap bmp1 = mAppCache.get(key);
			if (bmp1 != null) {
				return bmp1;
			} else {
				Bitmap bmp2 = BitmapFactory.decodeResource(mContext.getResources(), image_id).copy(Bitmap.Config.ARGB_8888, true);
				if (bmp2 != null) {
					mAppCache.put(key, bmp2);
					return bmp2;
				}
			}
		}
		return null;
	}

	// /**
	// * 圆角
	// *
	// * @Title: getAppImageCache
	// * @Description: TODO
	// * @param image_id
	// * @return
	// * @author: ouArea
	// * @return Bitmap
	// * @throws
	// */
	// public Bitmap getAppImageCacheWithRoundCorner(int image_id) {
	// if (image_id != -1) {
	// String key = String.valueOf(image_id) + "_c_r";
	// Bitmap bmp1 = mAppCache.get(key);
	// if (bmp1 != null) {
	// return bmp1;
	// } else {
	// Bitmap bmp2 = BitmapFactory.decodeResource(mContext.getResources(),
	// image_id).copy(Bitmap.Config.ARGB_8888, true);
	// if (null != bmp2) {
	// Bitmap bmp3 = BitmapUtil.toRoundCorner(bmp2, 10);
	// mAppCache.put(key, bmp3);
	// if (null != bmp3) {
	// return bmp3;
	// }
	// }
	// }
	// }
	// return null;
	// }
}

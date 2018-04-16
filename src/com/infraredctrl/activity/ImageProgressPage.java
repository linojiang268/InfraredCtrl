package com.infraredctrl.activity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import frame.infraredctrl.util.BitmapUtil;
import frame.infraredctrl.util.Code;
import frame.infraredctrl.util.FileManager;
import frame.infraredctrl.util.ImageCache;
import frame.infraredctrl.view.FullWithWidthImageView;

public class ImageProgressPage extends Activity implements OnClickListener {
	public static final String IMAGE_FILE_PATH = "IMAGE_FILE_PATH";
	public static final String IMAGE_CAMERA = "IMAGE_CAMERA";
	public static final String IMAGE_CHOICE = "IMAGE_CHOICE";
	public static final String IMAGE_DIALOG = "IMAGE_DIALOG";
	private FullWithWidthImageView mivImage;
	private ImageButton mibtBack;
	private ImageButton mibtRotateLeft;
	private ImageButton mibtRotateRight;
	private ImageButton mibtSave;

	private ProgressDialog mProgressDialog;
	private AlertDialog mPictureDialog;

	private Bitmap mBmpCache = null;
	private File mImgFile = null;
	private boolean choosePic;
	private ImageCache cache;
	private String type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_image_process);
		if (FileManager.isSdCardExist()) {
			this.mProgressDialog = new ProgressDialog(this);
			this.mProgressDialog.setMessage("正在处理...");
			// this.mProgressDialog.setCanceledOnTouchOutside(false);
			// this.mProgressDialog.setCancelable(false);
			this.cache = new ImageCache(ImageProgressPage.this);
			this.mivImage = (FullWithWidthImageView) findViewById(R.id.ivImage);
			this.mibtBack = (ImageButton) findViewById(R.id.ibtBack);
			this.mibtRotateLeft = (ImageButton) findViewById(R.id.ibtRotateLeft);
			this.mibtRotateRight = (ImageButton) findViewById(R.id.ibtRotateRight);
			this.mibtSave = (ImageButton) findViewById(R.id.ibtSave);
			this.mibtBack.setOnClickListener(this);
			this.mibtRotateLeft.setOnClickListener(this);
			this.mibtRotateRight.setOnClickListener(this);
			this.mibtSave.setOnClickListener(this);
			checkIntent();
		} else {
			Toast.makeText(this, "请先插入SD卡", Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	private void checkIntent() {
		Intent intent = getIntent();
		// 显示图像
		if (intent != null && intent.hasExtra(IMAGE_FILE_PATH)) {
			type = IMAGE_FILE_PATH;
			mImgFile = new File(intent.getStringExtra(IMAGE_FILE_PATH));
			new ProgressDealPicture().execute();
		} else if (intent != null && intent.hasExtra(IMAGE_DIALOG)) {
			type = IMAGE_DIALOG;
			// 选择图像（拍照或上传）
			choosePic = false;
			this.mPictureDialog = new AlertDialog.Builder(this).setTitle("选择图片").setItems(new String[] { "拍照", "相册" }, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = null;
					switch (which) {
					case 0:
						choosePic = true;
						mImgFile = FileManager.getImageTempFile();
						intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImgFile));
						startActivityForResult(intent, Code.IMAGE_CAMERA);
						break;
					case 1:
						choosePic = true;
						mImgFile = FileManager.getImageTempFile();
						intent = new Intent(Intent.ACTION_PICK, null);
						intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
						intent.putExtra("crop", "true");// crop=true有这句才能出来最后的裁剪页面.

						intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
						intent.putExtra("aspectY", 1);// x:y=1:2

						intent.putExtra("outputX", 80);
						intent.putExtra("outputY", 80);

						intent.putExtra("output", Uri.fromFile(mImgFile));
						intent.putExtra("outputFormat", "JPEG");// 返回格式JPEG
						startActivityForResult(intent, Code.IMAGE_PICK);
						break;
					default:
						break;
					}
				}
			}).create();
			this.mPictureDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					if (!choosePic) {
						finish();
					}
				}
			});
			this.mPictureDialog.show();
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					h.sendEmptyMessage(0);
				}
			}, 500);
		} else if (intent != null && intent.hasExtra(IMAGE_CAMERA)) {
			type = IMAGE_CAMERA;
			mImgFile = FileManager.getImageTempFile();
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImgFile));
			startActivityForResult(intent, Code.IMAGE_CAMERA);
		} else if (intent != null && intent.hasExtra(IMAGE_CHOICE)) {
			type = IMAGE_CHOICE;
			mImgFile = FileManager.getImageTempFile();
			intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			intent.putExtra("crop", "true");// crop=true有这句才能出来最后的裁剪页面.

			intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
			intent.putExtra("aspectY", 1);// x:y=1:2

			intent.putExtra("outputX", 80);
			intent.putExtra("outputY", 80);

			intent.putExtra("output", Uri.fromFile(mImgFile));
			intent.putExtra("outputFormat", "JPEG");// 返回格式JPEG
			startActivityForResult(intent, Code.IMAGE_PICK);
		} else {
			finish();
		}
	}

	private Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mivImage.setImageBitmap(cache.getAppImageCache(R.drawable.image_default));
		}

	};

	protected void onResume() {
		super.onResume();
		StatService.onResume(ImageProgressPage.this);
		if (mImgFile != null && mImgFile.exists()) {
			new ProgressDealPicture().execute();
		}
	};

	public void onPause() {
		StatService.onPause(ImageProgressPage.this);
		recycleCache();
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			finish();
		} else {
			if (requestCode == Code.IMAGE_CAMERA) {
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(Uri.fromFile(mImgFile), "image/*");
				intent.putExtra("crop", "true");// crop=true有这句才能出来最后的裁剪页面.

				intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
				intent.putExtra("aspectY", 1);// x:y=1:2

				intent.putExtra("outputX", 80);
				intent.putExtra("outputY", 80);

				intent.putExtra("output", Uri.fromFile(mImgFile));
				intent.putExtra("outputFormat", "JPEG");// 返回格式JPEG
				startActivityForResult(intent, Code.IMAGE_PICK);
			}
			// if (requestCode == Code.IMAGE_PICK) {
			// mImgFile = FileManager.getFileFromIntentData(this,
			// data);
			// }
			new ProgressDealPicture().execute();
		}
	}

	/**
	 * 加载file到bitmap
	 * 
	 * @ClassName ProgressDealPicture
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-1-7
	 */
	private class ProgressDealPicture extends AsyncTask<Void, Void, Boolean> {
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (mImgFile != null && mImgFile.exists()) {
				mBmpCache = BitmapUtil.getLocalFileImageBitmap(mImgFile, -1);
				return mBmpCache != null;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (result) {
				mivImage.setImageBitmap(mBmpCache);
			} else {
				finish();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtBack:
			if (type.equals(IMAGE_FILE_PATH)) {
				finish();
			} else {
				setResult(RESULT_CANCELED);
				finish();
			}
			break;
		case R.id.ibtRotateLeft:
			new ProgressRotate().execute(-90f);
			break;
		case R.id.ibtRotateRight:
			new ProgressRotate().execute(90f);
			break;
		case R.id.ibtSave:
			if (type.equals(IMAGE_FILE_PATH)) {
				finish();
			} else {
				new ProgressSave().execute();
			}
			break;
		}
	}

	private void recycleCache() {
		mivImage.setImageBitmap(null);
		if (mBmpCache != null && !mBmpCache.isRecycled()) {
			mBmpCache.recycle();
			System.gc();
		}
		mBmpCache = null;
	}

	/**
	 * 图像旋转
	 * 
	 * @ClassName ProgressRotate
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-1-7
	 */
	private final class ProgressRotate extends AsyncTask<Float, Void, Bitmap> {
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(Float... params) {
			float degree = params[0];
			return BitmapUtil.getRotateBitmap(mBmpCache, degree);
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				recycleCache();
				mBmpCache = result;
				mivImage.setImageBitmap(result);
			}
		}
	}

	private final class ProgressSave extends AsyncTask<Void, Void, Intent> {
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected Intent doInBackground(Void... params) {
			if (mBmpCache != null && !mBmpCache.isRecycled()) {
				mImgFile = FileManager.getImageTempFile();
				if (BitmapUtil.saveBitmap(mImgFile, mBmpCache, 90)) {
					Intent intent = new Intent();
					intent.putExtra(IMAGE_FILE_PATH, mImgFile.getPath());
					Log.i("ImageProgress", "image size: " + mImgFile.length() / 1024 + "k");
					return intent;
				}
			}
			return null;
		}

		protected void onPostExecute(Intent result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (result != null) {
				mivImage.setImageBitmap(null);
				setResult(RESULT_OK, result);
			} else {
				Toast.makeText(getApplicationContext(), "图片保存失败", Toast.LENGTH_SHORT).show();
			}
			finish();
		}
	}
}
